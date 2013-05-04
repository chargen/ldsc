package at.ac.tuwien.ldsc.group1.domain.exceptions;

import at.ac.tuwien.ldsc.group1.domain.components.Machine;
import at.ac.tuwien.ldsc.group1.domain.components.PhysicalMachine;
import at.ac.tuwien.ldsc.group1.domain.components.VirtualMachine;

public class ResourceUnavailableException extends Exception {
    private static final long serialVersionUID = -2012040940846202170L;

    private Machine m;
    
    
    
    public ResourceUnavailableException() {
		super();
	}

	public ResourceUnavailableException(Machine m) {
		this.m = m;
	}
	
	public void printErrorMsg() {
		// TODO Auto-generated method stub
		super.printStackTrace();
		
	}

	public void printResourceAllocationErrorLog(PhysicalMachine pm, VirtualMachine vm,
			Integer neededCpuInMHz, Integer neededHddSize, Integer neededRam) {
		
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

	

    
    
    //TODO: Add some enum based exception types like:
    //      OUT_OF_RAM
    //      OUT_OF_HARDDISK
    //      OUT_OF_CPU
}
