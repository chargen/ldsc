package at.ac.tuwien.ldsc.group1.domain;

import at.ac.tuwien.ldsc.group1.domain.components.Application;

public class Event implements Comparable<Event> {
    long eventTime; // time on the event time line.
    EventType eventType;
    Application application;
    boolean toBeSkipped = false;

    public Event(long eventTime, EventType eventType, Application application) {
        this.eventTime = eventTime;
        this.eventType = eventType;
        this.application = application;
    }

    public long getEventTime() {
        return eventTime;
    }
    public EventType getEventType() {
        return eventType;
    }
    public Application getApplication() {
        return application;
    }

	public boolean isToBeSkipped() {
		return toBeSkipped;
	}

	public void setToBeSkipped(boolean toBeSkipped) {
		this.toBeSkipped = toBeSkipped;
	}
	
	public void setEventTime(long time) {
        eventTime = time;
    }
	
	@Override
	public int compareTo(Event e) {
		final int BEFORE = -1;
		final int EQUAL = 0;
		final int AFTER = 1;


        if (this.eventTime < e.getEventTime()) {
            return BEFORE;
        } else if (this.eventTime > e.getEventTime()) {
            return AFTER;
        } else {
            if (this.getEventType().equals(EventType.STOP) && e.getEventType().equals(EventType.START)) {
                return BEFORE;
            } else if (this.getEventType().equals(EventType.START) && e.getEventType().equals(EventType.STOP)) {
                return AFTER;
            } else {
                // Two events should never be equal unless they are the same object, we are generating a new event
                // for every application, even if all its properties are equal these are still two separate events,
                // so we will use the id of the application to enforce inequality.
                return Integer.signum(this.getApplication().getId() - e.getApplication().getId());
            }
        }
    }

    @Override
	public String toString(){
		return "[EventTime: "+this.eventTime + "SKIP" + this.isToBeSkipped() +" | EventType: "+ this.eventType.getDescription() +" | Application: " +
                "[ Id: " + this.application.getId() +
                ", Cpu: " + this.application.getCpuInMhz() +
                ", Dur: " + this.application.getDuration() +
                ", Size: " + this.application.getHddSize() +
                ", Ram: " + this.application.getRam() +
                ", TimeStamp: " + this.application.getTimeStamp() +
                " ] ]";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((application == null) ? 0 : application.hashCode());
		result = prime * result + (int) (eventTime ^ (eventTime >>> 32));
		result = prime * result
				+ ((eventType == null) ? 0 : eventType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Event other = (Event) obj;
		if (application == null) {
			if (other.application != null)
				return false;
		} else if (!application.equals(other.application))
			return false;
		if (eventTime != other.eventTime)
			return false;
		if (eventType != other.eventType)
			return false;
		return true;
	}
}
