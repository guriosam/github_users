package objects;

import java.util.List;

public class UserPoint {

	private String name;
	private String firstDate;
	private List<CommitInfo> commitInfo;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<CommitInfo> getCommitInfo() {
		return commitInfo;
	}
	public void setCommitInfo(List<CommitInfo> commitInfo) {
		this.commitInfo = commitInfo;
	}
	
	@Override
	public String toString() {
		return "UserPoint [name=" + name + ", commitInfo=" + commitInfo + "]";
	}
	public String getFirstDate() {
		return firstDate;
	}
	public void setFirstDate(String firstDate) {
		this.firstDate = firstDate;
	}
	
}
