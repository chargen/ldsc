package at.ac.tuwien.ldsc.group1.domain.components;

public class ApplicationImpl implements Application {
    long duration;
    private int ram;
    private int hddSize;
    private int cpuInMhz;
    private long timeStamp;

    public ApplicationImpl(int ram, int hddSize, int cpuInMhz, long duration, long timeStamp) {
        this.duration = duration;
        this.ram = ram;
        this.hddSize = hddSize;
        this.cpuInMhz = cpuInMhz;
        this.timeStamp = timeStamp;
    }

    @Override
    public int getHddSize() {
        return hddSize;
    }

    @Override
    public int getRam() {
        return ram;
    }

    @Override
    public int getCpuInMhz() {
        return cpuInMhz;
    }

    @Override
    public long getDuration() {
        return duration;
    }

    @Override
    public void start() {
        //TODO: Use log4j
        System.out.println("Application has started");
    }

    @Override
    public void stop() {
        // TODO: Use log4j
        System.out.println("Application has finished");
    }

	public long getTimeStamp() {
		return timeStamp;
	}
    
    
}
