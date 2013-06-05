package at.ac.tuwien.ldsc.group1.domain.components;


/**
 * Interface for a physical machine, we can only start it, and shut it down
 *
 * @author Sebastian Geiger
 */
public interface PhysicalMachine extends Machine {
    /**
     * Time since the machine was started
     */
    long uptime();
    Integer getCpuInMhzMax();
    Integer getRamMax();
    Integer getOverprovidedCpuInMhz();
    Integer getOverprovidedRam();
    void setOverprovidedCpuInMhz(Integer cpuInMhzMax);
    void setOverprovidedRam(Integer ramMax);
    void revertOverprovidedRam();
    void revertOverprovidedCpuInMhz();
    
    public double getPenalty();
	public void setPenalty(double penalty);
	
	public double getOverprovidionPercentage();
	
}
