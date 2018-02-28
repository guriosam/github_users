package objects;

import java.util.Date;
import java.util.List;

public class UserCommit {
	
	private String sha;
	private String date;
	private String authorName;
	private String authorEmail;
	private String authorLogin;
	private boolean testInclusion;
	private String classification;
	
	
	public String getSha() {
		return sha;
	}
	public void setSha(String sha) {
		this.sha = sha;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getAuthorName() {
		return authorName;
	}
	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}
	public String getAuthorEmail() {
		return authorEmail;
	}
	public void setAuthorEmail(String authorEmail) {
		this.authorEmail = authorEmail;
	}
	public String getAuthorLogin() {
		return authorLogin;
	}
	public void setAuthorLogin(String authorLogin) {
		this.authorLogin = authorLogin;
	}
	public boolean hasTestInclusion() {
		return testInclusion;
	}
	public void setTestInclusion(boolean testInclusion) {
		this.testInclusion = testInclusion;
	}
	public String getClassification() {
		return classification;
	}
	public void setClassification(String classification) {
		this.classification = classification;
	}
	
	
	
	

}
