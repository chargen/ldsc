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
	Integer overprovidedCpuInMhz = 0;
    Integer overprovidedRam = 0;
    private final int id;
    long startTimestamp;

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
    
    public Integer getOverprovidedCpuInMhz() {
        return overprovidedCpuInMhz;
    }

    public void setOverprovidedCpuInMhz(Integer overprovidedCpuInMhz) {
        // we swap the values
        this.overprovidedCpuInMhz = cpuInMhzMax;
        cpuInMhzMax= overprovidedCpuInMhz ;
    }
    
    public void revertOverprovidedCpuInMhz() {
        // we swap the values
        cpuInMhzMax = this.overprovidedCpuInMhz ;
    }

    public Integer getOverprovidedRam() {
        return overprovidedRam;
    }

    public void setOverprovidedRam(Integer overprovidedRam) {
        this.overprovidedRam = ramMax;
        ramMax = overprovidedRam;
    }
    
    public void revertOverprovidedRam() {
        ramMax = this.overprovidedRam;
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

	
}
