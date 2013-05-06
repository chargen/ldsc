package at.ac.tuwien.ldsc.group1;

import org.junit.Test;

import at.ac.tuwien.ldsc.group1.application.CsvParser;
import at.ac.tuwien.ldsc.group1.application.CsvWriter;
import at.ac.tuwien.ldsc.group1.application.E2CElasticityManager;
import at.ac.tuwien.ldsc.group1.application.Scheduler;
import at.ac.tuwien.ldsc.group1.application.Scheduler1;
import at.ac.tuwien.ldsc.group1.domain.CloudOverallInfo;

public class E2CElasticityManagerTest {
	
	public final static String testOutput = "testOutput.csv";
	public final static String testOutput2 = "testOutput2.csv";
	
	@Test
	public void testSimulation(){
        String fileName = "TestScenario1.csv";
        CsvParser parser= new CsvParser(fileName);
        CsvWriter writer = new CsvWriter(testOutput);
        writer.writeHeader1();
        Scheduler scheduler = new Scheduler1(writer);
		scheduler.setMaxNumberOfPhysicalMachines(5);
		E2CElasticityManager manager = new E2CElasticityManager(parser,writer,scheduler);
		manager.startSimulation();
		System.out.println("Why the fuck is this text not in console?");
		
		CsvWriter writer2 = new CsvWriter(testOutput2);
		writer2.writeHeader2();
		
		for(CloudOverallInfo c : manager.getCloudOverAllInfos()){
			writer2.writeCsv(c);
		}
		
		writer2.close();
		
	}
	

}
