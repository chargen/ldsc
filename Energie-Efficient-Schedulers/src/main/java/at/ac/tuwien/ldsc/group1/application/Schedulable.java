package at.ac.tuwien.ldsc.group1.application;

import at.ac.tuwien.ldsc.group1.domain.Event;
import at.ac.tuwien.ldsc.group1.domain.components.Application;

public interface Schedulable {
    void schedule(Event event);

    /**
     * Template method for use inside the scheduler
     * this function is called by the schedule method.
     */
    void addApplication(Application application);

    /**
     * Template method for use inside the scheduler,
     * this function is called by the schedule method.
     */
    void removeApplication(Application application);
}