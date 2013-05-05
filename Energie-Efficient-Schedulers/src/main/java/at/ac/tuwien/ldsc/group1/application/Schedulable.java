package at.ac.tuwien.ldsc.group1.application;

import at.ac.tuwien.ldsc.group1.domain.Event;
import at.ac.tuwien.ldsc.group1.domain.components.Application;
import at.ac.tuwien.ldsc.group1.domain.exceptions.ResourceUnavailableException;
import at.ac.tuwien.ldsc.group1.domain.exceptions.SchedulingNotPossibleException;

public interface Schedulable {
	/**
	 * This method is called by the Elasticity manager to schedule events.
	 */
    void schedule(Event event);

    /**
     * Template method for use inside the scheduler
     * this function is called by the schedule method.
     * 
     */
    void addApplication(Application application) throws ResourceUnavailableException;

    /**
     * Template method for use inside the scheduler,
     * this function is called by the schedule method.
     */
    void removeApplication(Application application);
    /**
     *  to close the streams
     */
    void finalize();
    
    void setMaxNumberOfPhysicalMachines(int nr);
}