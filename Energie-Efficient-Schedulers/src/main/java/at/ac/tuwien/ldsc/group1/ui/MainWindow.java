package at.ac.tuwien.ldsc.group1.ui;

import at.ac.tuwien.ldsc.group1.application.CsvParser;
import at.ac.tuwien.ldsc.group1.application.CsvWriter;
import at.ac.tuwien.ldsc.group1.application.E2CElasticityManager;
import at.ac.tuwien.ldsc.group1.application.Scheduler;
import at.ac.tuwien.ldsc.group1.domain.CloudOverallInfo;
import at.ac.tuwien.ldsc.group1.domain.CloudStateInfo;
import at.ac.tuwien.ldsc.group1.ui.interfaces.GuiLogger;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

public class MainWindow implements GuiLogger {

    private JFrame frame;
    private final String defaultTarget = "data/TestScenario1.csv";
    JLabel lblPleaseSelectScenario;
    private File selectedFile = null;
    JSpinner spinner;
    JComboBox comboBox;
    private static E2CElasticityManager manager;
    private static CsvWriter overviewWriter;
    private static List<Scheduler> schedulers;
    private static CsvParser parser;
    private static CsvWriter scenarioWriter;
    private static CsvWriter scenarioWriter2;
    private static CsvWriter scenarioWriter3;
    private static CsvWriter scenarioWriter4;
    private JTextPane textPane;
    private JFreeChart chart;
    XYSeries seriesVm = new XYSeries("VMs");
    XYSeries seriesPm = new XYSeries("PMs");
    XYSeries seriesConsumtion = new XYSeries("Consumption");

    private static final ApplicationContext ac = new FileSystemXmlApplicationContext("src/main/resources/spring.xml");

    JLabel lblNumberOfFederationpartners;
    JSpinner spinner_1;


    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        initializeBeans();
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    MainWindow window = new MainWindow();

                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static void initializeBeans() {
        scenarioWriter = (CsvWriter) ac.getBean("scenarioWriter");
        scenarioWriter2 = (CsvWriter) ac.getBean("scenarioWriter2");
        scenarioWriter3 = (CsvWriter) ac.getBean("scenarioWriter3");
        scenarioWriter4 = (CsvWriter) ac.getBean("scenarioWriter4");

        overviewWriter = (CsvWriter) ac.getBean("overviewWriter");

        parser = (CsvParser) ac.getBean("csvParser");
        schedulers = (List<Scheduler>) ac.getBean("schedulers");

        manager = new E2CElasticityManager(parser, scenarioWriter, schedulers);
//	        manager.startSimulations();


    }

    /**
     * Create the application.
     */
    public MainWindow() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e) {
            System.err.println("Look an feel error");
        }
        frame = new JFrame();
        frame.setBounds(100, 100, 721, 789);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JButton btnOpenFile = new JButton("Open File");
        btnOpenFile.addActionListener(new ActionListener() {


            public void actionPerformed(ActionEvent e) {

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(defaultTarget));
                int returnVal = fileChooser.showOpenDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File f = fileChooser.getSelectedFile();
                    lblPleaseSelectScenario.setText("Selected Scenario: " + f.getName());
                    selectedFile = f;
                }
            }
        });

        btnOpenFile.setBounds(56, 13, 95, 25);
        frame.getContentPane().add(btnOpenFile);

        lblPleaseSelectScenario = new JLabel("Please Select Scenario...(default Selected: TestScenario1)");
        lblPleaseSelectScenario.setBounds(213, 22, 287, 16);
        frame.getContentPane().add(lblPleaseSelectScenario);

        spinner = new JSpinner();
        spinner.setModel(new SpinnerNumberModel(new Integer(5), new Integer(1), null, new Integer(1)));
        spinner.setBounds(213, 51, 29, 20);
        frame.getContentPane().add(spinner);

        JLabel lblNumberOPms = new JLabel("Number of PMs:");
        lblNumberOPms.setBounds(66, 51, 85, 16);
        frame.getContentPane().add(lblNumberOPms);

        JLabel lblScheduler = new JLabel("Scheduler:");
        lblScheduler.setBounds(65, 87, 86, 16);
        frame.getContentPane().add(lblScheduler);

        comboBox = new JComboBox();
        comboBox.setModel(new DefaultComboBoxModel(new String[]{"Scheduler1", "Scheduler2", "Scheduler3", "Federation"}));
        comboBox.setBounds(213, 84, 125, 22);
        frame.getContentPane().add(comboBox);

        comboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (comboBox.getSelectedIndex() == 3) {
                    lblNumberOfFederationpartners.setVisible(true);
                    spinner_1.setVisible(true);
                } else {
                    lblNumberOfFederationpartners.setVisible(false);
                    spinner_1.setVisible(false);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(0, 413, 705, 338);
        frame.getContentPane().add(scrollPane);


        textPane = new JTextPane();
        scrollPane.setViewportView(textPane);

        JButton btnRun = new JButton("Run");
        btnRun.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

//				selectedFile
                if (selectedFile != null) parser.setFileName(selectedFile.getAbsolutePath());
                scenarioWriter.setGuiLogger(MainWindow.this);
                scenarioWriter2.setGuiLogger(MainWindow.this);
                scenarioWriter3.setGuiLogger(MainWindow.this);
                scenarioWriter4.setGuiLogger(MainWindow.this);

                for (Scheduler s : schedulers) {
                    s.setMaxNumberOfPhysicalMachines((Integer) spinner.getValue());
                    s.setNumberOfFederationPartners((Integer) spinner_1.getValue());
                }


                if (comboBox.getSelectedIndex() == 0) {
                    manager.startSpecificSimulation(0);
                } else if (comboBox.getSelectedIndex() == 1) {
                    manager.startSpecificSimulation(1);
                } else if (comboBox.getSelectedIndex() == 2) {
                    manager.startSpecificSimulation(2);
                } else {
                    manager.startSpecificSimulation(3);
                }


                for (CloudOverallInfo c : manager.getCloudOverAllInfos()) {
                    overviewWriter.writeLine(c);
                }
                overviewWriter.close();

            }
        });
        btnRun.setBackground(Color.RED);
        btnRun.setBounds(453, 84, 89, 23);
        frame.getContentPane().add(btnRun);

        lblNumberOfFederationpartners = new JLabel("Number of FederationPartners: ");
        lblNumberOfFederationpartners.setBounds(66, 131, 162, 14);
        frame.getContentPane().add(lblNumberOfFederationpartners);
        lblNumberOfFederationpartners.setVisible(false);
        spinner_1 = new JSpinner();
        spinner_1.setBounds(252, 128, 29, 20);
        frame.getContentPane().add(spinner_1);
        spinner_1.setVisible(false);
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        JMenu mnChart = new JMenu("Chart");
        menuBar.add(mnChart);

        JMenuItem mntmPlotresult = new JMenuItem("PlotResult");
        mnChart.add(mntmPlotresult);
        mntmPlotresult.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
//				XYSeriesCollection dataset = new XYSeriesCollection();
//		        dataset.addSeries(seriesVm);
//		        dataset.addSeries(seriesPm);
//		        dataset.addSeries(seriesConsumtion);
                ChartFrame ch = new ChartFrame(seriesVm, seriesPm, seriesConsumtion);
                ch.setVisible(true);
            }
        });

    }

    @Override
    public void writeGuiLog(CloudStateInfo guilog) {
        seriesVm.add(guilog.getTimestamp(), guilog.getRunningVMs());
        seriesPm.add(guilog.getTimestamp(), guilog.getRunningPMs());
        seriesConsumtion.add(guilog.getTimestamp(), guilog.getTotalPowerConsumption());
        this.textPane.setText(this.textPane.getText() + "\n" + guilog.toString());

    }
}
