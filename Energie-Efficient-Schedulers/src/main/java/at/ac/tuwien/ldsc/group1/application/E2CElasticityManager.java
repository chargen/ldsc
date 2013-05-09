package at.ac.tuwien.ldsc.group1.application;

import at.ac.tuwien.ldsc.group1.domain.CloudOverallInfo;
import at.ac.tuwien.ldsc.group1.domain.Event;
import at.ac.tuwien.ldsc.group1.domain.EventType;
import at.ac.tuwien.ldsc.group1.domain.components.Application;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * This is our Energy-Efficient Cloud Elasticity Manager (E2CEM).
 * <br/>
 * It will initialise a scheduler and then control when an application is
 * added (based on the timestamp) and when it needs to be removed (based on the
 * duration).
 */
public class E2CElasticityManager {
    Scheduler scheduler;
    CsvParser csvParser;
    CsvWriter csvWriter;
    Set<Event> events;
    List<CloudOverallInfo> infoList = new ArrayList<>();

    public E2CElasticityManager(CsvParser parser, CsvWriter writer, Scheduler scheduler) {
        this.csvParser = parser;
        this.csvWriter = writer;
        this.scheduler = scheduler;
    }

    public void startSimulation() {
        //1. Get list of application from parser
        List<Application> appList = csvParser.parse();

        //2. Build interval list that transforms the list of applications from the parser
        //   into events
        events = new TreeSet<>();

        for (Application app : appList) {

            //For Each Application there is
            //One Event when the app STARTS
            //One Event when the app STOPS
            long startTime = app.getTimeStamp();
            long stopTime = app.getTimeStamp() + app.getDuration();

            Event startEvent = new Event(startTime, EventType.START, app);
            Event stopEvent = new Event(stopTime, EventType.STOP, app);

            events.add(startEvent);
            events.add(stopEvent);
        }

        scheduler.callScheduling(events);

        //close streams
        scheduler.finalize();
        infoList.add(scheduler.getOverAllInfo());
    }


    public List<CloudOverallInfo> getCloudOverAllInfos() {
        return this.infoList;
    }

}


