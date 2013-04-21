package at.ac.tuwien.ldsc.group1.domain.components;



public interface Machine extends Component, Composite {
    /**
     * Returns the power consumption of this machine
     */
    double getPowerConsumption();

    /**
     * The max HDD size of this machine
     */
    int getHddMax();

    /**
     * The max RAM of this machine
     */
    int getRamMax();

    /**
     * The max CPU of this machine
     */
    int getCpuInMhzMax();
}
