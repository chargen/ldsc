package at.ac.tuwien.ldsc.group1.domain;

/**
 * This is a DTO that contains all the data from the scheduler that is required by the
 * CsvWriter to log the cloud utilization.
 */
public class CloudStateInfo {
    int timestamp;
    int totalRAM;
    int totalCPU;
    int totalSize;
    int runningPMs;
    int runningVMs;
    double totalPowerConsumption;
    int inSourced;
    int outSourced;
    
	public CloudStateInfo(int timestamp, int totalRAM, int totalCPU, int totalSize,
			int runningPMs, int runningVMs, double totalPowerConsumption,
			int inSourced, int outSourced) {
		super();
		this.timestamp = timestamp;
		this.totalRAM = totalRAM;
		this.totalCPU = totalCPU;
		this.totalSize = totalSize;
		this.runningPMs = runningPMs;
		this.runningVMs = runningVMs;
		this.totalPowerConsumption = totalPowerConsumption;
		this.inSourced = inSourced;
		this.outSourced = outSourced;
	}

	public int getTimestamp() {
		return timestamp;
	}

	public int getTotalRAM() {
		return totalRAM;
	}

	public int getTotalCPU() {
		return totalCPU;
	}

	public int getTotalSize() {
		return totalSize;
	}

	public int getRunningPMs() {
		return runningPMs;
	}

	public int getRunningVMs() {
		return runningVMs;
	}

	public double getTotalPowerConsumption() {
		return totalPowerConsumption;
	}

	public int getInSourced() {
		return inSourced;
	}

	public int getOutSourced() {
		return outSourced;
	}
	@Override
    public String toString(){
    	return this.getTimestamp() + ";" + this.getTotalRAM() + ";" + this.getTotalCPU() + ";" + this.getTotalSize()+ ";" +
    			this.getRunningPMs() + ";" + this.getRunningVMs() + ";" +
                String.format("%.2f", this.getTotalPowerConsumption())
                + ";" + this.getInSourced() + ";" + this.getOutSourced();
    }

    public String getExtrapolatableString() {
        return "%d" + ";" + this.getTotalRAM() + ";" + this.getTotalCPU() + ";" + this.getTotalSize()+ ";" +
                this.getRunningPMs() + ";" + this.getRunningVMs() + ";" +
                String.format("%.2f", this.getTotalPowerConsumption())
                + ";" + this.getInSourced() + ";" + this.getOutSourced();
    }

}
