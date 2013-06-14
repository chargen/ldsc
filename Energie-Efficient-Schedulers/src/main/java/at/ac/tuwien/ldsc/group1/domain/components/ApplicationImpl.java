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
        int result = (int) (duration ^ (duration >>> 32));
        result = 31 * result + ram;
        result = 31 * result + hddSize;
        result = 31 * result + cpuInMhz;
        result = 31 * result + (int) (timeStamp ^ (timeStamp >>> 32));
        result = 31 * result + id;
        result = 31 * result + (isOutSourced ? 1 : 0);
        result = 31 * result + (isInSourced ? 1 : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ApplicationImpl that = (ApplicationImpl) o;

        if (cpuInMhz != that.cpuInMhz) return false;
        if (duration != that.duration) return false;
        if (hddSize != that.hddSize) return false;
        if (id != that.id) return false;
        if (isInSourced != that.isInSourced) return false;
        if (isOutSourced != that.isOutSourced) return false;
        if (ram != that.ram) return false;
        if (timeStamp != that.timeStamp) return false;

        return true;
    }

    @Override
	public String toString() {
		return "ApplicationImpl [duration=" + duration + ", ram=" + ram
				+ ", hddSize=" + hddSize + ", cpuInMhz=" + cpuInMhz
				+ ", timeStamp=" + timeStamp + ", id=" + id + "]";
	}
}
