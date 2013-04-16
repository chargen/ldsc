package at.ac.tuwien.ldsc.group1.domain;

/**
 * This is a DTO that contains all the data from the scheduler that is required by the
 * CsvWriter to log the cloud utilization.
 */
public class CloudInfo {
    int timestamp;
    int totalRAM;
    int totalCPU;
    int totalSize;
    int runningPMs;
    int runningVMs;
    int totalPowerConsumption;
    int inSourced;
    int outSourced;
}
