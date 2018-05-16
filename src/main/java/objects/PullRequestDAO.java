package objects;

public class PullRequestDAO {

	private String hash;
	private String user;
	private String authorDate;
	private int countOpened;
	private int countClosed;
	private int countMerged;
	private int countRequested;
	private double openMerged;
	public String getHash() {
		return hash;
	}
	public void setHash(String hash) {
		this.hash = hash;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getAuthorDate() {
		return authorDate;
	}
	public void setAuthorDate(String authorDate) {
		this.authorDate = authorDate;
	}
	public int getCountOpened() {
		return countOpened;
	}
	public void setCountOpened(int countOpened) {
		this.countOpened = countOpened;
	}
	public int getCountClosed() {
		return countClosed;
	}
	public void setCountClosed(int countClosed) {
		this.countClosed = countClosed;
	}
	public int getCountMerged() {
		return countMerged;
	}
	public void setCountMerged(int countMerged) {
		this.countMerged = countMerged;
	}
	public int getCountRequested() {
		return countRequested;
	}
	public void setCountRequested(int countRequested) {
		this.countRequested = countRequested;
	}
	public double getOpenMerged() {
		return openMerged;
	}
	public void setOpenMerged(double openMerged) {
		this.openMerged = openMerged;
	}
	
	

}
