package objects;

public class CommitSize {

	private int emptyCount;
	private int tinyCount;
	private int smallCount;
	private int mediumCount;
	private int largeCount;

	public int getTinyCount() {
		return tinyCount;
	}

	public void setTinyCount(int tinyCount) {
		this.tinyCount = tinyCount;
	}

	public int getSmallCount() {
		return smallCount;
	}

	public void setSmallCount(int smallCount) {
		this.smallCount = smallCount;
	}

	public int getMediumCount() {
		return mediumCount;
	}

	public void setMediumCount(int mediumCount) {
		this.mediumCount = mediumCount;
	}

	public int getLargeCount() {
		return largeCount;
	}

	public void setLargeCount(int largeCount) {
		this.largeCount = largeCount;
	}

	public int getEmptyCount() {
		return emptyCount;
	}

	public void setEmptyCount(int emptyCount) {
		this.emptyCount = emptyCount;
	}

}
