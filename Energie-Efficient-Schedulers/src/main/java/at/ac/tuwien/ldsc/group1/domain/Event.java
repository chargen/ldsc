package at.ac.tuwien.ldsc.group1.domain;

public class Event {
    int eventTime; // time on the event time line.
    EventType eventType;
    Application application;

    public Event(int eventTime, EventType eventType, Application application) {
        this.eventTime = eventTime;
        this.eventType = eventType;
        this.application = application;
    }

    public int getEventTime() {
        return eventTime;
    }
    public EventType getEventType() {
        return eventType;
    }
    public Application getApplication() {
        return application;
    }
}
