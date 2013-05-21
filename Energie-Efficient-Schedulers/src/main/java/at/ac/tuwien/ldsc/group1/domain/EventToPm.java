package at.ac.tuwien.ldsc.group1.domain;

import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.ldsc.group1.domain.components.PhysicalMachine;

public class EventToPm {
    List<Event> events = new ArrayList<>();
    PhysicalMachine pm;
    
    public EventToPm() {
       
    }
    
    public List<Event> getEvents() {
        return events;
    }
    public void setEvent(List<Event> event) {
        this.events = event;
    }
    public PhysicalMachine getPm() {
        return pm;
    }
    public void setPm(PhysicalMachine pm) {
        this.pm = pm;
    }
    public void addEvent(Event event){
        this.events.add(event);
    }
    
}
