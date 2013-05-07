package at.ac.tuwien.ldsc.group1.application;

import at.ac.tuwien.ldsc.group1.domain.CloudOverallInfo;
import at.ac.tuwien.ldsc.group1.domain.CloudStateInfo;
import at.ac.tuwien.ldsc.group1.domain.Event;
import at.ac.tuwien.ldsc.group1.domain.EventType;
import at.ac.tuwien.ldsc.group1.domain.components.*;
import at.ac.tuwien.ldsc.group1.domain.exceptions.ResourceUnavailableException;
import at.ac.tuwien.ldsc.group1.domain.exceptions.SchedulingNotPossibleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.*;

public class Scheduler1 implements Scheduler {

    private int maxPMs;
    private int currentPms = 0;
    private long internalTime = 0L;
    long lastinternalTime = 0L;
    double lastTotalCosumption = 0;

    //Use maps to map VM --> PM and App --> VM
    private Map<VirtualMachine, PhysicalMachine> vmAllocations;
    private Map<Application, VirtualMachine> appAllocations = new Hashtable<>();

    private Integer VmRamBase;
    private Integer VmHddBase;
    private Integer VmCpuInMhzBase;

    @Autowired
    @Qualifier("scenarioWriter")
    CsvWriter scenarioWriter;

    private Event lastEvent = null;

    private final CloudOverallInfo overallInfo = new CloudOverallInfo();
    private Set<Event> events;

    public Scheduler1() {
        ResourceBundle res = ResourceBundle.getBundle("virtualMachine");
        VmRamBase = Integer.parseInt(res.getString("ramBase"));
        VmHddBase = Integer.parseInt(res.getString("sizeBase"));
        VmCpuInMhzBase = Integer.parseInt(res.getString("cpuBase"));
    }

    @Override
    public void schedule(Event event) throws SchedulingNotPossibleException {
        if (event.getEventType() == EventType.START) {
            try {
                this.addApplication(event.getApplication());
            } catch (ResourceUnavailableException e) {
                e.printErrorMsg();
                throw new SchedulingNotPossibleException();
            }
        } else {
            this.removeApplication(event.getApplication());
        }

        long previousTimeStamp = 0L;
        if (lastEvent != null) {
            previousTimeStamp = lastEvent.getEventTime();
        }
        if (event.getEventTime() - previousTimeStamp > 0) {
            lastinternalTime = internalTime;
            internalTime = internalTime + (event.getEventTime() - previousTimeStamp);
        } // else leave internal time as it is, the entire time scale will be shifted

        this.writeLog();
        this.lastEvent = event;
    }

    @Override
    public void callScheduling(Set<Event> events) {
        this.events = events;
        for (Event e : events) {
            if (!e.isToBeSkipped()) handleEvent(e);
        }
    }

    private void handleEvent(Event event) {
        try {
            this.schedule(event);
        } catch (SchedulingNotPossibleException e) {
            Event stopEvent = getNextStopEvent(event);
            //schedule stop
            this.handleEvent(stopEvent);
            //remove this stop event from the event list
            stopEvent.setToBeSkipped(true);
            //schedule original event
            this.handleEvent(event);

        }
    }

    @Override
    public void addApplication(Application application) throws ResourceUnavailableException, SchedulingNotPossibleException {
        //1. Find a physical machine which can host this application
        Integer neededRam = application.getRam() + this.VmRamBase;
        Integer neededHddSize = application.getHddSize() + this.VmHddBase;
        Integer neededCpuInMHz = application.getCpuInMhz() + this.VmCpuInMhzBase;
        PhysicalMachine pm = selectOptimalPM(neededRam, neededHddSize, neededCpuInMHz);
        //2. This is the first scenario, so we create one virtual machine per application
        VirtualMachine vm = new VirtualMachineImpl(pm);
        vmAllocations.put(vm, pm);

        //Try to allocate resources and start the VM
        try {
            vm.addComponent(application); //resources are allocated inside this method
            vm.start();
            overallInfo.setTotalVMs(overallInfo.getTotalVMs() + 1);
        } catch (ResourceUnavailableException e) {
            e.printResourceAllocationErrorLog(pm, vm, neededCpuInMHz, neededHddSize, neededRam);
        }

        //if everything worked, we add the (app, vm) tuple to the map of applications
        appAllocations.put(application, vm);
    }

    @Override
    public void removeApplication(Application application) {
        //1. find the virtual machine on which this application runs
        //   and remove it.
        VirtualMachine currentVm = appAllocations.remove(application);
        if (currentVm != null) {
            currentVm.removeComponent(application);     // free resources inside this method
            //2. Kill VM if not needed anymore (we just removed the last app from it)
            if (currentVm.getComponents() == null || currentVm.getComponents().isEmpty()) {
                currentVm.stop(); //this also removes this VM from its parent
                // if there are no applications running on this VM then it implies that appAllocations does not
                // contain the currentVM
                assert(!appAllocations.containsValue(currentVm));

                //3. Kill PM if not needed anymore (we just removed the last VM from it)
                PhysicalMachine currentPm = vmAllocations.remove(currentVm);
                if (currentPm != null && (currentPm.getComponents() == null || currentPm.getComponents().isEmpty())) {
                    currentPm.stop();
                    vmAllocations.remove(currentPm);
                }
            }
        } else {
            System.out.println("How come app is running on no virtual machine?");
            throw new RuntimeException("Unexpected scheduler state");
        }
    }

    private PhysicalMachine selectOptimalPM(Integer neededRam, Integer neededHddSize, Integer neededCpuInMHz) throws SchedulingNotPossibleException {
        if (this.vmAllocations == null) {
            this.vmAllocations = new Hashtable<>();
            PhysicalMachine pm = createNewPM();
            pm.start(); //TODO start method is empty --> Count Initial Power Consumption there?
            overallInfo.setTotalPMs(overallInfo.getTotalPMs() + 1);
            return pm;
        } else {
            //iterate over PMList give back first possible
            //TODO this finds the first pm that has enough space, use a more efficient heuristic to find a pm???
            for (PhysicalMachine pm : this.vmAllocations.values()) {
                if (pm.getCpuAvailable() >= neededCpuInMHz &&
                        pm.getRamAvailable() >= neededRam &&
                        pm.getHddAvailable() >= neededHddSize) {
                    return pm;
                }
            }
            //list iterated and no pm could give back -> start new pm
            PhysicalMachine pm = createNewPM();
            pm.start();
            overallInfo.setTotalPMs(overallInfo.getTotalPMs() + 1);
            return pm;
        }
    }


    private PhysicalMachine createNewPM() throws SchedulingNotPossibleException {
        if (this.currentPms < maxPMs) {
            this.currentPms++;
            return new PhysicalMachineImpl();
        } else {
            throw new SchedulingNotPossibleException();
        }
    }

    private void writeLog() {
        int timestamp;
        int totalRAM = 0;
        int totalCPU = 0;
        int totalSize = 0;
        int runningPMs;
        int runningVMs = 0;
        double totalPowerConsumption = 0;
        int inSourced = 0;        //TODO
        int outSourced = 0;        //TODO

        timestamp = (int) internalTime;
        //Note that the vmAllocations map can contain each PM several times, thus we need to create a set from it first
        Set<PhysicalMachine> pms = new HashSet<>(vmAllocations.values());
        for (Machine pm : pms) {
            totalRAM += pm.getRamAvailable();
            totalCPU += pm.getCpuAvailable();
            totalSize += pm.getHddAvailable();
            runningVMs += pm.getComponents().size();
            //this consumption is the overall powerconsuption of the cloud in the moment
            totalPowerConsumption += pm.getPowerConsumption();
        }

        CloudStateInfo info = new CloudStateInfo(timestamp, totalRAM, totalCPU, totalSize, currentPms, runningVMs, totalPowerConsumption, inSourced, outSourced);
        this.updatePowerConsumption(lastTotalCosumption);
        lastTotalCosumption = totalPowerConsumption;
        this.scenarioWriter.writeLine(info);
    }


    private void updatePowerConsumption(double lastTotalCosumption) {
        //total consumption after the previous event * time interval between last and new event in seconds
        this.overallInfo.setTotalPowerConsumption(lastTotalCosumption*(lastinternalTime/1000));
    }

    @Override
    public void finalize() {
        this.scenarioWriter.close();
    }

    @Override
    public CloudOverallInfo getOverAllInfo(){
        overallInfo.setScheduler(this.getClass().getName());
        overallInfo.setTotalDuration(internalTime);
        return this.overallInfo;
    }

    @Override
    public void setMaxNumberOfPhysicalMachines(int nr) {
        this.maxPMs = nr;

    }

    private Event getNextStopEvent(Event event) {
        for (Event e : events) {
            if (e.getEventTime() > event.getEventTime() && e.getEventType().equals(EventType.STOP) && appAllocations.containsKey(e.getApplication()) && !e.isToBeSkipped()) {
                return e;
            }
        }
        System.out.println("Cloud is full and no App can be stopped.");
        return null;
    }

}
