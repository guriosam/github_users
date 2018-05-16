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
	private String commitFrequency;

	private double REXPCommit;
	private int SEXPCommit;

	private int expActivity;
	private double REXPActivity;

	private int expReview;
	private double REXPReview;

	private double additions;
	private double totalAdditions;
	private double meanAdditions;
	private double medianAdditions;
	private double deletions;
	private double totalDeletions;
	private double meanDeletions;
	private double medianDeletions;
	private double modifiedFiles;
	private double totalModifiedFiles;
	private double meanModifiedFiles;
	private double medianModifiedFiles;
	private double linesChanged;
	private double totalLinesChanged;
	private double meanLinesChanged;
	private double medianLinesChanged;
	private double previousBuggyPercent;
	private double buggyPercent;
	private double ownership;
	private double medianOwnership;

	private String nature;

	private int emptyNatureCount;
	private int managementCount;
	private int reengineeringCount;
	private int correctiveEngineeringCount;
	private int forwardEngineeringCount;
	private int uncategorizedCount;

	private double emptyNaturePercent;
	private double managementPercent;
	private double reengineeringPercent;
	private double correctiveEngineeringPercent;
	private double forwardEngineeringPercent;
	private double uncategorizedPercent;

	private boolean isEmptySize;
	private boolean isTiny;
	private boolean isSmall;
	private boolean isMedium;
	private boolean isLarge;

	private int emptySizeCount;
	private int tinyCount;
	private int smallCount;
	private int mediumCount;
	private int largeCount;
	private double emptySizePercent;
	private double tinyPercent;
	private double smallPercent;
	private double mediumPercent;
	private double largePercent;

	private int commitsPulls;

	private int activeDays;
	private int timeOnProject;
	private int repositoryTime;

	private int numberIssueComments;
	private int numberPullComments;
	private int numberCommitComments;
	private int numberOpenIssues;
	private int numberClosedIssues;
	private double pullRequestsMerged;
	private double percentPullRequestsMerged;
	private int numberOpenPullRequests;
	private int numberClosedPullRequests;
	private int numberRequestedReviewer;
	private boolean hasTests;
	private int testsCount;
	private int insertionPoints;
	private int previousInsertionPoints;
	private double testPresence;

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

	public double getMeanModifiedFiles() {
		return meanModifiedFiles;
	}

	public void setMeanModifiedFiles(List<Integer> files) {

		Util.sortList(files);

		if (files.size() > 0) {
			meanModifiedFiles = (double) this.modifiedFiles / files.size();
		} else {
			meanModifiedFiles = 0.0;
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

	public void setTimeOnProject(String minimumDate, String maximumDate) {

		timeOnProject = (Days.daysBetween(LocalDateTime.parse(minimumDate), LocalDateTime.parse(maximumDate)).getDays()
				+ 1);

	}

	public int getNumberIssueComments() {
		return numberIssueComments;
	}

	public void setNumberIssueComments(int numberIssueComments) {
		this.numberIssueComments = numberIssueComments;
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
		return medianModifiedFiles;
	}

	public void setMedianModifiedFiles(List<Integer> files) {
		Util.sortList(files);

		if (files.size() > 0) {
			if (files.size() % 2 == 0) {
				this.medianModifiedFiles = ((files.get(files.size() / 2) + files.get(files.size() / 2 - 1)) / 2);
			} else {
				this.medianModifiedFiles = (files.get(files.size() / 2));
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
					/ (Days.daysBetween(LocalDateTime.parse(minimumDate), LocalDateTime.parse(maximumDate)).getDays()
							+ 1);
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
		this.hasTests = b;

	}

	public int getInsertionTestsCount() {
		return testsCount;
	}

	public void setInsertionTestsCount(int insertionTestsCount) {
		this.testsCount = insertionTestsCount;
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
		return hasTests;
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

	public void setMeanModifiedFiles(double meanModifiedFiles) {
		this.meanModifiedFiles = meanModifiedFiles;
	}

	public void setMedianModifiedFiles(double medianModifiedFiles) {
		this.medianModifiedFiles = medianModifiedFiles;
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

	public void setTestPresence(double testPresence) {
		this.testPresence = testPresence;
	}

	public double getTestPresence() {
		return this.testPresence;
	}

	public double getEmptySizePercent() {
		return emptySizePercent;
	}

	public void setEmptySizePercent(double emptySizePercent) {
		this.emptySizePercent = emptySizePercent;
	}

	public double getTinyPercent() {
		return tinyPercent;
	}

	public void setTinyPercent(double tinyPercent) {
		this.tinyPercent = tinyPercent;
	}

	public double getSmallPercent() {
		return smallPercent;
	}

	public void setSmallPercent(double smallPercent) {
		this.smallPercent = smallPercent;
	}

	public double getMediumPercent() {
		return mediumPercent;
	}

	public void setMediumPercent(double mediumPercent) {
		this.mediumPercent = mediumPercent;
	}

	public double getLargePercent() {
		return largePercent;
	}

	public void setLargePercent(double largePercent) {
		this.largePercent = largePercent;
	}

	public double getEmptyNaturePercent() {
		return emptyNaturePercent;
	}

	public void setEmptyNaturePercent(double emptyNaturePercent) {
		this.emptyNaturePercent = emptyNaturePercent;
	}

	public double getManagementPercent() {
		return managementPercent;
	}

	public void setManagementPercent(double managementPercent) {
		this.managementPercent = managementPercent;
	}

	public double getReengineeringPercent() {
		return reengineeringPercent;
	}

	public void setReengineeringPercent(double reengineeringPercent) {
		this.reengineeringPercent = reengineeringPercent;
	}

	public double getCorrectiveEngineeringPercent() {
		return correctiveEngineeringPercent;
	}

	public void setCorrectiveEngineeringPercent(double correctiveEngineeringPercent) {
		this.correctiveEngineeringPercent = correctiveEngineeringPercent;
	}

	public double getForwardEngineeringPercent() {
		return forwardEngineeringPercent;
	}

	public void setForwardEngineeringPercent(double forwardEngineeringPercent) {
		this.forwardEngineeringPercent = forwardEngineeringPercent;
	}

	public double getUncategorizedPercent() {
		return uncategorizedPercent;
	}

	public void setUncategorizedPercent(double uncategorizedPercent) {
		this.uncategorizedPercent = uncategorizedPercent;
	}

	public int getNumberPullComments() {
		return numberPullComments;
	}

	public void setNumberPullComments(int numberPullComments) {
		this.numberPullComments = numberPullComments;
	}

	public int getNumberCommitComments() {
		return numberCommitComments;
	}

	public void setNumberCommitComments(int numberCommitComments) {
		this.numberCommitComments = numberCommitComments;
	}

	public double getMedianLinesChanged() {
		return medianLinesChanged;
	}

	public void setMedianLinesChanged(List<Double> medianLinesChanged) {
		Util.sortList(medianLinesChanged);

		if (medianLinesChanged.size() > 0) {
			if (medianLinesChanged.size() % 2 == 0) {
				this.medianLinesChanged = ((medianLinesChanged.get(medianLinesChanged.size() / 2)
						+ medianLinesChanged.get(medianLinesChanged.size() / 2 - 1)) / 2);
			} else {
				this.medianLinesChanged = (medianLinesChanged.get(medianLinesChanged.size() / 2));
			}
		}
	}

	public double getLinesChanged() {
		return linesChanged;
	}

	public void setLinesChanged(double linesChanged) {
		this.linesChanged = linesChanged;
	}

	public double getMeanLinesChanged() {
		return meanLinesChanged;
	}

	public void setMeanLinesChanged(List<Double> meanLinesChanged) {
		Util.sortList(meanLinesChanged);

		if (meanLinesChanged.size() > 0) {
			this.meanLinesChanged = (double) this.linesChanged / meanLinesChanged.size();
		} else {
			this.meanLinesChanged = 0.0;
		}
	}

	public String getCommitFrequency() {
		return commitFrequency;
	}

	public void setCommitFrequency(String commitFrequency) {
		this.commitFrequency = commitFrequency;
	}

	public boolean isEmptySize() {
		return isEmptySize;
	}

	public void setEmptySize(boolean isEmptySize) {
		this.isEmptySize = isEmptySize;
	}

	public boolean isTiny() {
		return isTiny;
	}

	public void setTiny(boolean isTiny) {
		this.isTiny = isTiny;
	}

	public boolean isSmall() {
		return isSmall;
	}

	public void setSmall(boolean isSmall) {
		this.isSmall = isSmall;
	}

	public boolean isLarge() {
		return isLarge;
	}

	public void setLarge(boolean isLarge) {
		this.isLarge = isLarge;
	}

	public boolean isMedium() {
		return isMedium;
	}

	public void setMedium(boolean isMedium) {
		this.isMedium = isMedium;
	}

	public double getREXP() {
		return REXPCommit;
	}

	public void setREXP(double rEXP2) {
		REXPCommit = rEXP2;
	}

	public int getSEXP() {
		return SEXPCommit;
	}

	public void setSEXP(int sEXP) {
		SEXPCommit = sEXP;
	}

	public double getTotalAdditions() {
		return totalAdditions;
	}

	public void setTotalAdditions(double totalAdditions) {
		this.totalAdditions = totalAdditions;
	}

	public double getTotalDeletions() {
		return totalDeletions;
	}

	public void setTotalDeletions(double totalDeletions) {
		this.totalDeletions = totalDeletions;
	}

	public double getTotalModifiedFiles() {
		return totalModifiedFiles;
	}

	public void setTotalModifiedFiles(double totalModifiedFiles) {
		this.totalModifiedFiles = totalModifiedFiles;
	}

	public double getTotalLinesChanged() {
		return totalLinesChanged;
	}

	public void setTotalLinesChanged(double totalLinesChanged) {
		this.totalLinesChanged = totalLinesChanged;
	}

	public double getMedianModifiedFiles() {
		return medianModifiedFiles;
	}

	public void setMeanLinesChanged(double meanLinesChanged) {
		this.meanLinesChanged = meanLinesChanged;
	}

	public void setMedianLinesChanged(double medianLinesChanged) {
		this.medianLinesChanged = medianLinesChanged;
	}

	public int getExpActivity() {
		return expActivity;
	}

	public void setExpActivity(int expActivity) {
		this.expActivity = expActivity;
	}

	public double getREXPActivity() {
		return REXPActivity;
	}

	public void setREXPActivity(double rEXPActivity) {
		REXPActivity = rEXPActivity;
	}

	public int getExpReview() {
		return expReview;
	}

	public void setExpReview(int expReview) {
		this.expReview = expReview;
	}

	public double getREXPReview() {
		return REXPReview;
	}

	public void setREXPReview(double rEXPReview) {
		REXPReview = rEXPReview;
	}

	public String getNature() {
		return nature;
	}

	public void setNature(String nature) {
		this.nature = nature;
	}

	@Override
	public String toString() {
		return "UserInfo [hash=" + hash + ", login=" + login + ", buggy=" + buggy + ", commits=" + commits
				+ ", meanCommits=" + meanCommits + ", medianCommits=" + medianCommits + ", commitFrequency="
				+ commitFrequency + ", REXPCommit=" + REXPCommit + ", SEXPCommit=" + SEXPCommit + ", expActivity="
				+ expActivity + ", REXPActivity=" + REXPActivity + ", expReview=" + expReview + ", REXPReview="
				+ REXPReview + ", additions=" + additions + ", totalAdditions=" + totalAdditions + ", meanAdditions="
				+ meanAdditions + ", medianAdditions=" + medianAdditions + ", deletions=" + deletions
				+ ", totalDeletions=" + totalDeletions + ", meanDeletions=" + meanDeletions + ", medianDeletions="
				+ medianDeletions + ", modifiedFiles=" + modifiedFiles + ", totalModifiedFiles=" + totalModifiedFiles
				+ ", meanModifiedFiles=" + meanModifiedFiles + ", medianModifiedFiles=" + medianModifiedFiles
				+ ", linesChanged=" + linesChanged + ", totalLinesChanged=" + totalLinesChanged + ", meanLinesChanged="
				+ meanLinesChanged + ", medianLinesChanged=" + medianLinesChanged + ", buggyPercent=" + buggyPercent
				+ ", nature=" + nature + ", emptyNatureCount=" + emptyNatureCount + ", managementCount="
				+ managementCount + ", reengineeringCount=" + reengineeringCount + ", correctiveEngineeringCount="
				+ correctiveEngineeringCount + ", forwardEngineeringCount=" + forwardEngineeringCount
				+ ", uncategorizedCount=" + uncategorizedCount + ", emptyNaturePercent=" + emptyNaturePercent
				+ ", managementPercent=" + managementPercent + ", reengineeringPercent=" + reengineeringPercent
				+ ", correctiveEngineeringPercent=" + correctiveEngineeringPercent + ", forwardEngineeringPercent="
				+ forwardEngineeringPercent + ", uncategorizedPercent=" + uncategorizedPercent + ", isEmptySize="
				+ isEmptySize + ", isTiny=" + isTiny + ", isSmall=" + isSmall + ", isMedium=" + isMedium + ", isLarge="
				+ isLarge + ", emptySizeCount=" + emptySizeCount + ", tinyCount=" + tinyCount + ", smallCount="
				+ smallCount + ", mediumCount=" + mediumCount + ", largeCount=" + largeCount + ", emptySizePercent="
				+ emptySizePercent + ", tinyPercent=" + tinyPercent + ", smallPercent=" + smallPercent
				+ ", mediumPercent=" + mediumPercent + ", largePercent=" + largePercent + ", commitsPulls="
				+ commitsPulls + ", activeDays=" + activeDays + ", timeOnProject=" + timeOnProject
				+ ", numberIssueComments=" + numberIssueComments + ", numberPullComments=" + numberPullComments
				+ ", numberCommitComments=" + numberCommitComments + ", numberOpenIssues=" + numberOpenIssues
				+ ", numberClosedIssues=" + numberClosedIssues + ", pullRequestsMerged=" + pullRequestsMerged
				+ ", percentPullRequestsMerged=" + percentPullRequestsMerged + ", numberOpenPullRequests="
				+ numberOpenPullRequests + ", numberClosedPullRequests=" + numberClosedPullRequests
				+ ", numberRequestedReviewer=" + numberRequestedReviewer + ", hasTests=" + hasTests + ", testsCount="
				+ testsCount + ", insertionPoints=" + insertionPoints + ", testPresence=" + testPresence + "]";
	}

	public double getOwnership() {
		return ownership;
	}

	public void setOwnership(double ownership) {
		this.ownership = ownership;
	}

	public double getMedianOwnership() {
		return medianOwnership;
	}

	public void setMedianOwnership(List<Double> medianOwnership) {

		Util.sortList(medianOwnership);

		if (medianOwnership.size() > 0) {
			if (medianOwnership.size() % 2 == 0) {
				this.medianOwnership = ((medianOwnership.get(medianOwnership.size() / 2)
						+ medianOwnership.get(medianOwnership.size() / 2 - 1)) / 2);
			} else {
				this.medianOwnership = (medianOwnership.get(medianOwnership.size() / 2));
			}
		}

	}

	public void setRepositoryTime(String firstDate, String authorDate) {
		repositoryTime = (Days.daysBetween(LocalDateTime.parse(firstDate), LocalDateTime.parse(authorDate)).getDays()
				+ 1);

	}

	public int getRepositoryTime() {
		return repositoryTime;
	}

	public int getPreviousInsertionPoints() {
		return previousInsertionPoints;
	}

	public void setPreviousInsertionPoints(int previousInsertionPoints) {
		this.previousInsertionPoints = previousInsertionPoints;
	}

	public double getPreviousBuggyPercent() {
		return previousBuggyPercent;
	}

	public void setPreviousBuggyPercent(double previousBuggyPercent) {
		this.previousBuggyPercent = previousBuggyPercent;
	}

}
