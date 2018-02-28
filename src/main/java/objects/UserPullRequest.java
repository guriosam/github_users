package objects;

import java.util.List;

public class UserPullRequest {

	private String id;
	private boolean merged;
	private String user;
	private String merged_by;
	private String state;
	private String created_at;
	private String closed_at;
	private List<String> reviewers;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public boolean isMerged() {
		return merged;
	}
	public void setMerged(boolean merged) {
		this.merged = merged;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getMerged_by() {
		return merged_by;
	}
	public void setMerged_by(String merged_by) {
		this.merged_by = merged_by;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public List<String> getReviewers() {
		return reviewers;
	}
	public void setReviewers(List<String> reviewers) {
		this.reviewers = reviewers;
	}
	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
	public String getClosed_at() {
		return closed_at;
	}
	public void setClosed_at(String closed_at) {
		this.closed_at = closed_at;
	}
	
	
	
}
