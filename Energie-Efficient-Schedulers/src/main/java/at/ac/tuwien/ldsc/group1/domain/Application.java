package at.ac.tuwien.ldsc.group1.domain;

/**
 * An interface for an application that runs in a virtual machine.
 * Based on the assignment an application is always created with fixed
 * size, ram and MHz requirements. Therefore this inteface is read-only.
 *
 * @author Sebastian Geiger
 */
public interface Application {
    int getHddSize();
    int getRam();
    int getMhz();
}
