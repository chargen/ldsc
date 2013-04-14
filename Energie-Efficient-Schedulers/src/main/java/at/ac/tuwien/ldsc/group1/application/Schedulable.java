package at.ac.tuwien.ldsc.group1.application;

import at.ac.tuwien.ldsc.group1.domain.Application;

public interface Schedulable {
    void schedule();
    void addApplication(Application application);
    void removeApplication(Application application);
}
