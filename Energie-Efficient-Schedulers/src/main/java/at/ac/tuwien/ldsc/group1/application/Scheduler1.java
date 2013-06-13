package at.ac.tuwien.ldsc.group1.application;

import at.ac.tuwien.ldsc.group1.domain.CloudOverallInfo;
import at.ac.tuwien.ldsc.group1.domain.CloudStateInfo;
import at.ac.tuwien.ldsc.group1.domain.Event;
import at.ac.tuwien.ldsc.group1.domain.EventType;
import at.ac.tuwien.ldsc.group1.domain.components.Application;
import at.ac.tuwien.ldsc.group1.domain.components.Machine;
import at.ac.tuwien.ldsc.group1.domain.components.PhysicalMachine;
import at.ac.tuwien.ldsc.group1.domain.components.PhysicalMachineImpl;
import at.ac.tuwien.ldsc.group1.domain.components.VirtualMachine;
import at.ac.tuwien.ldsc.group1.domain.components.VirtualMachineImpl;
import at.ac.tuwien.ldsc.group1.domain.exceptions.ResourceUnavailableException;
import at.ac.tuwien.ldsc.group1.domain.exceptions.SchedulingNotPossibleException;
import at.ac.tuwien.ldsc.group1.domain.federation.FederationPartner;
import at.ac.tuwien.ldsc.group1.domain.federation.ScenarioType;
import com.google.common.collect.TreeMultiset;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.ResourceBundle;
import java.util.Set;

public class Scheduler1 implements Scheduler {

	private final double MAGICPROPORTION = 4400/1900;
    private int maxPMs;
    private int currentPms = 0;
    private long internalTime = 0L;
    long lastInternalTime = -1L;
    double lastTotalConsumption = 0;

    //Use maps to map VM --> PM and App --> VM
    private Map<VirtualMachine, PhysicalMachine> pmAllocations;
    private Map<Application, VirtualMachine> appAllocations = new Hashtable<>();
    private Queue<Application> queuedApplications = new LinkedList<>();

    private Integer VmRamBase;
    private Integer VmHddBase;
    private Integer VmCpuInMhzBase;
    private boolean eventHandled = false;

    @Resource(name = "scenarioWriter1") CsvWriter scenarioWriter;

    private final CloudOverallInfo overallInfo = new CloudOverallInfo();
    private TreeMultiset<Event> events;

    private List<FederationPartner> partnerList;

    public Scheduler1(int maxPMs) {
        this.maxPMs = maxPMs;
        //TODO not here
        setNumberOfFederationPartners(1);

        ResourceBundle res = ResourceBundle.getBundle("virtualMachine");
        VmRamBase = Integer.parseInt(res.getString("ramBase"));
        VmHddBase = Integer.parseInt(res.getString("sizeBase"));
        VmCpuInMhzBase = Integer.parseInt(res.getString("cpuBase"));
    }

    @Override
    public void schedule(Event event) {
        Application application = event.getApplication();
        if (event.getEventType() == EventType.START) {
            try {
                //Handle logging
                if(event.getEventTime() != internalTime && eventHandled)
                    this.writeLog();
                //Add Application
                eventHandled = false;
                this.addApplication(application);
                application.start();
                updateEventTime(event);
                eventHandled = true;

                //Add stop event
                events.add(new Event(event.getEventTime() + application.getDuration(), EventType.STOP, application));
            } catch (ResourceUnavailableException e) {
                e.printErrorMsg();
            } catch (SchedulingNotPossibleException e) {
                System.out.println("[" + internalTime + "/" + event.getEventTime() + "] Application delayed...");
                //TODO
                //instead of delaying try to add application to cloudpartner
                boolean isDeployedInfederation = false;
                for(FederationPartner f: partnerList){
                	isDeployedInfederation = f.deploySourceOutApplication(application);
                }

                if(isDeployedInfederation){
                	//TODO count sourceout
                }else{

                	queuedApplications.add(application);
                }
            }
        } else {
            if(event.getEventTime() != internalTime && eventHandled)
                this.writeLog();
            this.removeApplication(application);
            application.stop();
            updateEventTime(event);
            eventHandled = true;
            Application nextApplication = queuedApplications.poll();
            if (nextApplication != null) {
                long startTime = internalTime;
                events.add(new Event(startTime, EventType.START, nextApplication));
            }
        }
    }

    private void updateEventTime(Event event) {
        lastInternalTime = internalTime;
        internalTime = event.getEventTime();
    }

    @Override
    public void handleEvents(TreeMultiset<Event> events) {
        if(maxPMs <= 0)
            throw new RuntimeException("The cloud does not contain any physical machines");
        this.events = events;
        while (events.size() > 0) {
            Iterator<Event> iterator = events.iterator();
            Event event = iterator.next();
            iterator.remove();
            schedule(event);
        }
        System.out.println("Number of queued applications:" + queuedApplications.size());
        /* TODO: check if queue still contains some applications and schedule them
                  It might be possible that the queue still contains some applications which have not been
                  executed yet. Simply calling another loop at this point, could possibly introduce an endless loop.
                  We need to also consider the case that there are applications which are too large to run on any
                  physical machine (even if its empty).*/
    }

    @Override
    public PhysicalMachine addApplication(Application application) throws ResourceUnavailableException, SchedulingNotPossibleException {
        //1. Find a physical machine which can host this application
        int neededRam = application.getRam() + this.VmRamBase;
        int neededHddSize = application.getHddSize() + this.VmHddBase;
        int neededCpuInMHz = application.getCpuInMhz() + this.VmCpuInMhzBase;
        PhysicalMachine pm = selectOptimalPM(neededRam, neededHddSize, neededCpuInMHz);
        //2. This is the first scenario, so we create one virtual machine per application
        VirtualMachine vm = new VirtualMachineImpl(pm);
        pmAllocations.put(vm, pm);

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
        return pm;
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
                assert (!appAllocations.containsValue(currentVm));

                //3. Kill PM if not needed anymore (we just removed the last VM from it)
                PhysicalMachine currentPm = pmAllocations.remove(currentVm);
                if (currentPm != null && (currentPm.getComponents() == null || currentPm.getComponents().isEmpty())) {
                    currentPm.stop();
                    currentPms--;
                    pmAllocations.remove(currentPm);
                }
            }
        } else {
            System.out.println("How come app is running on no virtual machine?");
            throw new RuntimeException("Unexpected scheduler state");
        }
    }

    private PhysicalMachine selectOptimalPM(Integer neededRam, Integer neededHddSize, Integer neededCpuInMHz) throws SchedulingNotPossibleException {



        if (this.pmAllocations == null) {
            this.pmAllocations = new Hashtable<>();
            PhysicalMachine pm = createNewPM();
            pm.start();
            overallInfo.setTotalPMs(overallInfo.getTotalPMs() + 1);
            return pm;
        } else {

        	if(neededRam/neededCpuInMHz > MAGICPROPORTION){

        		PhysicalMachine pm = selectPMwithMoreRAMProportion(neededRam, neededHddSize, neededCpuInMHz);
        		if(pm != null) return pm;

        	}else{

        		PhysicalMachine pm = selectPMwithMoreCPUProportion(neededRam, neededHddSize, neededCpuInMHz);
        		if(pm != null) return pm;

        	}

        	PhysicalMachine firstPossiblePM = selectFirstPossiblePM(neededRam, neededHddSize, neededCpuInMHz);
        	if(firstPossiblePM != null) return firstPossiblePM;

            //list iterated and no pm could give back -> start new pm
            PhysicalMachine pm = createNewPM();
            pm.start();
            overallInfo.setTotalPMs(overallInfo.getTotalPMs() + 1);
            return pm;
        }
    }

    private PhysicalMachine selectPMwithMoreCPUProportion(Integer neededRam, Integer neededHddSize, Integer neededCpuInMHz) {
    	for (PhysicalMachine pm : this.pmAllocations.values()) {
    		if (pm.getCpuAvailable() >= neededCpuInMHz &&
    				pm.getRamAvailable() >= neededRam &&
    				pm.getHddAvailable() >= neededHddSize &&
    				pm.getRamAvailable()/pm.getCpuAvailable() > MAGICPROPORTION) {
    			return pm;
    		}
    	}
    	return null;
    }

	private PhysicalMachine selectPMwithMoreRAMProportion(Integer neededRam, Integer neededHddSize, Integer neededCpuInMHz) {
		for (PhysicalMachine pm : this.pmAllocations.values()) {
    		if (pm.getCpuAvailable() >= neededCpuInMHz &&
    				pm.getRamAvailable() >= neededRam &&
    				pm.getHddAvailable() >= neededHddSize &&
    				pm.getRamAvailable()/pm.getCpuAvailable() < MAGICPROPORTION) {
    			return pm;
    		}
    	}
    	return null;
	}

	//iterate over PMList give back first possible
    private PhysicalMachine selectFirstPossiblePM(int neededRam,int neededHddSize,int neededCpuInMHz){
    	 for (PhysicalMachine pm : this.pmAllocations.values()) {
             if (pm.getCpuAvailable() >= neededCpuInMHz &&
                     pm.getRamAvailable() >= neededRam &&
                     pm.getHddAvailable() >= neededHddSize) {
                 return pm;
             }
         }
    	 return null;
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
        int runningVMs = 0;
        double totalPowerConsumption = 0;
        int inSourced = 0;        //TODO
        int outSourced = 0;        //TODO

        timestamp = (int) internalTime;
        //Note that the pmAllocations map can contain each PM several times, thus we need to create a set from it first
        Set<PhysicalMachine> pms = new HashSet<>(pmAllocations.values());
        for (Machine pm : pms) {
            totalRAM += pm.getRam();
            totalCPU += pm.getCpuInMhz();
            totalSize += pm.getHddSize();
            runningVMs += pm.getComponents().size();
            //this consumption is the overall powerConsumption of the cloud in the moment
            totalPowerConsumption += pm.getPowerConsumption();
        }

        CloudStateInfo info = new CloudStateInfo(timestamp, totalRAM, totalCPU, totalSize, currentPms, runningVMs, totalPowerConsumption, inSourced, outSourced);
        this.updatePowerConsumption(lastTotalConsumption);
        lastTotalConsumption = totalPowerConsumption;
        this.scenarioWriter.writeLine(info);
    }


    private void updatePowerConsumption(double lastTotalConsumption) {
        //total consumption after the previous event * time interval between last and new event in seconds
        this.overallInfo.setTotalPowerConsumption(lastTotalConsumption * (lastInternalTime / 1000));
    }

    @Override
    public void finalize() {
        this.scenarioWriter.close();
    }

    @Override
    public CloudOverallInfo getOverAllInfo() {
        overallInfo.setScheduler(this.getClass().getSimpleName());
        overallInfo.setTotalDuration(internalTime);
        return this.overallInfo;
    }

    @Override
    public void setMaxNumberOfPhysicalMachines(int nr) {
        this.maxPMs = nr;
    }

    @Override
	public void setNumberOfFederationPartners(int nr) {
		//todo init federationPartners
		partnerList = new ArrayList<>();
		for(int i = 0 ; i < nr; i++){
			FederationPartner partner = new FederationPartner();
			partnerList.add(partner);
		}

	}
}
