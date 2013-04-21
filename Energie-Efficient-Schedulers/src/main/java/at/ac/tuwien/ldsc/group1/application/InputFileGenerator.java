package at.ac.tuwien.ldsc.group1.application;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Random;
import java.util.ResourceBundle;

public class InputFileGenerator {
    private static final String header = "Timestamp;Size;RAM;CPU;Duration\n";

    private int timestampBase;
    private int timestampFactor;
    private int sizeBase;
    private int sizeFactor;
    private int ramBase;
    private int ramFactor;
    private int cpuBase;
    private int cpuFactor;
    private int durationBase;
    private int durationFactor;

    public InputFileGenerator() {
        ResourceBundle resource = ResourceBundle.getBundle("applications");
        try {
        timestampBase =   Integer.parseInt(resource.getString("timestampBase"));
        timestampFactor = Integer.parseInt(resource.getString("timestampFactor"));
        sizeBase =        Integer.parseInt(resource.getString("sizeBase"));
        sizeFactor =      Integer.parseInt(resource.getString("sizeFactor"));
        ramBase  =        Integer.parseInt(resource.getString("ramBase"));
        ramFactor =       Integer.parseInt(resource.getString("ramFactor"));
        cpuBase =         Integer.parseInt(resource.getString("cpuBase"));
        cpuFactor =       Integer.parseInt(resource.getString("cpuFactor"));
        durationBase =    Integer.parseInt(resource.getString("durationBase"));
        durationFactor =  Integer.parseInt(resource.getString("durationFactor"));
        } catch(NumberFormatException e) {
            throw new RuntimeException("InputFileGenerator: Error in resource file");
        }
    }

    public boolean generateFile(int numberOfRandomItems, String filename) {
        BufferedWriter out;
        FileOutputStream outStream;
        try {
            outStream = new FileOutputStream(filename);
            OutputStreamWriter outWriter = new OutputStreamWriter(outStream);
            out = new BufferedWriter(outWriter);
        } catch (IOException e) {
            return false;
        }

        try {
            out.write(header);

            for(int i=0; i < numberOfRandomItems; i++) {
                Random random = new Random();
                long timestamp = (int) (timestampBase + ((timestampFactor/ (double) 2) + random.nextGaussian() * (timestampFactor/ (double) 2)));
                if(timestamp < timestampBase) {
                    timestamp = timestampBase;
                }
                if(timestamp > timestampFactor+timestampBase) {
                    timestamp = timestampFactor+timestampBase;
                }
                int size       = (int) (sizeBase + Math.random() * sizeFactor);
                int ram        = (int) (ramBase + Math.random() * ramFactor);
                int cpu        = (int) (cpuBase + Math.random() * cpuFactor);
                long duration = (int) (durationBase + ((durationFactor/ (double) 2) + random.nextGaussian() * (durationFactor/ (double) 2)));
                if(duration < durationBase) {
                    duration = durationBase;
                }
                if(duration > durationFactor+durationBase) {
                    duration = durationFactor+durationBase;
                }

                out.write("" + timestamp + ";" + size + ";" + ram + ";" + cpu + ";" + duration + "\n");
            }
        } catch (IOException e) {
            return false;
        } finally {
            try {
                out.close();
                outStream.close();
            } catch (IOException e) { }
        }
        return true;
    }
}
