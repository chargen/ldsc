package at.ac.tuwien.ldsc.group1.domain.components;

import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import at.ac.tuwien.ldsc.group1.domain.exceptions.ResourceUnavailableException;

public class PhysicalMachineImpl extends MachineImpl implements PhysicalMachine {

	static final Integer ramBase; 
	static final Integer hddBase; 
	static final Integer cpuInMhzBase; 
	static final Integer ramMax; 
	static final Integer hddMax; 
	static final Integer cpuInMhzMax; 
 
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
		super(ramBase, hddBase, cpuInMhzBase, ramMax, hddMax, cpuInMhzMax);
	}
	
	
	
	
    public PhysicalMachineImpl(
            int ramBase, int hddBase, int cpuInMhzBase,
            int ramMax, int hddMax, int cpuInMhzMax)
    {
        super(ramBase, hddBase, cpuInMhzBase,
            ramMax, hddMax, cpuInMhzMax);
    }

    long startTimestamp;

    @Override
    public long uptime() {
        return (new Date().getTime() - startTimestamp);
    }

    @Override
    public void start() {
        this.startTimestamp = new Date().getTime();
        super.start();
    }


    


	

}
