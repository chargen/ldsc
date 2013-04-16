package at.ac.tuwien.ldsc.group1.application;

import java.util.List;

import at.ac.tuwien.ldsc.group1.domain.Event;

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

    }

    public void startSimulation() {
        //1. Get list of application from parser

        //2. Build interval list that transforms the list of applications from the parser
        //   into events
        List<Event> events = null;

        for(Event event : events) {
            //3. Feed it into the scheduler
            scheduler.schedule(event);
        }

        // Finally log summary information of cloud to output file 1
        //String info = scheduler.getSummaryInfo();
    }
}


