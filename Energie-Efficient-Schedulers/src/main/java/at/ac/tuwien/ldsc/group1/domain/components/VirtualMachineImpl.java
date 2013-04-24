package at.ac.tuwien.ldsc.group1.domain.components;

import at.ac.tuwien.ldsc.group1.domain.exceptions.ResourceUnavailableException;

public class VirtualMachineImpl extends MachineImpl implements VirtualMachine {

    //Fields
    private int ram;
    private int hddSize;
    private int cpuInMhz;

    //Constructors
    public VirtualMachineImpl(
            int ramBase, int hddBase, int cpuInMhzBase,
            Machine parent)
    {
        super(ramBase, hddBase, cpuInMhzBase,
            parent);
        //This virtual machine starts with a size equal to its base requirements
        this.ram = ramBase;
        this.hddSize = hddBase;
        this.cpuInMhz = cpuInMhzBase;
    }

    //Methods
    @Override
    public void suspend() {
        //TODO: Use log4j
        System.out.println("This VM is suspended.");
    }

    @Override
    public void addRam(int value) throws ResourceUnavailableException {
        if(value <= getRamAvailable()) {
            this.ram += value;
        } else {
            throw new ResourceUnavailableException();
        }
    }

    @Override
    public void addHddSize(int value) throws ResourceUnavailableException {
        if(value <= getHddAvailable()) {
            this.hddSize += value;
        } else {
            throw new ResourceUnavailableException();
        }
    }

    @Override
    public void addCpu(int value) throws ResourceUnavailableException {
        if(value <= getCpuAvailable()) {
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
    public void migrate(Machine parent) {
    	Machine oldParent = this.getParent();
    	oldParent.removeComponent(this);
        setParent(parent);
        parent.addComponent(this);
    }
}
