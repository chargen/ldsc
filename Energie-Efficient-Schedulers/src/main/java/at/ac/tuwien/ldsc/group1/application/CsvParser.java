package at.ac.tuwien.ldsc.group1.application;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.ldsc.group1.domain.components.Application;
import at.ac.tuwien.ldsc.group1.domain.components.ApplicationImpl;

/**
 * Read a CSV file with Application definition and create an instances of
 * an Applications
 *
 * @author sebastiangeiger
 *
 */
public class CsvParser {

    public List<Application> parse(String fileName) {
        
    	List<Application> appList= new ArrayList<Application>();
    	try {
			FileInputStream fis = new FileInputStream(fileName);
						
			DataInputStream in = new DataInputStream(fis);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			
			while ((strLine = br.readLine()) != null){
				
				  String[] parts = strLine.split(";");
				  try{
					  Long timeStamp = Long.parseLong(parts[0]);
					  Integer hddSize = Integer.parseInt(parts[1]);
					  Integer ram = Integer.parseInt(parts[2]);
					  Integer cpuInMhz = Integer.parseInt(parts[3]);
					  Long duration = Long.parseLong(parts[4]);
					  
					  ApplicationImpl app = new ApplicationImpl(ram, hddSize, cpuInMhz, duration,timeStamp);
					  appList.add(app);
				  
				  }catch(NumberFormatException e){
					  continue; //skip first row
				  }
				  
				  
			}
			 
			in.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	return appList;
        
    }
}
