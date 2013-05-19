package at.ac.tuwien.ldsc.group1.domain.components;

import java.util.Date;
import java.util.ResourceBundle;

public class PhysicalMachineImpl extends MachineImpl implements PhysicalMachine {

    private static int nextId = 0;
    static final Integer ramBase;
    static final Integer hddBase;
    static final Integer cpuInMhzBase;
    static final Integer ramMax;
    static final Integer hddMax;
	static final Integer cpuInMhzMax;
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

	public Integer getCpuInMhzMax() {
		return cpuInMhzMax;
	}
	
	public Integer getRamMax() {
		return ramMax;
	}

}
