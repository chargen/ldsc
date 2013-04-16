package at.ac.tuwien.ldsc.group1.domain;

public interface Machine {
    /**
     * Return the amount of RAM in MB
     */
    int getRam();

    /**
     * Return the HddSize in MB
     */
    int getHddSize();

    /**
     * Return the speed of the machine in MHz.
     */
    int getCpuInMhz();
}
