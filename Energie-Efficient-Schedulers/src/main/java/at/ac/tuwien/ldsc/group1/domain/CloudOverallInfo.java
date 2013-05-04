package at.ac.tuwien.ldsc.group1.domain;

/**
 * This is a DTO that contains all the data from the scheduler that is required by the
 * CsvWriter to log the cloud utilization.
 */
public class CloudOverallInfo {
	
	String Scheduler;
	String Scenario;
	int totalPMs,totalVMs, totalInSourced, totalOutSourced;
	long totalDuration;
	double totalPowerConsumption;
	
    
	
	public CloudOverallInfo(String scheduler, String scenario, int totalPMs,
			int totalVMs, int totalInSourced, int totalOutSourced,
			long totalDuration, double totalPowerConsumption) {
		super();
		Scheduler = scheduler;
		Scenario = scenario;
		this.totalPMs = totalPMs;
		this.totalVMs = totalVMs;
		this.totalInSourced = totalInSourced;
		this.totalOutSourced = totalOutSourced;
		this.totalDuration = totalDuration;
		this.totalPowerConsumption = totalPowerConsumption;
	}

	public String getScheduler() {
		return Scheduler;
	}

	public String getScenario() {
		return Scenario;
	}

	public int getTotalPMs() {
		return totalPMs;
	}

	public int getTotalVMs() {
		return totalVMs;
	}

	public int getTotalInSourced() {
		return totalInSourced;
	}

	public int getTotalOutSourced() {
		return totalOutSourced;
	}

	public long getTotalDuration() {
		return totalDuration;
	}

	public double getTotalPowerConsumption() {
		return totalPowerConsumption;
	}

	@Override
    public String toString(){
    	return this.getScheduler() + ";" + this.getScenario() + ";" + this.getTotalPMs() + ";" + this.getTotalVMs()+ ";" +
    			this.getTotalInSourced() + ";" + this.getTotalOutSourced() + ";" + this.getTotalDuration() + ";" + this.getTotalPowerConsumption();
    }
    
}
