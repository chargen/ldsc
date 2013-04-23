package at.ac.tuwien.ldsc.group1.application;

import java.util.List;

import at.ac.tuwien.ldsc.group1.domain.Event;
import at.ac.tuwien.ldsc.group1.domain.EventType;
import at.ac.tuwien.ldsc.group1.domain.components.Application;
import at.ac.tuwien.ldsc.group1.domain.components.PhysicalMachine;
import at.ac.tuwien.ldsc.group1.domain.components.VirtualMachine;

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
    	
    	//A.) Create VM
    	//B.) Fill PM with VM until they are full
    	//	B/1.) Start new PM if needed
    	//	B/2.) Optimize PM selection
    	

        //Finally: Log current clould utilization details to output file 2
    }

    @Override
    public void removeApplication(Application application) {
        //1. find the virtual machine on which this application runs
        //   and remove it.

    	//C.) Kill VM if not needed anymore (No App running on it)
    	//D.) Kill PM if not needed anymore (No VM running on it)
    	
        //Finally: Log current clould utilization details to output file 2
    }

}
