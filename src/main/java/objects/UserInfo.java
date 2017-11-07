package objects;

import java.util.List;

public class UserInfo {

	private String login;
	private int commits;
	private double additions;
	private double meanAdditions;
	private double medianAdditions;
	private double deletions;
	private double meanDeletions;
	private double medianDeletions;
	private double modifiedFiles;
	private double meanModified;
	private double medianModified;
	private int activeDays;
	private int timeOnProject;
	private List<String> weeks;

	private int numberComments;
	private int numberOpenIssues;
	private int numberClosedIssues;
	private double percentPullRequestsMerged;
	private int numberOpenPullRequests;
	private int numberClosedPullRequests;
	private int numberRequestedReviewer;
	private int insertionPoints;

	public int getCommits() {
		return commits;
	}

	public void setCommits(int commits) {
		this.commits = commits;
	}

	public double getAdditions() {
		return additions;
	}

	public void setAdditions(double additions) {
		this.additions = additions;
	}

	public double getMeanAdditions() {
		return meanAdditions;
	}

	public void setMeanAdditions(double meanAdditions) {
		this.meanAdditions = meanAdditions;
	}

	public double getMedianAdditions() {
		return medianAdditions;
	}

	public void setMedianAdditions(double medianAdditions) {
		this.medianAdditions = medianAdditions;
	}

	public double getDeletions() {
		return deletions;
	}

	public void setDeletions(double deletions) {
		this.deletions = deletions;
	}

	public double getMeanDeletions() {
		return meanDeletions;
	}

	public void setMeanDeletions(double meanDeletions) {
		this.meanDeletions = meanDeletions;
	}

	public double getMedianDeletions() {
		return medianDeletions;
	}

	public void setMedianDeletions(double medianDeletions) {
		this.medianDeletions = medianDeletions;
	}

	public double getModifiedFiles() {
		return modifiedFiles;
	}

	public void setModifiedFiles(double modifiedFiles) {
		this.modifiedFiles = modifiedFiles;
	}

	public double getMeanModified() {
		return meanModified;
	}

	public void setMeanModified(double meanModified) {
		this.meanModified = meanModified;
	}

	public int getActiveDays() {
		return activeDays;
	}

	public void setActiveDays(int activeDays) {
		this.activeDays = activeDays;
	}

	public int getTimeOnProject() {
		return timeOnProject;
	}

	public void setTimeOnProject(int timeOnProject) {
		this.timeOnProject = timeOnProject;
	}

	public List<String> getWeeks() {
		return weeks;
	}

	public void setWeeks(List<String> weeks) {
		this.weeks = weeks;
	}

	public int getNumberComments() {
		return numberComments;
	}

	public void setNumberComments(int numberComments) {
		this.numberComments = numberComments;
	}

	public int getNumberOpenIssues() {
		return numberOpenIssues;
	}

	public void setNumberOpenIssues(int numberOpenIssues) {
		this.numberOpenIssues = numberOpenIssues;
	}

	public int getNumberClosedIssues() {
		return numberClosedIssues;
	}

	public void setNumberClosedIssues(int numberClosedIssues) {
		this.numberClosedIssues = numberClosedIssues;
	}

	public double getPercentPullRequestsMerged() {
		return percentPullRequestsMerged;
	}

	public void setPercentPullRequestsMerged(double percentPullRequestsMerged) {
		this.percentPullRequestsMerged = percentPullRequestsMerged;
	}

	public int getNumberOpenPullRequests() {
		return numberOpenPullRequests;
	}

	public void setNumberOpenPullRequests(int numberOpenPullRequests) {
		this.numberOpenPullRequests = numberOpenPullRequests;
	}

	public int getNumberClosedPullRequests() {
		return numberClosedPullRequests;
	}

	public void setNumberClosedPullRequests(int numberClosedPullRequests) {
		this.numberClosedPullRequests = numberClosedPullRequests;
	}

	public int getNumberRequestedReviewer() {
		return numberRequestedReviewer;
	}

	public void setNumberRequestedReviewer(int numberRequestedReviewer) {
		this.numberRequestedReviewer = numberRequestedReviewer;
	}

	public double getMedianModified() {
		return medianModified;
	}

	public void setMedianModified(double medianModified) {
		this.medianModified = medianModified;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public int getInsertionPoints() {
		return insertionPoints;
	}

	public void setInsertionPoints(int insertionPoints) {
		this.insertionPoints = insertionPoints;
	}


}
