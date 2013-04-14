package at.ac.tuwien.ldsc.group1.domain;

/**
 * An interface for a virtual machine
 *
 * @author Sebastian Geiger
 */
public interface VirtualMachine {
    void start();
    void stop();
    void suspend();
    void addHdd(int value);
    void addHddSize(int value);
    void addCpu(int value);
    void removeHddSize(int value);
    void removeMemory(int value);
    void removeCpu(int value);
}
