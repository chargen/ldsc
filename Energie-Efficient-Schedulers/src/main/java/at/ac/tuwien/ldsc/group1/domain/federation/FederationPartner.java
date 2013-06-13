package at.ac.tuwien.ldsc.group1.domain.federation;

import at.ac.tuwien.ldsc.group1.domain.components.Application;
import at.ac.tuwien.ldsc.group1.domain.components.ApplicationImpl;

public class FederationPartner {


    public Application getSourceInApplication(ScenarioType type) {
        //TODO randomize
        Application app = new ApplicationImpl(65, 100, 100, 1000, 1000);
        return app;
    }

    public boolean deploySourceOutApplication(Application app) {
        return Math.random() <= 0.05;
    }

}
