package at.ac.tuwien.ldsc.group1.domain;

public enum EventType {
    START ("Start application"),
    STOP ("Stop application");

    String description;

    EventType(String description) {
        this.description = description;
    }

    String getDescription() {
        return description;
    }
}
