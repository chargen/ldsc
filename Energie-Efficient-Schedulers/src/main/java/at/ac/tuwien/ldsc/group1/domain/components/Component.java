package at.ac.tuwien.ldsc.group1.domain.components;


/**
 * A component represents a part of the cloud with resource requirements.
 * It can be an application, a virtual machine or a physical machine.
 * This interface provides methods for the different resource requirements
 * of a component, such as RAM, HDD Size, Cpu (in [MHz]) or the power
 * consumption (in [W/ms]).
 *
 * The advantage of having a general component object that extends over
 * both the application and the machine objects makes it easy for us
 * to query the resource consumptions for all objects.
 *
 * @author Sebastian Geiger
 *
 */
public interface Component {

    /**
     * Start the machine
     */
    void start();

    /**
     * Stop the machine
     */
    void stop();

    /**
     * Get the current RAM requirement of this component
     * (in [MB]).
     * @return
     */
    int getRam();

    /**
     * Get the current harddisk requirement of this
     * component (in [MB]).
     * @return
     */
    int getHddSize();

    /**
     * Get the current CPU requirement of this component
     * (in [Mhz]).
     * @return
     */
    int getCpuInMhz();


    int getId();
}
