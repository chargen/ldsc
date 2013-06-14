package at.ac.tuwien.ldsc.group1.domain.federation;

import at.ac.tuwien.ldsc.group1.domain.components.Application;
import at.ac.tuwien.ldsc.group1.domain.components.ApplicationImpl;

import java.util.Random;
import java.util.ResourceBundle;

public class FederationPartner {
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

    public FederationPartner() {
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
            throw new RuntimeException("InputFileGenerator: Error inputStream resource file");
        }
    }

    public Application getSourceInApplication(ScenarioType type) {
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
        Application app = new ApplicationImpl(ram, size, cpu, duration, timestamp);
        return app;
    }

    public boolean deploySourceOutApplication(Application app) {
        return Math.random() <= 0.05;
    }

}
