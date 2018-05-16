package objects;

public class IssueDAO {
	
	private String hash;
	private String user;
	private String authorDate;
	private int countOpened;
	private int countClosed;
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
	@Override
	public String toString() {
		return "IssueDAO [hash=" + hash + ", user=" + user + ", authorDate=" + authorDate + ", countOpened="
				+ countOpened + ", countClosed=" + countClosed + "]";
	}
	
	

}
