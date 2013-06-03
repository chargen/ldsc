package at.ac.tuwien.ldsc.group1.application;

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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import at.ac.tuwien.ldsc.group1.domain.CloudOverallInfo;
import at.ac.tuwien.ldsc.group1.domain.CloudStateInfo;
import at.ac.tuwien.ldsc.group1.domain.Event;
import at.ac.tuwien.ldsc.group1.domain.EventToPm;
import at.ac.tuwien.ldsc.group1.domain.EventType;
import at.ac.tuwien.ldsc.group1.domain.components.Application;
import at.ac.tuwien.ldsc.group1.domain.components.Component;
import at.ac.tuwien.ldsc.group1.domain.components.Machine;
import at.ac.tuwien.ldsc.group1.domain.components.PhysicalMachine;
import at.ac.tuwien.ldsc.group1.domain.components.PhysicalMachineImpl;
import at.ac.tuwien.ldsc.group1.domain.components.VirtualMachine;
import at.ac.tuwien.ldsc.group1.domain.components.VirtualMachineImpl;
import at.ac.tuwien.ldsc.group1.domain.exceptions.ResourceUnavailableException;
import at.ac.tuwien.ldsc.group1.domain.exceptions.SchedulingNotPossibleException;

import com.google.common.collect.TreeMultiset;

/** Scheduler3:
 *  Our idea is to make a Scheduler, which can overprovide Resources on a PM.
 *  It works as follows: When a new application arrives, we check on the PM with the lowest
 *  utilization if it is good to run that application on that PM. If the application is too big
 *  (more than 15% overprovisioning - without VM base consumption) we doesn't start it and we put it in our queue.
 *  In order to do that we create a penalty list where we save the state how much we overprovide.
 *  Each application running on the PM needs to run slower in relation to the overprovisioning.
 *  We only overprovide RAM and CPU
 *  
 *  Initial State: All PMs switched off. Create for every application that arrives an own VM with the 
 *  parameters of the application (size, CPU and RAM). Fill PMs with VMs until they are full. If the PM cannot 
 *  provide any more resources, check how much we need to overprovide. If it is more than 15% of the maximum value,
 *  start a new PM, otherwise create a new VM on that PM and let all applications on that PM run 15% slower.
 *  Kill a VM if you don’t need it anymore (no applications running on it), kill a PM if there are no VMs running
 *  on it. Try to find an efficient way to decide on which PM you start a new VM.
 *  
 */
public class Scheduler3 implements Scheduler {

	private final double ramThreshold = 1.15;
	private final double cpuThreshold = 1.15;
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
    private boolean overProvide = false;
    private double penalty = 0.0;

    @Autowired
    @Qualifier("scenarioWriter3")
    CsvWriter scenarioWriter;

    private final CloudOverallInfo overallInfo = new CloudOverallInfo();
    private TreeMultiset<Event> events;
   // private Map<PhysicalMachine, List<Event>> events;

    private List<EventToPm> assoc = new ArrayList<EventToPm>();
    
    public Scheduler3(int maxPMs) {
        this.maxPMs = maxPMs;
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
                PhysicalMachine pm = this.addApplication(application);
                application.start();
                updateEventTime(event);
                eventHandled = true;
                
                //Add stop event
                Event e = new Event(event.getEventTime() + application.getDuration(), EventType.STOP, application);
                events.add(e);
                
                EventToPm foundeventToPm = null;
                for(EventToPm eventToPM : assoc){
                    if(eventToPM.getPm().equals(pm)) foundeventToPm = eventToPM;
                }
                if(foundeventToPm == null) {
                    foundeventToPm =  new EventToPm();
                    foundeventToPm.setPm(pm);
                }
                
                foundeventToPm.addEvent(e);
                if(!assoc.contains(foundeventToPm)) {
                    System.out.println("Found Event to Pm");
                    assoc.add(foundeventToPm);
                }
                //EventToPm.addeventpm(pm, event);
                
            } catch (ResourceUnavailableException e) {
                e.printErrorMsg();
            } catch (SchedulingNotPossibleException e) {
                System.out.println("[" + internalTime + "/" + event.getEventTime() + "] Application delayed...");
                queuedApplications.add(application);
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
                // TODO: what if SchedulingNotPossible happens here?
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
        System.out.println(" ###########################################################################################################################################");
        System.out.println(" #########################################                           Scheduler 3                            ################################");
        System.out.println(" ###########################################################################################################################################");
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
        //2. This is the third scenario, so we create one virtual machine per application
        VirtualMachine vm = null;

        //Try to allocate resources and start the VM
        try {
        	if (overProvide == true) {
        	    System.out.println("We slow down pm speed now");
        		// Application needs to overprovide PM resources
        		// 1st add resources and check how much this pm needs to overprovide and save to penalty
        		// 2nd let all applications run slower on that pm until a new stop event arrives on that pm
	            pm = slowDownPmSpeed(pm, calculateOverprovidePm(pm, neededRam, neededCpuInMHz));
	  //TODO SOMETHINGS WRONG HERE, VM DOESNT PROVIDE NECESSARY RESOURCES ALTHOUGH PM SIZE INCREASED
        		System.out.println("PM with MaxCPU: " + pm.getCpuInMhzMax());
	            vm = new VirtualMachineImpl(pm);
	            System.out.println("VM CPU avail: " + vm.getCpuAvailable());
	            pmAllocations.put(vm, pm);
	            
        		vm.addOverProvidedComponent(application); //resources are allocated inside this method
        		vm.start();
	            overallInfo.setTotalVMs(overallInfo.getTotalVMs() + 1);
	            overProvide = false;
        	} else {
                System.out.println("Normal pm workflow");
        	    vm = new VirtualMachineImpl(pm);
                pmAllocations.put(vm, pm);
                
	            vm.addComponent(application); //resources are allocated inside this method
	            vm.start();
	            overallInfo.setTotalVMs(overallInfo.getTotalVMs() + 1);
        	}
        } catch (ResourceUnavailableException e) {
            e.printResourceAllocationErrorLog(pm, vm, neededCpuInMHz, neededHddSize, neededRam);
        }

        //if everything worked, we add the (app, vm) tuple to the map of applications
        appAllocations.put(application, vm);
        return pm;
    }

    private PhysicalMachine slowDownPmSpeed(PhysicalMachine pm, double pmPenalty) {
        
        EventToPm foundEventToPm = null;
        for(EventToPm eventToPm : assoc ){
            if(eventToPm.getPm().equals(pm)) foundEventToPm = eventToPm;
        }
        
        if(foundEventToPm == null) System.out.println("EventToPm not found");
        
        // modify PM SIZE that it FITS
        foundEventToPm.getPm().setOverprovidedRam((int) (foundEventToPm.getPm().getRamMax() * ramThreshold));
        foundEventToPm.getPm().setOverprovidedCpuInMhz((int) (foundEventToPm.getPm().getCpuInMhzMax() * cpuThreshold));
        
        for(Event e : foundEventToPm.getEvents()){
            //TODO use some other datastructure if this will be slow
            for(Event event : events){
                // delay it
            	//internalTime = lastInternalTime = 60000ms
            	//penalty = 1.1
            	//newEventTime  = 60000 * 0.1 = 6000ms
            	int xPenalty = 0; //6000ms
                if(event.equals(e)) event.setEventTime((long) (event.getEventTime() + xPenalty));
                events.add(event);
                
            }
        }
        return foundEventToPm.getPm();
//		// go through all applications of this pm and delay stop events
	}

	private double calculateOverprovidePm(PhysicalMachine pm, int neededRam,
			int neededCpuInMHz) {
	    // Calculate overprovisioning factor below threshold. 
		// calculate penalty with given amount / total amount * 100
		double tempCpuPenalty = ((neededCpuInMHz - pm.getCpuAvailable()) / pm.getCpuInMhzMax()) * 100;
		double tempRamPenalty = ((neededRam - pm.getRamAvailable()) / pm.getRamMax()) * 100;
		double tempPenalty = (((tempCpuPenalty + tempRamPenalty) / 2) / 100) + 1;
		// check if penalty is not null
		if (penalty == 0) {
			// first time
			
			// get average of these and format it to 1,
			penalty = tempPenalty;
		} else {
			// add more penalty
			if (penalty < tempPenalty) {
				// previous penalty was less than penalty now, we need to add more penalty
				
				double difference = tempPenalty - penalty;
				penalty = difference + 1; // e.g. 1,15 - 1,10 = 0,05, 0,05 + 1 = 1,05 to add
			} else {
				// penalty reduced ??? shouldnt happen here, but at recalculateOverprovidedPm function
			    assert (false);
			}
			
		}
		return penalty;
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
                // Application removed, pm not killed, recalculate overprovisioning
                if (currentPm != null && (currentPm.getComponents() != null || !currentPm.getComponents().isEmpty())) {
                	adjustPmSpeed(currentPm, recalculateOverprovidedPm(currentPm, application));
                }
            }
        } else {
            System.out.println("How come app is running on no virtual machine?");
            throw new RuntimeException("Unexpected scheduler state");
        }
    }

    private void adjustPmSpeed(PhysicalMachine currentPm,
			double recalculateOverprovidedPm) {
        EventToPm foundEventToPm = null;
        for(EventToPm eventToPm : assoc ){
            if(eventToPm.getPm().equals(currentPm)) foundEventToPm = eventToPm;
        }
        
        if(foundEventToPm == null) System.out.println("EventToPm not found");
        
        if (recalculateOverprovidedPm == 0) {
            // Revert everything
            foundEventToPm.getPm().revertOverprovidedCpuInMhz();
            foundEventToPm.getPm().revertOverprovidedRam();
            for(Event e : foundEventToPm.getEvents()){
                //TODO use some other datastructure if this will be slow
                for(Event event : events){
                    // stop delay
                    if(event.equals(e)) event.setEventTime((long) (event.getEventTime() / penalty));
                }
            }
            penalty = 0;
        } else {
            // Adjust Pm Speed according to new calculated Pm, we already overprovided PM size, no need to redo it, just delay the stop events
            
            for(Event e : foundEventToPm.getEvents()){
                //TODO use some other datastructure if this will be slow
                for(Event event : events){
                    // stop delay and add new
                    if(event.equals(e)) event.setEventTime((long) ((event.getEventTime() / penalty) * recalculateOverprovidedPm));
                }
            }
            penalty = recalculateOverprovidedPm;
        }
        
    }

	private double recalculateOverprovidedPm(PhysicalMachine currentPm, Application application) {
	   //1. find out how much free space this application gives us
        int neededRam = application.getRam() + this.VmRamBase;
        int neededCpuInMHz = application.getCpuInMhz() + this.VmCpuInMhzBase;
        
        if ((currentPm.getCpuInMhzMax() - currentPm.getOverprovidedCpuInMhz()) - neededCpuInMHz > 0 || (currentPm.getRamMax() - currentPm.getOverprovidedRam()) - neededRam > 0) {
            // Max - Provided - needed = what is still needed to overprovide. / total amount (max of PM) = percentage of overprovisioning
            double tempCpuPenalty = (((currentPm.getCpuInMhzMax() - currentPm.getOverprovidedCpuInMhz()) - neededCpuInMHz) / currentPm.getOverprovidedCpuInMhz()) * 100;
            double tempRamPenalty = (((currentPm.getRamMax() - currentPm.getOverprovidedRam()) - neededRam) / currentPm.getOverprovidedRam()) * 100;
            double tempPenalty = (((tempCpuPenalty + tempRamPenalty) / 2) / 100) + 1;
            
            return tempPenalty;
        } else {
            // we dont need to overprovide any more, revert everything
            return 0;
        }
        
    	// recalculate delay of applications
	}

	private PhysicalMachine selectOptimalPM(Integer neededRam, Integer neededHddSize, Integer neededCpuInMHz) throws SchedulingNotPossibleException {
        
    	if (this.pmAllocations == null) {
            this.pmAllocations = new Hashtable<>();
            PhysicalMachine pm = createNewPM();
            pm.start();
            overallInfo.setTotalPMs(overallInfo.getTotalPMs() + 1);
            return pm;
        } else {
        	// Select PM with lowest utilization in relation to CPU / RAM:
        	if(neededRam/neededCpuInMHz > MAGICPROPORTION){
        		// Seems that application will need more RAM than CPU
        		// Maximum 15% more Ram allowed
        		PhysicalMachine pm = selectPmWithLowestRamUtilization(neededRam, neededHddSize, neededCpuInMHz);
        		if(pm != null) return pm;
        		
        	}else{
        		// Seems that application will need more CPU than RAM
        		// Maximum 15% more Cpu allowed
        		PhysicalMachine pm = selectPmWithLowestCpuUtilization(neededRam, neededHddSize, neededCpuInMHz);
        		if(pm != null) return pm;
        		
        	}
        	// Seems like no overprovisioning possible, try to get first possible PM (should be useless)
        	//PhysicalMachine firstPossiblePM = selectFirstPossiblePM(neededRam, neededHddSize, neededCpuInMHz);
        	//if(firstPossiblePM != null) return firstPossiblePM;
        	
            //list iterated and no pm could give back -> start new pm
            PhysicalMachine pm = createNewPM();
            pm.start();
            overallInfo.setTotalPMs(overallInfo.getTotalPMs() + 1);
            return pm;
        }
    }
    
    private PhysicalMachine selectPmWithLowestCpuUtilization(Integer neededRam, Integer neededHddSize, Integer neededCpuInMHz) {
    	PhysicalMachine lowestPM = null;
    	for (PhysicalMachine pm : this.pmAllocations.values()) {
    		if (lowestPM == null || lowestPM.getCpuAvailable() < pm.getCpuAvailable()) {
    			lowestPM = pm;
    	    }
    	}
    	// check if overprovisioning is possible on that pm
    	if ((lowestPM.getCpuAvailable() - neededCpuInMHz) > 0 ) {
        	return lowestPM;	
    	} else {
    	    
            System.out.println("getOverprovidedCpuInMhz: " + lowestPM.getOverprovidedCpuInMhz() + " and CpuMAx: " + lowestPM.getCpuInMhzMax());
            if (lowestPM.getOverprovidedCpuInMhz() != 0 && (lowestPM.getCpuInMhzMax() > lowestPM.getOverprovidedCpuInMhz())) {
                // we already have overprovided
                return null;
            }
            
            /*Only debug*/
            double temp = lowestPM.getCpuInMhzMax() * cpuThreshold - lowestPM.getCpuInMhzMax();
            double temp1 = (neededCpuInMHz - lowestPM.getCpuAvailable());
            System.out.println("We check if: " + lowestPM.getCpuInMhzMax() + " * " + cpuThreshold + " - " + lowestPM.getCpuInMhzMax() + " = " + temp);
            System.out.println("bigger than: " + neededCpuInMHz + " - " + lowestPM.getCpuAvailable() + " = " + temp1);
            
            
	    	if ((lowestPM.getCpuInMhzMax() * cpuThreshold - lowestPM.getCpuInMhzMax()) > (neededCpuInMHz - lowestPM.getCpuAvailable())) {
	    	    System.out.println("we overprovide now");
	    		overProvide = true;
	    		return lowestPM;
	    	} else {
	    		return null;
	    	}
    	}
    }
    
    private PhysicalMachine selectPmWithLowestRamUtilization(Integer neededRam, Integer neededHddSize, Integer neededCpuInMHz) {
    	PhysicalMachine lowestPM = null;
    	for (PhysicalMachine pm : this.pmAllocations.values()) {
    		if (lowestPM == null || lowestPM.getRamAvailable() < pm.getRamAvailable()) {
    			lowestPM = pm;
    	    }
    	}
    	
    	if (lowestPM.getOverprovidedRam() != 0 && (lowestPM.getRamMax() > lowestPM.getOverprovidedRam())) {
            return null;
        }
    	// check if overprovisioning is possible on that pm
    	if ((lowestPM.getRamAvailable() - neededRam) > 0 ) {
        	return lowestPM;	
    	} else {
	    	if ((lowestPM.getRamMax() * ramThreshold - lowestPM.getRamMax()) > (neededRam - lowestPM.getRamAvailable())) {
	    		overProvide = true;
	    		return lowestPM;
	    	} else {
	    		return null;
	    	}
    	}
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
        overallInfo.setScheduler(this.getClass().getName());
        overallInfo.setTotalDuration(internalTime);
        return this.overallInfo;
    }

    @Override
    public void setMaxNumberOfPhysicalMachines(int nr) {
        this.maxPMs = nr;
    }
}
