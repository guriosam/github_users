package objects;

public class CommentDAO {

	private int pullCommentCount;
	private int issueCommentCount;
	private int commitCommentCount;
	private String hash;
	private String user;
	private String authorDate;
	
	public int getPullCommentCount() {
		return pullCommentCount;
	}
	public void setPullCommentCount(int pullCommentCount) {
		this.pullCommentCount = pullCommentCount;
	}
	public int getIssueCommentCount() {
		return issueCommentCount;
	}
	public void setIssueCommentCount(int issueCommentCount) {
		this.issueCommentCount = issueCommentCount;
	}
	public int getCommitCommentCount() {
		return commitCommentCount;
	}
	public void setCommitCommentCount(int commitCommentCount) {
		this.commitCommentCount = commitCommentCount;
	}
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
	@Override
	public String toString() {
		return "CommentDAO [pullCommentCount=" + pullCommentCount + ", issueCommentCount=" + issueCommentCount
				+ ", commitCommentCount=" + commitCommentCount + ", hash=" + hash + ", user=" + user + ", authorDate="
				+ authorDate + "]";
	}
	
	

}
