package at.ac.tuwien.ldsc.group1.application;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import at.ac.tuwien.ldsc.group1.domain.Event;
import at.ac.tuwien.ldsc.group1.domain.EventType;
import at.ac.tuwien.ldsc.group1.domain.components.Application;

/**
 * This is our Energy-Efficient Cloud Elasticity Manager (E2CEM).
 *
 * It will initialise a scheduler and then control when an application is
 * added (based on the timestamp) and when it needs to be removed (based on the
 * duration).
 */
public class E2CElasticityManager {
    Schedulable scheduler;
    CsvParser csvParser;
    CsvWriter csvWriter;

    public E2CElasticityManager(CsvParser parser, CsvWriter writer, Schedulable scheduler) {

    	this.csvParser = parser;
    	this.csvWriter = writer;
    	this.scheduler = scheduler;
    	
    	
    }

    public void startSimulation() {
        //1. Get list of application from parser
    	
    	//TODO where is the FileName coming from?
    	String fileName = "TestScenario1.csv";
//		CsvParser parser = new CsvParser();
		List<Application> appList = csvParser.parse(fileName);

        //2. Build interval list that transforms the list of applications from the parser
        //   into events
		
		Set<Event> events = new TreeSet<Event>();
		for(Application app : appList){
			
			//For Each Application there is 
			//One Event when the app STARTS
			//One Event when the app STOPS
			//
			//Store Events ordered by their eventTime
			
			long startTime = app.getTimeStamp();
			long stopTime = app.getTimeStamp()+app.getDuration();
			
			Event startEvent = new Event(startTime, EventType.START, app);
			Event stopEvent = new Event(stopTime, EventType.STOP, app);
			
			
			events.add(startEvent);
			events.add(stopEvent);
			
		}
		

        for(Event event : events) {
            //3. Feed it into the scheduler
        	//EVENTS ARE ORDERED
            scheduler.schedule(event);
        }
        
        //close streams
        scheduler.finalize();

        // Finally log summary information of cloud to output file 1
        //String info = scheduler.getSummaryInfo();
    }
}


