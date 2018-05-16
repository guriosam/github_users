package objects;

public class CommitInfo {
	
	private String hash;
	private String date;
	private boolean buggy;
	
	public String getHash() {
		return hash;
	}
	public void setHash(String hash) {
		this.hash = hash;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
	public boolean isBuggy() {
		return buggy;
	}
	public void setBuggy(boolean buggy) {
		this.buggy = buggy;
	}
	
	@Override
	public String toString() {
		return "CommitInfo [hash=" + hash + ", date=" + date + ", buggy=" + buggy + "]";
	}
	
	

}
