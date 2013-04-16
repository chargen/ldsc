package at.ac.tuwien.ldsc.group1.application;

import java.util.List;

import at.ac.tuwien.ldsc.group1.domain.Application;
import at.ac.tuwien.ldsc.group1.domain.Event;
import at.ac.tuwien.ldsc.group1.domain.EventType;
import at.ac.tuwien.ldsc.group1.domain.PhysicalMachine;

public class Scheduler1 implements Schedulable {
    List<Application> applications;
    List<PhysicalMachine> physicalMachines;

    @Override
    public void schedule(Event event) {
        if(event.getEventType() == EventType.START) {
            //TODO: check resources
            this.addApplication(event.getApplication());
        } else {
            this.removeApplication(event.getApplication());
        }
    }

    @Override
    public void addApplication(Application application) {
        //1. make a decision on which virtual machine this application will run

        //Finally: Log current clould utilization details to output file 2
    }

    @Override
    public void removeApplication(Application application) {
        //1. find the virtual machine on which this application runs
        //   and remove it.

        //Finally: Log current clould utilization details to output file 2
    }

}
