package at.ac.tuwien.ldsc.group1;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;

import at.ac.tuwien.ldsc.group1.application.CsvParser;
import at.ac.tuwien.ldsc.group1.domain.Event;
import at.ac.tuwien.ldsc.group1.domain.EventType;
import at.ac.tuwien.ldsc.group1.domain.components.Application;

public class ParserTest {

	@Test
	public void ParsingTest(){
		String fileName = "TestScenario1.csv";
		CsvParser parser = new CsvParser();
		List<Application> appList = parser.parse(fileName);
		Assert.assertEquals(100, appList.size());
		
		
		Set<Event> events = new TreeSet<Event>();
		for(Application app : appList){
			
			
			long startTime = app.getTimeStamp();
			long stopTime = app.getTimeStamp()+app.getDuration();
			
			Event startEvent = new Event(startTime, EventType.START, app);
			Event stopEvent = new Event(stopTime, EventType.STOP, app);
						
			events.add(startEvent);
			events.add(stopEvent);
			
		}
		
		//see how the list is ordered
		for(Event e : events){
			System.out.println(e.toString());
		}
		System.out.println("Number of Events: "+ events.size());
		Assert.assertEquals(200, events.size());
	}
	
}
