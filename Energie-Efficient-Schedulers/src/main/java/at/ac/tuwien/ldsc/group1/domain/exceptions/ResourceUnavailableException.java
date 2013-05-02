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

    
    
    //TODO: Add some enum based exception types like:
    //      OUT_OF_RAM
    //      OUT_OF_HARDDISK
    //      OUT_OF_CPU
}
