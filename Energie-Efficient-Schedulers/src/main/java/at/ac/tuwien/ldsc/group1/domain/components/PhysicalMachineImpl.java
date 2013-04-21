package at.ac.tuwien.ldsc.group1.domain.components;

import java.util.Date;

public class PhysicalMachineImpl extends MachineImpl implements PhysicalMachine {

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
