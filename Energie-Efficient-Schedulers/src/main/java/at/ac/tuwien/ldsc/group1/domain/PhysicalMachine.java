package at.ac.tuwien.ldsc.group1.domain;

/**
 * Interface for a physical machine, we can only start it, and shut it down
 *
 * @author Sebastian Geiger
 */
public interface PhysicalMachine extends Machine{
    /**
     * Boot the physical machine
     */
    void boot();

    /**
     * Shutdown the physical machine
     */
    void shutdown();

    /**
     * Time since the machine was started
     */
    int uptime();

    /**
     * Power consumption since the physical machine was started
     */
    int powerConsumption();

    /**
     * Assign a virtual machine to this physical machine
     */
    void addVm(VirtualMachine vm);
}
