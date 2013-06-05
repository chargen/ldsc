package at.ac.tuwien.ldsc.group1.domain.components;

import at.ac.tuwien.ldsc.group1.domain.exceptions.ResourceUnavailableException;

import java.util.ResourceBundle;

public class VirtualMachineImpl extends MachineImpl implements VirtualMachine {

    //Fields
    private int ram;
	private int hddSize;
    private int cpuInMhz;

    static final Integer VmRamBase;
    static final Integer VmHddBase;
    static final Integer VmCpuInMhzBase;
    
    static{
        ResourceBundle res = ResourceBundle.getBundle("virtualMachine");
        VmRamBase = Integer.parseInt(res.getString("ramBase"));
        VmHddBase = Integer.parseInt(res.getString("sizeBase"));
        VmCpuInMhzBase = Integer.parseInt(res.getString("cpuBase"));
    }

    private static int nextId = 0;
    private final int id;

    //Constructors
    public VirtualMachineImpl(Machine parent) throws ResourceUnavailableException {
        this(VmRamBase, VmHddBase, VmCpuInMhzBase, parent);
    }
    
    public VirtualMachineImpl(
            int ramBase, int hddBase, int cpuInMhzBase,
            Machine parent) throws ResourceUnavailableException
    {
        super(ramBase, hddBase, cpuInMhzBase, parent);
        //This virtual machine starts with a size equal to its base requirements
        this.ram = ramBase;
        this.hddSize = hddBase;
        this.cpuInMhz = cpuInMhzBase;
        this.id = ++nextId;
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
            throw new ResourceUnavailableException(this);
        }
    }

    @Override
    public void addHddSize(int value) throws ResourceUnavailableException {
        if(value <= getHddAvailable()) {
            this.hddSize += value;
        } else {
            throw new ResourceUnavailableException(this);
        }
    }

    @Override
    public void addCpu(int value) throws ResourceUnavailableException {
        if(value <= getCpuAvailable()) {
            this.cpuInMhz += value;
        } else {
            throw new ResourceUnavailableException(this);
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
    public int getRam() {
        assert(super.getRam() == ram);
		return ram;
	}

    @Override
	public int getHddSize() {
        assert(super.getHddSize() == hddSize);
		return hddSize;
	}

    @Override
	public int getCpuInMhz() {
        assert(super.getCpuInMhz() == cpuInMhz);
		return cpuInMhz;
	}

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void migrate(Machine parent) throws ResourceUnavailableException {
    	Machine oldParent = this.getParent();
    	oldParent.removeComponent(this);
        setParent(parent);
        parent.addComponent(this);
    }

    /* We need to override the addComponent and removeComponent methods, because they need to handle
     * the resizing of the vm.
     */
    @Override
    public void addComponent(Component component) throws ResourceUnavailableException {
        this.addCpu(component.getCpuInMhz());
        this.addHddSize(component.getHddSize());
        this.addRam(component.getRam());
        super.addComponent(component);
    }

    @Override
    public void removeComponent(Component component) {
        super.removeComponent(component);
        this.removeCpu(component.getCpuInMhz());
        this.removeRam(component.getRam());
        this.removeHddSize(component.getHddSize());
    }

    @Override
    public void start() {
        System.out.println("    VM " + getId() + " Started on Parent: " + parent.getId());
    }

    @Override
    public void stop() {
        System.out.println("    VM " + getId() + " Stopped on Parent: " + parent.getId());
        this.getParent().removeComponent(this);
    }

    @Override
    public void addOverProvidedComponent(Application application) throws ResourceUnavailableException {
        // TODO: DELETE THIS METHOD
        this.addCpu(application.getCpuInMhz());
        this.addHddSize(application.getHddSize());
        this.addRam(application.getRam());
        super.addComponent(application);
    }

	@Override
	public String toString() {
		return "VirtualMachineImpl [ram=" + ram + ", hddSize=" + hddSize
				+ ", cpuInMhz=" + cpuInMhz + ", id=" + id + ", hddMax="
				+ hddMax + ", cpuInMhzMax=" + cpuInMhzMax + ", ramMax="
				+ ramMax + ", overprovidedCpuInMhz=" + overprovidedCpuInMhz
				+ ", overprovidedRam=" + overprovidedRam + "]";
	}
    
    
}
