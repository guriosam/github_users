package objects;

import java.util.List;

import org.apache.commons.collections.ComparatorUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.LocalDateTime;

import utils.Util;

public class UserInfo {

	private String hash;
	private String login;
	private boolean buggy;
	private int commits;
	private double meanCommits;
	private double medianCommits;
	private double additions;
	private double meanAdditions;
	private double medianAdditions;
	private double deletions;
	private double meanDeletions;
	private double medianDeletions;
	private double modifiedFiles;
	private double meanModified;
	private double medianModified;
	private double buggyPercent;
	private int emptyNatureCount;
	private int managementCount;
	private int reengineeringCount;
	private int correctiveEngineeringCount;
	private int forwardEngineeringCount;
	private int uncategorizedCount;
	private int emptySizeCount;
	private int tinyCount;
	private int smallCount;
	private int mediumCount;
	private int largeCount;

	private int commitsPulls;

	private int activeDays;
	private int timeOnProject;
	// private List<String> weeks;

	private int numberComments;
	private int numberOpenIssues;
	private int numberClosedIssues;
	private double pullRequestsMerged;
	private double percentPullRequestsMerged;
	private int numberOpenPullRequests;
	private int numberClosedPullRequests;
	private int numberRequestedReviewer;
	private boolean insertionTests;
	private int insertionTestsCount;
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

	public void setMeanAdditions(List<Double> additions) {

		Util.sortList(additions);

		if (additions.size() > 0) {
			meanAdditions = (double) this.additions / additions.size();
		} else {
			meanAdditions = 0.0;
		}

	}

	public double getMedianAdditions() {
		return medianAdditions;
	}

	public void setMedianAdditions(List<Double> additions) {

		Util.sortList(additions);

		if (additions.size() > 0) {
			if (additions.size() % 2 == 0) {
				this.medianAdditions = ((additions.get(additions.size() / 2) + additions.get(additions.size() / 2 - 1))
						/ 2);
			} else {
				this.medianAdditions = (additions.get(additions.size() / 2));
			}
		}
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

	public void setMeanDeletions(List<Double> deletions) {

		Util.sortList(deletions);

		if (deletions.size() > 0) {
			meanDeletions = (double) this.deletions / deletions.size();
		} else {
			meanDeletions = 0.0;
		}
	}

	public double getMedianDeletions() {
		return medianDeletions;
	}

	public void setMedianDeletions(List<Double> deletions) {

		Util.sortList(deletions);

		if (deletions.size() > 0) {
			if (deletions.size() % 2 == 0) {
				this.medianDeletions = ((deletions.get(deletions.size() / 2) + deletions.get(deletions.size() / 2 - 1))
						/ 2);
			} else {
				this.medianDeletions = (deletions.get(deletions.size() / 2));
			}
		}
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

	public void setMeanModified(List<Integer> files) {

		Util.sortList(files);

		if (files.size() > 0) {
			meanModified = (double) this.modifiedFiles / files.size();
		} else {
			meanModified = 0.0;
		}

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

	public void setTimeOnProject(List<String> datesToOrder, String maximumDate) {

		Util.sortList(datesToOrder);

		if (datesToOrder.size() > 0) {
			timeOnProject = (Days.daysBetween(LocalDateTime.parse(datesToOrder.get(0)), LocalDateTime.parse(maximumDate))
					.getDays() + 1);
		} else {
			timeOnProject = 0;
		}

	}

	// public List<String> getWeeks() {
	// return weeks;
	// }

	// public void setWeeks(List<String> weeks) {
	// this.weeks = weeks;
	// }

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

	public void setMedianModified(List<Integer> files) {
		Util.sortList(files);

		if (files.size() > 0) {
			if (files.size() % 2 == 0) {
				this.medianModified = ((files.get(files.size() / 2) + files.get(files.size() / 2 - 1)) / 2);
			} else {
				this.medianModified = (files.get(files.size() / 2));
			}
		}
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

	public double getMeanCommits() {
		return meanCommits;
	}

	public void setMeanCommits(String minimumDate, String maximumDate) {
		

		if (Days.daysBetween(LocalDateTime.parse(minimumDate), LocalDateTime.parse(maximumDate)).getDays() >= 0) {
			meanCommits = (double) commits
					/ (Days.daysBetween(LocalDateTime.parse(minimumDate), LocalDateTime.parse(maximumDate)).getDays() + 1);
		} else {
			meanCommits = (double) commits;
		}

	}

	public double getMedianCommits() {
		return medianCommits;
	}

	public void setMedianCommits(List<Integer> orderedDates) {
		if (orderedDates.size() == 0) {
			medianCommits = 0;
		} else if (orderedDates.size() > 0) {

			if (orderedDates.size() % 2 == 0) {
				medianCommits = (double) (orderedDates.get(orderedDates.size() / 2)
						+ orderedDates.get(orderedDates.size() / 2 - 1)) / 2;
			} else {
				medianCommits = (double) orderedDates.get(orderedDates.size() / 2);
			}
		}
	}

	public int getCommitsPulls() {
		return commitsPulls;
	}

	public void setCommitsPulls(int commitsPulls) {
		this.commitsPulls = commitsPulls;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public double getBuggyPercent() {
		return buggyPercent;
	}

	public void setBuggyPercent(double buggyPercent) {
		this.buggyPercent = buggyPercent;
	}

	public void setInsertionTests(boolean b) {
		this.insertionTests = b;

	}

	public int getInsertionTestsCount() {
		return insertionTestsCount;
	}

	public void setInsertionTestsCount(int insertionTestsCount) {
		this.insertionTestsCount = insertionTestsCount;
	}

	public double getPullRequestsMerged() {
		return pullRequestsMerged;
	}

	public void setPullRequestsMerged(double pullRequestsMerged) {
		this.pullRequestsMerged = pullRequestsMerged;
	}


	public int getEmptyNatureCount() {
		return emptyNatureCount;
	}

	public void setEmptyNatureCount(int emptyNatureCount) {
		this.emptyNatureCount = emptyNatureCount;
	}

	public int getEmptySizeCount() {
		return emptySizeCount;
	}

	public void setEmptySizeCount(int emptySizeCount) {
		this.emptySizeCount = emptySizeCount;
	}

	public int getTinyCount() {
		return tinyCount;
	}

	public void setTinyCount(int tinyCount) {
		this.tinyCount = tinyCount;
	}

	public int getSmallCount() {
		return smallCount;
	}

	public void setSmallCount(int smallCount) {
		this.smallCount = smallCount;
	}

	public int getMediumCount() {
		return mediumCount;
	}

	public void setMediumCount(int mediumCount) {
		this.mediumCount = mediumCount;
	}

	public int getLargeCount() {
		return largeCount;
	}

	public void setLargeCount(int largeCount) {
		this.largeCount = largeCount;
	}

	public boolean isInsertionTests() {
		return insertionTests;
	}

	public void setMeanCommits(double meanCommits) {
		this.meanCommits = meanCommits;
	}

	public void setMedianCommits(double medianCommits) {
		this.medianCommits = medianCommits;
	}

	public void setMeanAdditions(double meanAdditions) {
		this.meanAdditions = meanAdditions;
	}

	public void setMedianAdditions(double medianAdditions) {
		this.medianAdditions = medianAdditions;
	}

	public void setMeanDeletions(double meanDeletions) {
		this.meanDeletions = meanDeletions;
	}

	public void setMedianDeletions(double medianDeletions) {
		this.medianDeletions = medianDeletions;
	}

	public void setMeanModified(double meanModified) {
		this.meanModified = meanModified;
	}

	public void setMedianModified(double medianModified) {
		this.medianModified = medianModified;
	}

	public void setTimeOnProject(int timeOnProject) {
		this.timeOnProject = timeOnProject;
	}

	public int getManagementCount() {
		return managementCount;
	}

	public void setManagementCount(int managementCount) {
		this.managementCount = managementCount;
	}

	public int getReengineeringCount() {
		return reengineeringCount;
	}

	public void setReengineeringCount(int reengineeringCount) {
		this.reengineeringCount = reengineeringCount;
	}

	public int getCorrectiveEngineeringCount() {
		return correctiveEngineeringCount;
	}

	public void setCorrectiveEngineeringCount(int correctiveEngineeringCount) {
		this.correctiveEngineeringCount = correctiveEngineeringCount;
	}

	public int getForwardEngineeringCount() {
		return forwardEngineeringCount;
	}

	public void setForwardEngineeringCount(int forwardEngineeringCount) {
		this.forwardEngineeringCount = forwardEngineeringCount;
	}

	public int getUncategorizedCount() {
		return uncategorizedCount;
	}

	public void setUncategorizedCount(int uncategorizedCount) {
		this.uncategorizedCount = uncategorizedCount;
	}

	public boolean isBuggy() {
		return buggy;
	}

	public void setBuggy(boolean buggy) {
		this.buggy = buggy;
	}

}
