/**
 * Initial State: All PMs switched off. If an application arrives, try to modify size, CPU and RAM of an existing VM to run the application. 
 * If no VM is running, create a new one (start a new PM if necessary). If the application has finished decrease the size, CPU and RAM of 
 * the VM. If no applications are running on a VM, shut down the VM. If no VM is running on a PM, shut down the PM. Try to get a maximum 
 * of utilization on every PM. Migration: Try to move applications from VMs to other VMs to get a better utilization and to use less PMs.
 */
package at.ac.tuwien.ldsc.group1.application;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import at.ac.tuwien.ldsc.group1.domain.CloudInfo;
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

public class Scheduler2 implements Schedulable {
    
    List<Application> applications;
    List<PhysicalMachine> physicalMachines;
    Integer VmRamBase;
    Integer VmHddBase;
    Integer VmCpuInMhzBase;
    CsvWriter writer;
    Event currentEvent = null;

    public Scheduler2(CsvWriter writer) {
        ResourceBundle res = ResourceBundle.getBundle("virtualMachine");
        VmRamBase = Integer.parseInt(res.getString("ramBase"));
        VmHddBase = Integer.parseInt(res.getString("sizeBase"));
        VmCpuInMhzBase = Integer.parseInt(res.getString("cpuBase")); 
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
        // If an application arrives, try to modify size, CPU and RAM of an existing VM to run the application. 
        // If no VM is running, create a new one (start a new PM if necessary). If the application has finished decrease the size, CPU and RAM of 
        // the VM. 
        
        
        Integer neededRam = application.getRam() + this.VmRamBase;
        Integer neededHddSize = application.getHddSize() + this.VmHddBase;
        Integer neededCpuInMHz = application.getCpuInMhz() + this.VmCpuInMhzBase;
        
        PhysicalMachine pm = selectOptimalPM(neededRam,neededHddSize,neededCpuInMHz);
        VirtualMachine vm = selectOptimalVM(pm, application);

        //vm.start();          //TODO what is start stand for? Can we do there the resource allocation?
        //allocate resources
        try {
            vm.addComponent(application);
        } catch (ResourceUnavailableException e) {
            
            //TODO  implement inputStream Exception
            System.out.println("Error while trying to allocate Resources, if we see this coming that means " +
                    "either that i did the PM selection wrong " +
                    "or (AppResources + VMBaseResources) > MaxPMResources");
            System.out.println("Requirements: CPU: " + neededCpuInMHz + " HDD: "+ neededHddSize + " Ram: "+ neededRam);
            System.out.println("VM CPU Available: "+vm.getCpuAvailable() + "/ Used:" + vm.getCpuInMhz());
            System.out.println("VM HDD Available: "+vm.getHddAvailable() + "/ Used:" + vm.getHddSize()) ;
            System.out.println("VM RAM Available: "+vm.getRamAvailable()+ "/ Used:" + vm.getRam());
            System.out.println("PM CPU Available: "+pm.getCpuAvailable()+ "/ Used:" + pm.getCpuInMhz());
            System.out.println("PM HDD Available: "+pm.getHddAvailable()+ "/ Used:" + pm.getHddSize());
            System.out.println("PM RAM Available: "+pm.getRamAvailable()+ "/ Used:" + pm.getRam());
        }
        
        

        //Finally: Log current cloud utilization details to output file 2
        this.writeLog(this.currentEvent.getEventTime());

    }

    @Override
    public void removeApplication(Application application) {
        // TODO Auto-generated method stub

    }
    
    private VirtualMachine selectOptimalVM(PhysicalMachine pm, Application application) throws ResourceUnavailableException {
        VirtualMachine vm = null;
        for(Component c : pm.getComponents()) {
            vm = (VirtualMachineImpl) c;
        }
        if (vm == null) return new VirtualMachineImpl(pm);
        else {
            // TODO: Allocate resources of Application
            return vm;
        }
    }
    
    private VirtualMachine tryMigrateVm(List<PhysicalMachine> physicalMachines) {
        for(PhysicalMachine iPm : physicalMachines){
            for(Component c : iPm.getComponents()){
                VirtualMachine vm = (VirtualMachine) c;
            }
        }
        return null;
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
                if( pm.getCpuAvailable() >= neededCpuInMHz &&
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
        int totalPowerConsumption = 0;
        int inSourced = 0;      //TODO
        int outSourced = 0;     //TODO
        
        timestamp = (int) timeStamp;
        runningPMs = this.physicalMachines.size();
        for(Machine pm : this.physicalMachines){
            totalRAM += pm.getRamAvailable();
            totalCPU += pm.getCpuAvailable();
            totalSize += pm.getHddAvailable();
            runningVMs += pm.getComponents().size();
            pm.setEventTime(timeStamp);
            totalPowerConsumption += pm.getOverallConsumption();
        }
        
        CloudInfo info = new CloudInfo(timestamp, totalRAM, totalCPU, totalSize, runningPMs, runningVMs, totalPowerConsumption, inSourced, outSourced);
        this.writer.writeCsv(info);
    }
    
    @Override
    public void finalize(){
        this.writer.close();
    }

}
