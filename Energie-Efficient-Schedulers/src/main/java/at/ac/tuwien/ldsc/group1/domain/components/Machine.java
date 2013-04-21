package at.ac.tuwien.ldsc.group1.domain.components;



public interface Machine extends Component, Composite {
    /**
     * Returns the power consumption of this machine
     */
    double getPowerConsumption();

    /**
     * Return the maximum available RAM space of the machine,
     * that can be allocated to its child components.
     */
    int getRamAvailable();

    /**
     * Return the maximum available cpu of the machine,
     * that can be allocated to its child components.
     */
    int getCpuAvailable();

    /**
     * Return the maximum available hdd space of the machine,
     * that can be allocated to its child components.
     */
    int getHddAvailable();

    /**
     * This function returns the parent object of a component.
     */
    Machine getParent();
}
