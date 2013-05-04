package at.ac.tuwien.ldsc.group1.application;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import at.ac.tuwien.ldsc.group1.domain.CloudStateInfo;
import at.ac.tuwien.ldsc.group1.domain.Event;
import at.ac.tuwien.ldsc.group1.domain.EventType;
import at.ac.tuwien.ldsc.group1.domain.components.Application;
import at.ac.tuwien.ldsc.group1.domain.components.Component;
import at.ac.tuwien.ldsc.group1.domain.components.Machine;
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
    Integer overAllConsumption;

    public Scheduler1(CsvWriter writer) {
    	ResourceBundle res = ResourceBundle.getBundle("virtualMachine");
    	VMramBase = Integer.parseInt(res.getString("ramBase"));
		VMhddBase = Integer.parseInt(res.getString("sizeBase"));
		VMcpuInMhzBase = Integer.parseInt(res.getString("cpuBase")); 
		this.writer = writer; 
    	
	}

	@Override
    public void schedule(Event event) {
		this.currentEvent = event;
        if(event.getEventType() == EventType.START) {
            //TODO: check resources
            try {
            	this.addApplication(event.getApplication());
            }catch (ResourceUnavailableException e) {
				e.printErrorMsg();
			}
            
        } else {
            this.removeApplication(event.getApplication());
        }
    }

    @Override
    public void addApplication(Application application) throws ResourceUnavailableException {
        //1. make a decision on which virtual machine this application will run
    	   	
    	//A.) Create VM
    	//B.) Fill PM with VM until they are full
    	//	B/1.) Start new PM if needed
    	//	B/2.) Optimize PM selection
    	Integer neededRam = application.getRam() + this.VMramBase;
    	Integer neededHddSize = application.getHddSize() + this.VMhddBase;
    	Integer neededCpuInMHz = application.getCpuInMhz() + this.VMcpuInMhzBase;
    	
    	PhysicalMachine pm = selectOptimalPM(neededRam,neededHddSize,neededCpuInMHz);
    	VirtualMachine vm = new VirtualMachineImpl(pm);

    	vm.start(); 		 //TODO what is start stand for? Can we do there the resource allocation?
    	//allocate resources
    	try {
    		vm.addComponent(application);		//allocate resources inside this method
		} catch (ResourceUnavailableException e) {
			
			e.printResourceAllocationErrorLog(pm,vm,neededCpuInMHz,neededHddSize,neededRam);
			
		}
    	
    	

        //Finally: Log current cloud utilization details to output file 2
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
			hostVM.removeComponent(application);	 // free resources inside this method
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
			PhysicalMachine pm = new PhysicalMachineImpl();
			this.physicalMachines = new ArrayList<PhysicalMachine>();
			this.physicalMachines.add(pm);
			pm.start(); //TODO start method is empty --> Count Initial Power Consumption there?
			return pm;
		}else{
			//iterate over PMList give back first possible
			//TODO find more clever solution
			for (PhysicalMachine pm : this.physicalMachines) {
				if(	pm.getCpuAvailable() >= neededCpuInMHz &&
					pm.getRamAvailable() >= neededRam &&
					pm.getHddAvailable() >= neededHddSize){
				
					return pm;
				}
			}
			
			//list iterated and no pm could give back -> start new pm
			PhysicalMachine pm = new PhysicalMachineImpl();
			this.physicalMachines.add(pm);
			pm.start();
			return pm;
			
		}

	}

	
	private void writeLog(long timeStamp) {
		int timestamp;
		int totalRAM = 0;
		int totalCPU = 0;
		int totalSize = 0;
		int runningPMs;
		int runningVMs = 0;
		double totalPowerConsumption = 0;
		int inSourced = 0;		//TODO
		int outSourced = 0;		//TODO
		
		timestamp = (int) timeStamp;
		runningPMs = this.physicalMachines.size();
		for(Machine pm : this.physicalMachines){
			totalRAM += pm.getRamAvailable();
			totalCPU += pm.getCpuAvailable();
			totalSize += pm.getHddAvailable();
			runningVMs += pm.getComponents().size();
			//this consumption is the overall powerconsuption of the cloud in the moment
			totalPowerConsumption += pm.getPowerConsumption();
		}
		
		CloudStateInfo info = new CloudStateInfo(timestamp, totalRAM, totalCPU, totalSize, runningPMs, runningVMs, totalPowerConsumption, inSourced, outSourced);
		this.writer.writeCsv(info);
	}
	
	@Override
	public void finalize(){
		this.writer.close();
	}

}
