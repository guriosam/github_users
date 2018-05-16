package objects;

import java.util.List;

public class InsertionPoint {

	private String issueID;
	private String fixCommit;
	private List<String> insertionPoints;
	
	public String getIssueID() {
		return issueID;
	}
	public void setIssueID(String issueID) {
		this.issueID = issueID;
	}
	public String getFixCommit() {
		return fixCommit;
	}
	public void setFixCommit(String fixCommit) {
		this.fixCommit = fixCommit;
	}
	public List<String> getInsertionPoints() {
		return insertionPoints;
	}
	public void setInsertionPoints(List<String> insertionPoints) {
		this.insertionPoints = insertionPoints;
	}
	
	@Override
	public String toString() {
		return "InsertionPoint [issueID=" + issueID + ", fixCommit=" + fixCommit + ", insertionPoints="
				+ insertionPoints + "]";
	}
	
	
	
}
