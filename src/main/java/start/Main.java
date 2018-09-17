package start;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import endpoints.CommentsAPI;
import endpoints.CommitsAPI;
import endpoints.IssuesAPI;
import endpoints.PullsAPI;
import generators.Comments;
import generators.Commits;
import generators.Issues;
import generators.PullRequests;
import generators.Users;
import metrics.CommentsMetrics;
import metrics.IssuesMetrics;
import metrics.NatureMetrics;
import metrics.PullRequestsMetrics;
import metrics.SizeMetrics;
import objects.UserPoint;
import utils.Git;
import utils.IO;
import utils.URLs;
import utils.Util;

public class Main {

	public static void main(String[] args) {

		List<String> projects = new ArrayList<>();
		
		//JavaScript
		projects.add("angular.js");
		projects.add("atom");
		projects.add("three.js");
		projects.add("webpack");
		projects.add("meteor");
		
		//Python
		projects.add("ansible");
		projects.add("scikit-learn");
		projects.add("pandas");
		projects.add("ipython");
		projects.add("salt");
		
		//Java
		projects.add("elasticsearch");
		projects.add("spring-boot");
		projects.add("netty");
		projects.add("bazel");
		projects.add("presto");
		
		//Ruby
		projects.add("fastlane");
		projects.add("vagrant");
		projects.add("metasploit-framework");
		projects.add("diaspora");
		projects.add("logstash");
		
		//PHP
		projects.add("symfony");
		projects.add("composer");
		projects.add("yii2");
		projects.add("matomo");
		projects.add("cakephp");

		for (int i = 0; i < projects.size(); i++) {

			String project = projects.get(i);

			System.out.println(project);

			CommitsAPI.downloadAllCommits(project, URLs.getUrl(project));
			Commits.collectHashsFromUsers(project);
			CommitsAPI.downloadAllIndividualCommits(project, URLs.getUrl(project));

			Git.cloneProject(project);

			// ISSUES
			// System.out.println("Generating Repository Issues Call");
			IssuesAPI.generateRepositoryIssuesCall(project, URLs.getUrl(project));
			// System.out.println("Generating Issues ID");
			Issues.generateIssuesIds(project);
			// System.out.println("Generating Individual Issues Call");
			IssuesAPI.generateIndividualIssuesCall(project, URLs.getUrl(project));
			// System.out.println("Reading Issues");
			Issues.readIssues(project);
			// System.out.println("Filtering Issues by Users");
			Issues.filterIssuesByUser(project);

			// // COMMENTS //System.out.println("Generating Comments Calls");
			IssuesAPI.generateCommentsCalls(project, URLs.getUrl(project));
			CommentsAPI.downloadGroupOfCommitComments(project, URLs.getUrl(project));
			Comments.generateCommentsIds(project);
			CommentsAPI.downloadIndividualCommitComments(project, URLs.getUrl(project));

			// PULL REQUESTS
			PullsAPI.generatePullsCalls(project, URLs.getUrl(project));
			PullRequests.generateIndividualPullsCalls(project, URLs.getUrl(project));

			PullRequests.generatePullsIds(project);

			// System.out.println("Downloading Commits of Pulls");
			PullRequests.collectCommitsOnPullRequests(project, URLs.getUrl(project));

			// System.out.println("Collecting Pull Commits Hashs");
			PullRequests.collectPullCommitsHashs(project);

			// System.out.println("Collecting Pull Comments");
			PullRequests.downloadPullsCommits(project);
			PullRequests.analyzePullCommits(project);
			PullsAPI.downloadCommentsInReviews(project, URLs.getUrl(project));

			// PullRequests.getIdsFromPerilI(project, URLs.getUrl(project));
			// PullRequests.getIdsFromPerilII(project);

			PullRequests.readPullRequests(project);

			Commits.collectOwnership(project);

			Commits.collectHashsFromUsers(project);

			Main.outputs(project);

		}

	}

	public static void outputs(String project) {

		List<UserPoint> userPoints = Users.organizePoints(project);

		System.out.println("Users: " + userPoints.size());

		Commits.generateCommitsInfo(project);
		PullRequestsMetrics.analyzePullRequestsMetrics(project, userPoints);
		IssuesMetrics.analyzeIssuesMetrics(project, userPoints);
		CommentsMetrics.analyzeComments(project, userPoints);
		NatureMetrics.analyzeNature(project, userPoints);
		SizeMetrics.analyzeSize(project, userPoints);
		Commits.analyzeOwnership(project);

		System.out.println("Analyzing all info");
		Commits.analyzeCommits(project, userPoints);

		// Calculate how many Commits, Issues, Pulls and Users we analyzed.
		// calculateMiningInfo(project);

		System.out.println("Finished");
	}

	public static void calculateMiningInfo(String project) {

		List<String> commits = IO.filesOnFolder(Util.getIndividualCommitsPath(project));
		List<String> issues = IO.filesOnFolder(Util.getIndividualIssuesFolder(project));
		List<String> pulls = IO.filesOnFolder(Util.getIndividualPullsFolder(project));
		HashMap<String, List<String>> userCount = Issues.readComments(project);
		HashMap<String, List<String>> userPullsCommentCount = PullRequests.readComments(project);
		HashMap<String, List<String>> userCommitCommentCount = Comments.readCommitComments(project);
		int comments = 0;

		for (String u : userCount.keySet()) {
			comments += userCount.get(u).size();
		}
		for (String u : userPullsCommentCount.keySet()) {
			comments += userPullsCommentCount.get(u).size();
		}
		for (String u : userCommitCommentCount.keySet()) {
			comments += userCommitCommentCount.get(u).size();
		}

		System.out.println("Commits: " + commits.size());
		System.out.println("Issues: " + issues.size());
		System.out.println("Pulls: " + pulls.size());
		System.out.println("Comments: " + comments + "\n");

	}

}
