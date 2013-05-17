package at.ac.tuwien.ldsc.group1;

import at.ac.tuwien.ldsc.group1.application.CsvParser;
import at.ac.tuwien.ldsc.group1.domain.Event;
import at.ac.tuwien.ldsc.group1.domain.EventType;
import at.ac.tuwien.ldsc.group1.domain.components.Application;
import com.google.common.collect.TreeMultiset;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring.xml")
public class ParserTest {

    @Autowired
    CsvParser parser;

    @Test
    public void ParsingTest() {

        List<Application> appList = parser.parse();
        Assert.assertEquals(100, appList.size());


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
        long lasttime = 0;
        for (Event e : events) {
            System.out.println(e.toString());
            Assert.assertTrue(e.getEventTime() >= lasttime);
            lasttime = e.getEventTime();
        }
        System.out.println("Number of Events: " + events.size());
        Assert.assertEquals(200, events.size());


    }

}
