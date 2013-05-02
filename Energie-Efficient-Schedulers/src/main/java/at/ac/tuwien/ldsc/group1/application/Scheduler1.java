package at.ac.tuwien.ldsc.group1.application;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import at.ac.tuwien.ldsc.group1.domain.CloudInfo;
import at.ac.tuwien.ldsc.group1.domain.Event;
import at.ac.tuwien.ldsc.group1.domain.EventType;
import at.ac.tuwien.ldsc.group1.domain.components.Application;
import at.ac.tuwien.ldsc.group1.domain.components.Component;
import at.ac.tuwien.ldsc.group1.domain.components.PhysicalMachine;
import at.ac.tuwien.ldsc.group1.domain.components.PhysicalMachineImpl;
import at.ac.tuwien.ldsc.group1.domain.components.VirtualMachine;
import at.ac.tuwien.ldsc.group1.domain.components.VirtualMachineImpl;
import at.ac.tuwien.ldsc.group1.domain.exceptions.ResourceUnavailableException;

public class Scheduler1 implements Schedulable {
    List<Application> applications;
    List<PhysicalMachine> physicalMachines;
    Integer VMramBase;
    Integer VMhddBase;
    Integer VMcpuInMhzBase;
    CsvWriter writer;
    Event currentEvent = null;

    public Scheduler1() {
    	
    	ResourceBundle res = ResourceBundle.getBundle("virtualMachine");
    	VMramBase = Integer.parseInt(res.getString("ramBase"));
		VMhddBase = Integer.parseInt(res.getString("sizeBase"));
		VMcpuInMhzBase = Integer.parseInt(res.getString("cpuBase")); 
		writer = new CsvWriter("TestOutput2.csv"); //TODO not here...
    	
	}

	@Override
    public void schedule(Event event) {
		this.currentEvent = event;
        if(event.getEventType() == EventType.START) {
            //TODO: check resources
            this.addApplication(event.getApplication());
        } else {
            this.removeApplication(event.getApplication());
        }
    }

    @Override
    public void addApplication(Application application) {
        //1. make a decision on which virtual machine this application will run
    	   	
    	//A.) Create VM
    	//B.) Fill PM with VM until they are full
    	//	B/1.) Start new PM if needed
    	//	B/2.) Optimize PM selection
    	Integer neededRam = application.getRam() + this.VMramBase;
    	Integer neededHddSize = application.getHddSize() + this.VMhddBase;
    	Integer neededCpuInMHz = application.getCpuInMhz() + this.VMcpuInMhzBase;
    	
    	PhysicalMachine pm = selectOptimalPM(neededRam,neededHddSize,neededCpuInMHz);
    	VirtualMachine vm = new VirtualMachineImpl(VMramBase, VMhddBase, VMcpuInMhzBase, pm);
//    	pm.addComponent(vm); //TODO --> why we give the parent in the constructor if we use it for nothing there?
    	vm.start(); 		 //TODO what is start stand for? Can we do there the resource allocation?
    	//allocate resources
    	try {
    		vm.addComponent(application);
			vm.addCpu(application.getCpuInMhz());
			vm.addHddSize(application.getHddSize());
			vm.addRam(application.getRam());
    	
    	} catch (ResourceUnavailableException e) {
    		System.out.println("Error while trying to allocate Resources, if we see this coming that means " +
    				"either that i did the PM selection wrong " +
    				"or (AppResources + VMBaseResources) > MaxPMResources");
    		System.out.println("Requirements: CPU: " + neededCpuInMHz + " HDD: "+ neededHddSize + " Ram: "+ neededRam);
    		
    		System.out.println("VM CPU Available: "+vm.getCpuAvailable() + "/ Max:" + vm.getCpuInMhz());
    		System.out.println("VM HDD Available: "+vm.getHddAvailable() + "/ Max:" + vm.getHddSize()) ;
    		System.out.println("VM RAM Available: "+vm.getRamAvailable()+ "/ Max:" + vm.getRam());
    		System.out.println("PM CPU Available: "+pm.getCpuAvailable()+ "/ Max:" + pm.getCpuInMhz());
    		System.out.println("PM HDD Available: "+pm.getHddAvailable()+ "/ Max:" + pm.getHddSize());
    		System.out.println("PM RAM Available: "+pm.getRamAvailable()+ "/ Max:" + pm.getRam());
    		
    		
    	}

        //Finally: Log current clould utilization details to output file 2
    	this.writeLog(this.currentEvent.getEventTime());
    }

	@Override
    public void removeApplication(Application application) {
        //1. find the virtual machine on which this application runs
        //   and remove it.
		VirtualMachine hostVM = null;
		for (PhysicalMachine pm : physicalMachines) {
			for(Component VMcomp : pm.getComponents()){
				VirtualMachine vm = (VirtualMachine) VMcomp;
				for(Component appComp : vm.getComponents()){
					if(appComp.equals(application)) {
						hostVM = vm;
						break;
					}
				}
			}
		}
		if(hostVM != null)	{
			hostVM.removeComponent(application);
			//free resources on VM:
			hostVM.removeCpu(application.getCpuInMhz());
			hostVM.removeHddSize(application.getHddSize());
			hostVM.removeRam(application.getRam());
		}else{
			System.out.println("How come app is running on no virtual machine?");
		}
		
		

    	//C.) Kill VM if not needed anymore (No App running on it)
		VirtualMachine vmToBeStopped = null;
		for (PhysicalMachine pm : physicalMachines) {
			for(Component VMcomp : pm.getComponents()){
				VirtualMachine vm = (VirtualMachine) VMcomp;
				if(vm.getComponents() == null || vm.getComponents().isEmpty()){
					vmToBeStopped = vm;
				}
			}
		}
		
		if(vmToBeStopped != null){
			vmToBeStopped.stop();
			vmToBeStopped.getParent().removeComponent(vmToBeStopped);	//TODO I Think it should be happening inside the stop()
			
			//D.) Kill PM if not needed anymore (No VM running on it) 
			//Do not run this part if no VM were stopped before
			PhysicalMachine pmToBeStopped = null;
			for (PhysicalMachine pm : physicalMachines) {
				if(pm.getComponents() == null || pm.getComponents().isEmpty()){
					pmToBeStopped = pm;
				}
			}
			
			if(pmToBeStopped != null){
				pmToBeStopped.stop();
				this.physicalMachines.remove(pmToBeStopped);
			}
			
		}else{
			//do nothing
		}
    	
        //Finally: Log current clould utilization details to output file 2
		this.writeLog(this.currentEvent.getEventTime());
    }
	
	private PhysicalMachine selectOptimalPM(Integer neededRam, Integer neededHddSize, Integer neededCpuInMHz) {
		
		if(this.physicalMachines == null){
			PhysicalMachine pm = this.createNewPhisicalMachine();
			pm.start(); //TODO start method is empty --> Count Initial Power Consumption there?
			this.physicalMachines = new ArrayList<PhysicalMachine>();
			this.physicalMachines.add(pm);
			return pm;
		}else{
			//iterate over PMList give back first possible
			//TODO find more clever solution
			for (PhysicalMachine pm : this.physicalMachines) {
				if(	pm.getCpuAvailable() > neededCpuInMHz &&
					pm.getRamAvailable() > neededRam &&
					pm.getHddAvailable() > neededHddSize){
				
					return pm;
				}
			}
			
			//list iterated and no pm could give back -> start new pm
			PhysicalMachine pm = this.createNewPhisicalMachine();
			pm.start();
			this.physicalMachines.add(pm);
			return pm;
			
		}

	}

	private PhysicalMachine createNewPhisicalMachine() {
		ResourceBundle res = ResourceBundle.getBundle("physicalMachine");
		
		Integer ramBase = Integer.parseInt(res.getString("ramBase"));
		Integer hddBase = Integer.parseInt(res.getString("sizeBase"));
		Integer cpuInMhzBase = Integer.parseInt(res.getString("cpuBase")); 
		Integer ramMax = Integer.parseInt(res.getString("ramMax")); 
		Integer hddMax = Integer.parseInt(res.getString("sizeMax")); 
		Integer cpuInMhzMax = Integer.parseInt(res.getString("cpuMax")); 
		
		PhysicalMachine pm = new PhysicalMachineImpl(ramBase, hddBase, cpuInMhzBase, ramMax, hddMax, cpuInMhzMax);
		return pm;
	}
	
	private void writeLog(long timeStamp) {
		int timestamp;
		int totalRAM = 0;
		int totalCPU = 0;
		int totalSize = 0;
		int runningPMs;
		int runningVMs = 0;
		int totalPowerConsumption = 0;
		int inSourced = 0;		//TODO
		int outSourced = 0;		//TODO
		
		timestamp = (int) timeStamp;
		runningPMs = this.physicalMachines.size();
		for(PhysicalMachine pm : this.physicalMachines){
			totalRAM += pm.getRamAvailable();
			totalCPU += pm.getCpuAvailable();
			totalSize += pm.getHddAvailable();
			runningVMs += pm.getComponents().size();
			totalPowerConsumption += pm.getPowerConsumption(); //TODO -> Counting the Consumption of a machine -> updating consumption in each TimeStep
		}
		
		CloudInfo info = new CloudInfo(timestamp, totalRAM, totalCPU, totalSize, runningPMs, runningVMs, totalPowerConsumption, inSourced, outSourced);
		this.writer.writeCsv(info);
	}
	
	@Override
	public void finalize(){
		this.writer.close();
	}

}
