package at.ac.tuwien.ldsc.group1.domain.federation;

import at.ac.tuwien.ldsc.group1.domain.components.Application;
import at.ac.tuwien.ldsc.group1.domain.components.ApplicationImpl;

public class FederationPartner {
	
	
	public Application getSourceInApplication(ScenarioType type){
		
		//todo randomize
		
		Application app = new ApplicationImpl(50, 100, 100, 1000, 0);
		return app;
	}
	public boolean deploySourceOutApplication(Application app){
		return true;
	}

}
