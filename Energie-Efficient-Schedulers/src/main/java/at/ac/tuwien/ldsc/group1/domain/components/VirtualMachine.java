package at.ac.tuwien.ldsc.group1.domain.components;

import at.ac.tuwien.ldsc.group1.domain.exceptions.ResourceUnavailableException;


/**
 * An interface for a virtual machine
 *
 * @author Sebastian Geiger
 */
public interface VirtualMachine extends Machine {

    /**
     * Suspend the machine. The requirements for cpu and ram are set to zero,
     * but hddSize remains the same a suspended vm is also not running.
     */
    void suspend();

    /**
     * When a virtual machine is migrated to another (physical) machine,
     * this method must be called to pass set the new parent machine of
     * this virtual machine.
     */
    void migrate(Machine parent) throws ResourceUnavailableException;

    /**
     * Tries to add more ram, and throws an exceptions if the maximum is reached
     */
    void addRam(int value) throws ResourceUnavailableException;

    /**
     * Tries to add more hard disk space and throws an exception if the maximum is reached
     */
    void addHddSize(int value) throws ResourceUnavailableException;

    /**
     * Tries to add more cpu cycles (in [MHz]) and throws an exception if the maximum is reached
     */
    void addCpu(int value) throws ResourceUnavailableException;

    /**
     * Removes hard disk size from the machine, if you try to remove more hard disk size than
     * the machine can free then the hdd size will be set to the base requirement for this machine.
     */
    void removeHddSize(int value);

    /**
     * Removes RAM from the machine, if you try to remove more RAM than
     * the machine can free then the RAM size will be set to the base
     * requirement for this machine.
     */
    void removeRam(int value);

    /**
     * Remove CPU cycles from this machine. If you try to remove more
     * cpu cycles than this machine can free, then the CPU cycles are set
     * to the base requirement for this machine.
     */
    void removeCpu(int value);
}
