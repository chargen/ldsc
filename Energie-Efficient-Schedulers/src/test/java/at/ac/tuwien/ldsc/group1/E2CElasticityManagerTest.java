package at.ac.tuwien.ldsc.group1;

import org.junit.Test;

import at.ac.tuwien.ldsc.group1.application.CsvParser;
import at.ac.tuwien.ldsc.group1.application.CsvWriter;
import at.ac.tuwien.ldsc.group1.application.E2CElasticityManager;
import at.ac.tuwien.ldsc.group1.application.Schedulable;
import at.ac.tuwien.ldsc.group1.application.Scheduler1;

public class E2CElasticityManagerTest {
	
	public final static String testOutput = "testOutput.csv";
	
	@Test
	public void testSimulation(){
        String fileName = "TestScenario1.csv";
        CsvParser parser= new CsvParser(fileName);
        CsvWriter writer = new CsvWriter(testOutput);
        Schedulable scheduler = new Scheduler1(writer);
		scheduler.setMaxNumberOfPhysicalMachines(10);
		E2CElasticityManager manager = new E2CElasticityManager(parser,writer,scheduler);
		manager.startSimulation();
		
	}
	

}
