package at.ac.tuwien.ldsc.group1.application;

import at.ac.tuwien.ldsc.group1.domain.components.Application;
import at.ac.tuwien.ldsc.group1.domain.components.ApplicationImpl;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Read a CSV file with Application definition and create an instances of
 * an Applications
 *
 * @author Sebastian Geiger, Peter Patonai
 */
public class CsvParser {
    BufferedReader bufferedReader;
    List<Application> appList;
    DataInputStream inputStream;
    String fileNameCache;

    public CsvParser(String fileName) {
    	this.fileNameCache = fileName;
        appList = new ArrayList<>();
        try {
            FileInputStream fis = new FileInputStream(fileName);
            inputStream = new DataInputStream(fis);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        } catch (FileNotFoundException e) {
            System.out.println("Unable to open file: " + fileName);
        }
    }
    
    public void setFileName(String fileName){
    	this.fileNameCache = fileName;
        appList = new ArrayList<>();
        try {
            FileInputStream fis = new FileInputStream(fileName);
            inputStream = new DataInputStream(fis);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        } catch (FileNotFoundException e) {
            System.out.println("Unable to open file: " + fileName);
        }
    }

    public List<Application> parse() {
    	
//    	if(inputStream.)
    	
        try {
            String strLine;
            while ((strLine = bufferedReader.readLine()) != null) {
                String[] parts = strLine.split(";");
                try {
                    Long timeStamp = Long.parseLong(parts[0]);
                    Integer hddSize = Integer.parseInt(parts[1]);
                    Integer ram = Integer.parseInt(parts[2]);
                    Integer cpuInMhz = Integer.parseInt(parts[3]);
                    Long duration = Long.parseLong(parts[4]);

                    Application app = new ApplicationImpl(ram, hddSize, cpuInMhz, duration, timeStamp);
                    appList.add(app);
                } catch (NumberFormatException e) {
                    continue; //skip header in the first row
                }
            }

//            inputStream.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }

        return appList;

    }
}
