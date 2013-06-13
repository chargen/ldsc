package at.ac.tuwien.ldsc.group1.domain.components;

public class ApplicationImpl implements Application {
    long duration;
    private int ram;
    private int hddSize;
    private int cpuInMhz;
    private long timeStamp;
    private static int nextId = 0;
    private final int id;
    private boolean isOutSourced;
    private boolean isInSourced;

    public ApplicationImpl(int ram, int hddSize, int cpuInMhz, long duration, long timeStamp) {
        this.duration = duration;
        this.ram = ram;
        this.hddSize = hddSize;
        this.cpuInMhz = cpuInMhz;
        this.timeStamp = timeStamp;
        this.id = ++nextId;
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
    public int getId() {
        return id;
    }

    @Override
    public long getDuration() {
        return duration;
    }

    @Override
    public void start() {
        System.out.println("        Application " + getId() + " has started; takes: " + getDuration());
    }

    @Override
    public void stop() {
        System.out.println("        Application " + getId() + " has finished");
    }

	public long getTimeStamp() {
		return timeStamp;
	}

    @Override
    public boolean isOutSourced() {
        return isOutSourced;
    }

    @Override
    public void setIsOutSourced(boolean isOutSourced) {
        this.isOutSourced = isOutSourced;
    }

    @Override
    public void setIsInSourced(boolean inSourced) {
        this.isInSourced = inSourced;
    }

    @Override
    public boolean isInSourced() {
        return isInSourced;
    }

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + cpuInMhz;
		result = prime * result + (int) (duration ^ (duration >>> 32));
		result = prime * result + hddSize;
		result = prime * result + ram;
		result = prime * result + (int) (timeStamp ^ (timeStamp >>> 32));
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
		ApplicationImpl other = (ApplicationImpl) obj;
		if (cpuInMhz != other.cpuInMhz)
			return false;
		if (duration != other.duration)
			return false;
		if (hddSize != other.hddSize)
			return false;
		if (ram != other.ram)
			return false;
		if (timeStamp != other.timeStamp)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ApplicationImpl [duration=" + duration + ", ram=" + ram
				+ ", hddSize=" + hddSize + ", cpuInMhz=" + cpuInMhz
				+ ", timeStamp=" + timeStamp + ", id=" + id + "]";
	}
}
