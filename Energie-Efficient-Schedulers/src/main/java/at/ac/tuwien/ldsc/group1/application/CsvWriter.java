package at.ac.tuwien.ldsc.group1.application;

import at.ac.tuwien.ldsc.group1.domain.CloudOverallInfo;
import at.ac.tuwien.ldsc.group1.domain.CloudStateInfo;
import at.ac.tuwien.ldsc.group1.domain.WriterType;
import org.joda.time.DateTime;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class CsvWriter {
    File file;
    FileWriter fw;
    BufferedWriter bw;

    /**
     * @param baseName The base name of the file (the filename will in the style <pre>baseName-yyyy-MM-dd.csv</pre>
     *                 <br/>Where yyyy-MM-dd is the current date
     */
    public CsvWriter(String baseName, WriterType type) {
        DateTime date = new DateTime();
        String dateString = date.toString("yyyy-MM-dd");
        try {
            this.file = new File(baseName + "-" + dateString + ".csv");

            // if file doesn't exist, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            fw = new FileWriter(file.getAbsoluteFile());
            bw = new BufferedWriter(fw);

            switch (type) {
                case SCENARIO:
                    writeScenarioHeader();
                    break;
                case OVERVIEW:
                    writeOverviewHeader();
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeScenarioHeader() {
        try {
            bw.write("Timestamp; TotalRAM; TotalCPU; TotalSize; RunningPMs; RunningVMs; TotalPowerConsumption; InSourced; OutSourced");
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeOverviewHeader() {
        try {
            bw.write("Scheduler;Scenario;TotalPMs;TotalVMs;TotalDuration;TotalPowerConsumption;TotalInSourced;TotalOutSourced");
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeLine(CloudStateInfo cloudInfo) {
        try {
            bw.write(cloudInfo.toString());
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void writeLine(CloudOverallInfo cloudInfo) {
        try {
            bw.write(cloudInfo.toString());
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void close() {
        try {
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
