package at.ac.tuwien.ldsc.group1.domain;

/**
 * Interface for a physical machine, we can only start it, and shut it down
 *
 * @author Sebastian Geiger
 */
public interface PhysicalMachine {
    void boot();
    void shutdown();
}
