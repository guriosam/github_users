package start;

import java.util.ArrayList;
import java.util.List;

import generators.Commits;
import generators.Issues;
import generators.PullRequests;
import generators.Users;
import utils.Util;

public class Main {

	public static void main(String[] args) {

		List<String> projects = new ArrayList<>();
		List<String> urls = new ArrayList<>();

		//projects.add("elasticsearch");
		//urls.add("elastic/elasticsearch");
		//projects.add("spring-boot");
		//urls.add("spring-projects/spring-boot");
		//projects.add("netty");
		//urls.add("netty/netty");
		//projects.add("bazel");
		//urls.add("bazelbuild/bazel");
		//projects.add("presto");
		//urls.add("prestodb/presto");
		//projects.add("Signal-Android");
		//urls.add("WhisperSystems/Signal-Android");
		//projects.add("okhttp");
		//urls.add("square/okhttp");

		for (int i = 0; i < projects.size(); i++) {

			String project = projects.get(i);
			String url = urls.get(i);

			System.out.println("********** " + project.toUpperCase() + " *********");

			/*// Download all user's commits of the project ( files with max 30
			// commits each)
			Users.downloadUsersCommitsBatches(project, url);
			// Reading file with 30 commits and downloading each commit
			// individually
			Commits.downloadUserCommitsFromMaster(project, url);

			// ISSUES
			// System.out.println("Generating Repository Issues Call");
			Issues.generateRepositoryIssuesCall(project, url);
			// System.out.println("Generating Issues ID");
			Issues.generateIssuesIds(project);
			// System.out.println("Generating Individual Issues Call");
			Issues.generateIndividualIssuesCall(project, url);
			// System.out.println("Reading Issues");
			Issues.readIssues(project);
			// System.out.println("Filtering Issues by Users");
			Issues.filterIssuesByUser(project);

			// COMMENTS System.out.println("Generating Comments Calls");
			Issues.generateCommentsCalls(project, url);

			// COMMENTS System.out.println("Read Comments");
			Issues.readComments(project);

			// PULL REQUESTS
			Issues.generatePullsCalls(project, url);
			PullRequests.generateIndividualPullsCalls(project, url);
			// System.out.println("Collecting Not Merged Pulls");
			PullRequests.generatePullsIds(project, false);
			// System.out.println("Downloading Commits of Not Merged Pulls");
			PullRequests.collectCommitsOnPullRequests(project, url, false);
			// System.out.println("Collecting Pull Commits Hashs");
			PullRequests.collectPullCommitsHashs(project);
			// System.out.println("Comparing Hashs");
			Issues.compareHashs(project, url);

			// System.out.println("Collecting Not Merged Pulls From Heuristc
			// 2");
			PullRequests.downloadIndividualPulls(project, url, Util.getPullsFolder(project) + "pulls_distinct.txt");
			PullRequests.collectCommitsOnPullRequests(project, url, false);
			PullRequests.collectPullCommitsHashs(project);
			Issues.readPullRequests(project);

			PullRequests.generatePullsIds(project, true);
			PullRequests.collectCommitsOnPullRequests(project, url, true);
			PullRequests.collectPullCommitsByUser(project, url);

*/
			// FINAL DATA
			// System.out.println("Analyzing all info");
			Commits.analyzeCommits(project);

		}

	}

}
