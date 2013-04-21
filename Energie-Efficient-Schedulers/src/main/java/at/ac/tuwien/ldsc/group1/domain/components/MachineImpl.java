package at.ac.tuwien.ldsc.group1.domain.components;

import java.util.ArrayList;
import java.util.List;

public class MachineImpl implements Machine, Composite {
    /**
     * A default component has a power consumption of 0.3 Watt per MHz,
     * concrete implementations of Component are allowed to
     */
    private static final double DEFAULT_POWER_PER_MHZ = 0.3;
    public final double CURRENT_POWER_PER_MHZ = POWER_PER_MHZ;
    public static double POWER_PER_MHZ = DEFAULT_POWER_PER_MHZ;
    public static final int MS_PER_SECOND = 1000;

    //Base resources must be set when the machine is instantiated
    //they represent the requirements for the OS.
    private int hddBase;
    private int ramBase;
    private int cpuInMhzBase;

    //The maximum resources that are available to this machine
    //they are based on the resources that the underlying hardware
    //can provide
    protected int hddMax;
    protected int cpuInMhzMax;
    protected int ramMax;

    /**
     * A list of components that are managed by this machine
     */
    private List<Component> components = new ArrayList<>();

    //Constructors
    public MachineImpl(
            int ramBase, int hddBase, int cpuInMhzBase,
            int ramMax, int hddMax, int cpuInMhzMax)
    {
        this.ramBase = ramBase;
        this.hddBase = hddBase;
        this.cpuInMhzBase = cpuInMhzBase;
        this.ramMax = ramMax;
        this.hddMax = hddMax;
        this.cpuInMhzMax = cpuInMhzMax;
    }

    //Methods
    @Override
    public int getRam() {
        int ram = ramBase;
        for(Component c : components) {
            ram += c.getRam();
        }
        assert(ram <= getRamMax());
        return ram;
    }

    @Override
    public int getHddSize() {
        int hddSize = hddBase;
        for(Component c : components) {
            hddSize += c.getHddSize();
        }
        assert(hddSize <= getHddMax());
        return hddSize;
    }

    @Override
    public int getCpuInMhz() {
        int cpuInMhz = cpuInMhzBase;
        for(Component c : components) {
            cpuInMhz += c.getCpuInMhz();
        }
        assert(cpuInMhz <= getCpuInMhzMax());
        return cpuInMhz;
    }

    @Override
    public double getPowerConsumption() {
        int powerConsumption = 0;
        for(Component c : components) {
            powerConsumption += c.getCpuInMhz() * CURRENT_POWER_PER_MHZ/ MS_PER_SECOND;
        }
        return powerConsumption;
    }

    @Override
    public void start() {
        //TODO: Use log4j
        System.out.println("Started a machine");
    }

    @Override
    public void stop() {
        //TODO: Use log4j
        System.out.println("Stopped a machine");
    }

    @Override
    public void addComponent(Component component) {
        components.add(component);
    }

    @Override
    public void removeComponent(Component component) {
        components.remove(component);
    }

    @Override
    public List<Component> getComponents() {
        return components;
    }

    public int getHddBase() {
        return hddBase;
    }

    public int getRamBase() {
        return ramBase;
    }

    public int getCpuInMhzBase() {
        return cpuInMhzBase;
    }

    @Override
    public int getHddMax() {
        return hddMax;
    }

    @Override
    public int getRamMax() {
        return ramMax;
    }

    @Override
    public int getCpuInMhzMax() {
        return cpuInMhzMax;
    }
}
