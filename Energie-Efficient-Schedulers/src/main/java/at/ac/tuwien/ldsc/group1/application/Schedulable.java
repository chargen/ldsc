package at.ac.tuwien.ldsc.group1.application;

import java.util.Set;

import at.ac.tuwien.ldsc.group1.domain.CloudOverallInfo;
import at.ac.tuwien.ldsc.group1.domain.Event;
import at.ac.tuwien.ldsc.group1.domain.components.Application;
import at.ac.tuwien.ldsc.group1.domain.exceptions.ResourceUnavailableException;
import at.ac.tuwien.ldsc.group1.domain.exceptions.SchedulingNotPossibleException;

public interface Schedulable {
    void schedule(Event event) throws SchedulingNotPossibleException;

    /**
     * Template method for use inside the scheduler
     * this function is called by the schedule method.
     * @throws SchedulingNotPossibleException 
     */
    void addApplication(Application application) throws ResourceUnavailableException, SchedulingNotPossibleException;

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

	void callScheduling(Set<Event> events);
	
	public CloudOverallInfo getOverAllInfo();
}