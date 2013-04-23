package at.ac.tuwien.ldsc.group1;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import at.ac.tuwien.ldsc.group1.application.CsvParser;
import at.ac.tuwien.ldsc.group1.domain.components.Application;

public class ParserTest {

	@Test
	public void ParsingTest(){
		String fileName = "TestScenario1.csv";
		CsvParser parser = new CsvParser();
		List<Application> appList = parser.parse(fileName);
		Assert.assertEquals(100, appList.size());
		
	}
	
}
