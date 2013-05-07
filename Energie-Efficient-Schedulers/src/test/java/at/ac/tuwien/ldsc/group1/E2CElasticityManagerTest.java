package at.ac.tuwien.ldsc.group1;

import at.ac.tuwien.ldsc.group1.application.CsvParser;
import at.ac.tuwien.ldsc.group1.application.CsvWriter;
import at.ac.tuwien.ldsc.group1.application.E2CElasticityManager;
import at.ac.tuwien.ldsc.group1.application.Scheduler;
import at.ac.tuwien.ldsc.group1.domain.CloudOverallInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring.xml")
public class E2CElasticityManagerTest {
    @Autowired CsvParser parser;

    @Autowired
    @Qualifier("scenarioWriter")
    CsvWriter scenarioWriter;
    
    @Autowired
    @Qualifier("overviewWriter")
    CsvWriter overviewWriter;
    
    @Autowired Scheduler scheduler;

    @Test
    public void testSimulation() {
        scheduler.setMaxNumberOfPhysicalMachines(5);
        E2CElasticityManager manager = new E2CElasticityManager(parser, scenarioWriter, scheduler);
        manager.startSimulation();

        for(CloudOverallInfo c : manager.getCloudOverAllInfos()){
            overviewWriter.writeLine(c);
        }

        overviewWriter.close();

    }
}
