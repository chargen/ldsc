package at.ac.tuwien.ldsc.group1.ui;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.general.Series;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import java.awt.FlowLayout;

public class ChartFrame extends JFrame {

	private JPanel contentPane;
	private XYSeriesCollection fulldataset;
	private XYSeriesCollection dataset1;
	private XYSeriesCollection dataset2;
	
	/**
	 * Create the frame.
	 */
	public ChartFrame() {
//		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 761, 645);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
	}

	

	public ChartFrame(XYSeries seriesVm, XYSeries seriesPm,
			XYSeries seriesConsumtion) {
		dataset1 = new XYSeriesCollection();
		dataset1.addSeries(seriesVm);
		dataset1.addSeries(seriesPm);
		dataset2 = new XYSeriesCollection();
		dataset2.addSeries(seriesConsumtion);
		
		JFreeChart chart = createTimeSeriesChart(dataset1);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBounds(300, 300, 300, 300);
        this.getContentPane().add(chartPanel);
        
        JFreeChart chart1 = createTimeSeriesChart(dataset2);
        ChartPanel chartPanel1 = new ChartPanel(chart1);
        chartPanel1.setBounds(0, 0, 300, 300);
        this.getContentPane().add(chartPanel1);
	}

	private JFreeChart createTimeSeriesChart(XYSeriesCollection dataset) {
	
      return ChartFactory.createXYLineChart(
          "LogChart",  // chart title
          "TimeStamp",
          "Range",
          dataset,         // data
          PlotOrientation.VERTICAL,
          true,            // include legend
          true,            // tooltips
          true             // urls
      );
	}
}
