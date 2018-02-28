package objects;

import java.util.List;

public class UserPoint {

	private String name;
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

	
	
}
