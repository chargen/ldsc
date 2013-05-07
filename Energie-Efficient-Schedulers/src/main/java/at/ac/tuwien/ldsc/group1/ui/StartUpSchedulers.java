package at.ac.tuwien.ldsc.group1.ui;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class StartUpSchedulers {

    /**
     * Start scheduler one
     * @param args
     */
    public static void main(String[] args) {
        //Initialize Spring Application Context
        new ClassPathXmlApplicationContext("spring.xml");

        //2. Initialize Domain objects


        //3. Initialize Scheduler with number of physical machines


        //Loop for Scheduler 1 to 3:
            //Start E2CE instance
            //Wait till scheduler has finished
    }
}
