package at.ac.tuwien.ldsc.group1.domain.components;

import at.ac.tuwien.ldsc.group1.domain.exceptions.ResourceUnavailableException;

public class VirtualMachineImpl extends MachineImpl implements VirtualMachine {

    //Fields
    private int ram;
    private int hddSize;
    private int cpuInMhz;
    private PhysicalMachine pm;
    
    //Constructors
    // TODO: First one seems to be deprecated
    public VirtualMachineImpl(
            int ramBase, int hddBase, int cpuInMhzBase,
            int ramMax, int hddMax, int cpuInMhzMax)
    {
        super(ramBase, hddBase, cpuInMhzBase,
            ramMax, hddMax, cpuInMhzMax);
        //This virtual machine starts with a size equal to its base requirements
        this.ram = ramBase;
        this.hddSize = hddBase;
        this.cpuInMhz = cpuInMhzBase;
    }
    
    public VirtualMachineImpl(
            int ramBase, int hddBase, int cpuInMhzBase,
            PhysicalMachine pm)
    {
        super(ramBase, hddBase, cpuInMhzBase,
            pm.getRamAvailable(), pm.getHddAvailable(), pm.getCpuAvailable());
        //This virtual machine starts with a size equal to its base requirements
        this.ram = ramBase;
        this.hddSize = hddBase;
        this.cpuInMhz = cpuInMhzBase;
        this.setPhysicalMachine(pm);
    }

    //Methods
    @Override
    public void suspend() {
        //TODO: Use log4j
        System.out.println("This VM is suspended.");
    }

    @Override
    public void addRam(int value) throws ResourceUnavailableException {
        if(ram + value <= getRamMax()) {
            this.ram += value;
        } else {
            throw new ResourceUnavailableException();
        }
    }

    @Override
    public void addHddSize(int value) throws ResourceUnavailableException {
        if(hddSize + value <= getHddMax()) {
            this.hddSize += value;
        } else {
            throw new ResourceUnavailableException();
        }
    }

    @Override
    public void addCpu(int value) throws ResourceUnavailableException {
        if(cpuInMhz + value <= getCpuInMhzMax()) {
            this.cpuInMhz += value;
        } else {
            throw new ResourceUnavailableException();
        }
    }

    @Override
    public void removeHddSize(int value) {
        if(hddSize - value >= getHddBase()) {
            this.hddSize -= value;
        } else {
            this.hddSize = getHddBase();
        }
    }

    @Override
    public void removeRam(int value) {
        if(ram - value >= getRamBase()) {
            this.ram -= value;
        } else {
            this.ram = getRamBase();
        }
    }

    @Override
    public void removeCpu(int value) {
        if(cpuInMhz - value >= getCpuInMhzBase()) {
            this.cpuInMhz -= value;
        } else {
            this.cpuInMhz = getCpuInMhzBase();
        }
    }

    @Override
    public void updateMaxValues(int ramMax, int hddSizeMax, int cpuInMhzMax) {
        super.ramMax = ramMax;
        super.hddMax = hddSizeMax;
        super.cpuInMhzMax = cpuInMhzMax;
    }

	public PhysicalMachine getPhysicalMachine() {
		return pm;
	}

	public void setPhysicalMachine(PhysicalMachine pm) {
		this.pm = pm;
		this.parent = pm;
	}
}
