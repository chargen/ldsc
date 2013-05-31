package at.ac.tuwien.ldsc.group1.ui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.annotation.Resource;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import at.ac.tuwien.ldsc.group1.application.CsvParser;
import at.ac.tuwien.ldsc.group1.application.CsvWriter;
import at.ac.tuwien.ldsc.group1.application.E2CElasticityManager;
import at.ac.tuwien.ldsc.group1.application.Scheduler;
import at.ac.tuwien.ldsc.group1.domain.CloudOverallInfo;

public class MainWindow {

	private JFrame frame;
	private final String defaultTarget = "data/TestScenario1.csv";
	JLabel lblPleaseSelectScenario;
	private File selectedFile = null;
	JSpinner spinner;
	JComboBox comboBox;
	private static E2CElasticityManager manager;
	private static CsvWriter overviewWriter;
	private static List<Scheduler> schedulers;
	private static CsvParser parser ;
	private static final ApplicationContext ac =  new FileSystemXmlApplicationContext("src/main/resources/spring.xml" );

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

	private static  void initializeBeans() {
		CsvWriter scenarioWriter = (CsvWriter) ac.getBean("scenarioWriter");
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
		} catch (Exception e){
			System.err.println("Look an feel error");
		}
		frame = new JFrame();
		frame.setBounds(100, 100, 721, 510);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JButton btnOpenFile = new JButton("Open File");
		btnOpenFile.addActionListener(new ActionListener() {
			

			public void actionPerformed(ActionEvent e) {
				
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File(defaultTarget));
				int returnVal = fileChooser.showOpenDialog(null);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
			        File f = fileChooser.getSelectedFile();
			        lblPleaseSelectScenario.setText("Selected Scenario: "+f.getName());
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
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"Scheduler1", "Scheduler2", "Scheduler3"}));
		comboBox.setBounds(213, 84, 125, 22);
		frame.getContentPane().add(comboBox);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(0, 336, 705, 136);
		frame.getContentPane().add(scrollPane);
		
		JTextPane textPane = new JTextPane();
		scrollPane.setViewportView(textPane);
		
		JButton btnRun = new JButton("Run");
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
//				selectedFile
				parser.setFileName(selectedFile.getAbsolutePath());
				
				
				for(Scheduler s : schedulers){
					s.setMaxNumberOfPhysicalMachines((Integer) spinner.getValue());
				}
				
				
				if(comboBox.getSelectedIndex() == 0){
					manager.startSpecificSimulation(0);
				}else if(comboBox.getSelectedIndex() == 1){
					manager.startSpecificSimulation(1);
				}else{
					manager.startSpecificSimulation(2);
				}
				
				

				for(CloudOverallInfo c : manager.getCloudOverAllInfos()){
					overviewWriter.writeLine(c);
				}
				overviewWriter.close();
				
			}
		});
		btnRun.setBackground(Color.RED);
		btnRun.setBounds(453, 84, 89, 23);
		frame.getContentPane().add(btnRun);
		
	}
}
