package at.ac.tuwien.ldsc.group1.application;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import at.ac.tuwien.ldsc.group1.domain.CloudOverallInfo;
import at.ac.tuwien.ldsc.group1.domain.CloudStateInfo;

public class CsvWriter {
    String filename;
    File file;
	FileWriter fw;
	BufferedWriter bw;
	boolean headerAdded = false;

    public CsvWriter(String filename) {
    	this.filename = filename;
    	try {
    		this.file = new File(filename);

    		// if file doesnt exists, then create it
    		if (!file.exists()) {
    			file.createNewFile();
    		}
    		
    		fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			
    		
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }
    
    public void writeHeader1(){
    	if(!headerAdded){
    		try {
    			bw.write("Timestamp; TotalRAM; TotalCPU; TotalSize; RunningPMs; RunningVMs; TotalPowerConsumption; InSourced; OutSourced");
    			bw.newLine();
    			headerAdded = true;
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    }
    
    public void writeHeader2(){
    	if(!headerAdded){
    		try {
    			bw.write("Scheduler;Scenario;TotalPMs;TotalVMs;TotalDuration;TotalPowerConsumption;TotalInSourced;TotalOutSourced");
    			bw.newLine();
    			headerAdded = true;
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    }

    public void writeCsv(CloudStateInfo cloudInfo) {
    	
    	try {
			bw.write(cloudInfo.toString());
			bw.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}

    }
    
  public void writeCsv(CloudOverallInfo cloudInfo) {
    	
    	try {
			bw.write(cloudInfo.toString());
			bw.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}

    }
    
    public void close(){
    	try {
			bw.close();
//			fw.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
