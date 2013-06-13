package at.ac.tuwien.ldsc.group1;

import at.ac.tuwien.ldsc.group1.application.CsvParser;
import at.ac.tuwien.ldsc.group1.domain.Event;
import at.ac.tuwien.ldsc.group1.domain.EventType;
import at.ac.tuwien.ldsc.group1.domain.components.Application;
import com.google.common.collect.TreeMultiset;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring.xml")
public class ParserTest {

    @Resource(name="csvParser2")
    CsvParser parser;

    @Test
    public void ParsingTest() {

        List<Application> appList = parser.parse();

        TreeMultiset<Event> events = TreeMultiset.create();
        for (Application app : appList) {

            long startTime = app.getTimeStamp();
            long stopTime = app.getTimeStamp() + app.getDuration();

            Event startEvent = new Event(startTime, EventType.START, app);
            Event stopEvent = new Event(stopTime, EventType.STOP, app);

            events.add(startEvent);
            events.add(stopEvent);

        }

        //see how the list is ordered
        long lastTime = 0;
        for (Event e : events) {
            System.out.println(e.toString());
            // assert that event times are monotonically increasing
            Assert.assertTrue(e.getEventTime() >= lastTime);
            lastTime = e.getEventTime();
        }
        System.out.println("Number of Events: " + events.size());
    }

}
