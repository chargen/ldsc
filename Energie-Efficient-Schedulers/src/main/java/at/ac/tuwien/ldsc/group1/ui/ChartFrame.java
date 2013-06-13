package at.ac.tuwien.ldsc.group1.ui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities;

import javax.swing.*;
import java.awt.*;

/**
 * This class creates a new window which displays two line charts.
 * One line chart is a bigger one, where the other one is a smaller
 * chart thats embedded into the bigger one.
 */
public class ChartFrame extends JFrame {

    public ChartFrame(XYSeries seriesVm,
                      XYSeries seriesPm,
                      XYSeries seriesConsumption) {
        XYSeriesCollection dataSet1 = new XYSeriesCollection();
        dataSet1.addSeries(seriesConsumption);
        XYSeriesCollection dataSet2 = new XYSeriesCollection();
        dataSet2.addSeries(seriesVm);
        dataSet2.addSeries(seriesPm);
        JFreeChart chart = createTimeSeriesChart(dataSet1, dataSet2);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBounds(0, 0, 300, 300);

        this.getContentPane().add(chartPanel);
        this.setSize(new Dimension(500, 500));
        RefineryUtilities.centerFrameOnScreen(this);
    }

    private JFreeChart createTimeSeriesChart(XYSeriesCollection dataSet1, XYSeriesCollection dataSet2) {
        JFreeChart xyLineChart = ChartFactory.createXYLineChart(
                "CloudSimulation Chart",      // Chart title
                "TimeStamp",                  // x-Axis label
                "Range",                      // y-Axis label
                dataSet1,                     // Data
                PlotOrientation.VERTICAL,
                true,                         // Include legend
                true,                         // Tooltips
                true                          // Urls
        );
        XYPlot plot = xyLineChart.getXYPlot();

        final NumberAxis axis2 = new NumberAxis("Machines");
        axis2.setAutoRangeIncludesZero(false);
        plot.setRangeAxis(1, axis2)  ;
        plot.setDataset(1, dataSet2);
        plot.mapDatasetToRangeAxis(1, 1);


        final XYItemRenderer renderer = plot.getRenderer();
        renderer.setBaseToolTipGenerator(StandardXYToolTipGenerator.getTimeSeriesInstance());
        if (renderer instanceof StandardXYItemRenderer) {
            final StandardXYItemRenderer rr = (StandardXYItemRenderer) renderer;
            rr.setSeriesShapesFilled(0,true);
        }

        final StandardXYItemRenderer renderer2 = new StandardXYItemRenderer();
        renderer2.setSeriesPaint(0, Color.black);
        renderer.setBaseToolTipGenerator(StandardXYToolTipGenerator.getTimeSeriesInstance());
        plot.setRenderer(1, renderer2);



        return xyLineChart;
    }
}
