package at.ac.tuwien.ldsc.group1.ui;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class StartUpSimulationGUI {

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        final ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
        context.getBean(MainWindow.class);
    }
}
