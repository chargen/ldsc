package at.ac.tuwien.ldsc.group1.domain.components;

import java.util.Date;
import java.util.ResourceBundle;

public class PhysicalMachineImpl extends MachineImpl implements PhysicalMachine {

    private static int nextId = 0;
    static final Integer ramBase;
    static final Integer hddBase;
    static final Integer cpuInMhzBase;
    static Integer ramMax;
    static final Integer hddMax;
	static Integer cpuInMhzMax;

    private final int id;
    long startTimestamp;
    private double penalty = 1;
    

	static{
		ResourceBundle res = ResourceBundle.getBundle("physicalMachine");
		ramBase = Integer.parseInt(res.getString("ramBase"));
		hddBase = Integer.parseInt(res.getString("sizeBase"));
		cpuInMhzBase = Integer.parseInt(res.getString("cpuBase"));
		ramMax = Integer.parseInt(res.getString("ramMax"));
		hddMax = Integer.parseInt(res.getString("sizeMax"));
		cpuInMhzMax = Integer.parseInt(res.getString("cpuMax"));
	}

    public PhysicalMachineImpl() {
		this(ramBase, hddBase, cpuInMhzBase, ramMax, hddMax, cpuInMhzMax);
	}

    public PhysicalMachineImpl(
            int ramBase, int hddBase, int cpuInMhzBase,
            int ramMax, int hddMax, int cpuInMhzMax)
    {
        super(ramBase, hddBase, cpuInMhzBase,
            ramMax, hddMax, cpuInMhzMax);
        this.id = ++nextId;
    }

    @Override
    public long uptime() {
        return (new Date().getTime() - startTimestamp);
    }

    @Override
    public void start() {
        this.startTimestamp = new Date().getTime();
        System.out.println("PM " + getId() + " Started");
    }


    @Override
    public void stop() {
        System.out.println("PM " + getId() + " Stopped");
    }

    @Override
    public int getId() {
        return id;
    }
    
   
    
	public Integer getCpuInMhzMax() {
		return cpuInMhzMax;
	}
	
	public Integer getRamMax() {
		return ramMax;
	}

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result
                + (int) (startTimestamp ^ (startTimestamp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PhysicalMachineImpl other = (PhysicalMachineImpl) obj;
        if (id != other.id)
            return false;
        if (startTimestamp != other.startTimestamp)
            return false;
        return true;
    }

    public void setOverprovidedCpuInMhz(Integer overprovidedCpuInMhz) {
        // we swap the values
    	super.setOverprovidedCpuInMhz(overprovidedCpuInMhz);
        this.overprovidedCpuInMhz = cpuInMhzMax;
        cpuInMhzMax= overprovidedCpuInMhz ;
    }
    
    public void revertOverprovidedCpuInMhz() {
        // we swap the values
    	super.revertOverprovidedCpuInMhz();
        cpuInMhzMax = this.overprovidedCpuInMhz ;
    }

    public Integer getOverprovidedRam() {
    	
        return super.getOverprovidedRam();
    }

    public void setOverprovidedRam(Integer overprovidedRam) {
    	super.setOverprovidedRam(overprovidedRam);
        this.overprovidedRam = ramMax;
        ramMax = overprovidedRam;
    }
    
    public void revertOverprovidedRam() {
    	super.revertOverprovidedRam();
        ramMax = this.overprovidedRam;
    }
    
    public Integer getOverprovidedCpuInMhz() {
        return super.getOverprovidedCpuInMhz();
    }

	public double getPenalty() {
		return penalty;
	}

	public void setPenalty(double penalty) {
		this.penalty = penalty;
	}

	public double getOverprovidionPercentage() {
		double overprovidionPercentage = 0;
		
		if(this.getCpuInMhz() > 2400 || this.getRam() > 4700){
			double temp1 = (new Double(new Double(this.getCpuInMhz())/2400))*100;
			double temp2 = (new Double(new Double(this.getRam())/4700))*100;
			if(temp1 < 100) temp1 = 100;
			if(temp2 < 100) temp2 = 100;
			
			double temp3 = (temp1+temp2)/2;
			overprovidionPercentage = temp3-100;
		}
		
		return overprovidionPercentage;
	}

	@Override
	public String toString() {
		return "PhysicalMachineImpl [id=" + id + ", getCpuInMhzMax()="
				+ getCpuInMhzMax() + ", getRamMax()=" + getRamMax()
				+ ", getOverprovidionPercentage()="
				+ getOverprovidionPercentage() + ", getRam()=" + getRam()
				+ ", getHddSize()=" + getHddSize() + ", getCpuInMhz()="
				+ getCpuInMhz() + ", getRamAvailable()=" + getRamAvailable()
				+ ", getCpuAvailable()=" + getCpuAvailable()
				+ ", getHddAvailable()=" + getHddAvailable() + "]";
	}

	

	

	

	
	
    
}
