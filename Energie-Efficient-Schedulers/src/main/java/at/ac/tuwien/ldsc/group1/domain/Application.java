package at.ac.tuwien.ldsc.group1.domain;

/**
 * An interface for an application that runs in a virtual machine.
 * Based on the assignment an application is always created with fixed
 * size, ram and MHz requirements. Therefore this inteface is read-only.
 *
 * @author Sebastian Geiger
 */
public interface Application {
    /**
     * Size of the hard disks that the application requires.
     */
    int getHddSize();

    /**
     * Amount of RAM that the application needs.
     */
    int getRam();

    /**
     * CPU Requirements of the application in [MHz].
     * @return
     */
    int getMhz();

    /**
     * Returns the timestamp when the application was started in the cloud/
     */
    int getTimestamp();

    /**
     * Returns the duration in [ms] that the application needs to run.
     */
    int getDuration();
}
