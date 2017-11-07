package start;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import generators.Commits;
import generators.Issues;
import generators.Users;

public class Main {

	public static void main(String[] args) {

		List<String> projects = new ArrayList<>();
		List<String> urls = new ArrayList<>();
		/*
		 * projects.add("presto"); urls.add("prestodb/presto");
		 */
		// projects.add("okhttp");
		// urls.add("square/okhttp");

		 projects.add("elasticsearch");
		 urls.add("elastic/elasticsearch");
		/*
		 * projects.add("spring-security");
		 * urls.add("spring-projects/spring-security");
		 */
		// projects.add("spring-boot");
		// urls.add("spring-projects/spring-boot");
		/*
		 * projects.add("netty"); urls.add("netty/netty");
		 * projects.add("elasticsearch-hadoop");
		 * urls.add("elastic/elasticsearch-hadoop"); projects.add("OpenRefine");
		 * urls.add("OpenRefine/OpenRefine"); projects.add("guice");
		 * urls.add("google/guice"); projects.add("HikariCP");
		 * urls.add("brettwooldridge/HikariCP"); projects.add("ExoPlayer");
		 * urls.add("google/ExoPlayer"); projects.add("bazel");
		 * urls.add("bazelbuild/bazel"); projects.add("MaterialDrawer");
		 * urls.add("mikepenz/MaterialDrawer"); projects.add("Signal-Android");
		 * urls.add("WhisperSystems/Signal-Android"); projects.add("Hystrix");
		 * urls.add("Netflix/Hystrix"); projects.add("material-dialogs");
		 * urls.add("afollestad/material-dialogs"); projects.add("guava");
		 * urls.add("google/guava"); projects.add("glide");
		 * urls.add("bumptech/glide"); projects.add("fresco");
		 * urls.add("facebook/fresco"); projects.add("RxJava");
		 * urls.add("ReactiveX/RxJava");
		 */

		/**
		 * // 1. Run this before everything than comment the line //
		 * Contributors.generateContributorsStats(projects, urls); //
		 * Project.generateRunAllScript(projects);
		 * 
		 * // 2. Go to github_profiles and run get_contributors_stats.sh
		 **/

		for (int i = 0; i < projects.size(); i++) {

			String project = projects.get(i);
			String url = urls.get(i);

			/**
			 * Old code // 2. Run this line than comment //
			 * JsoupManager.getStats(projects.get(i), "https://github.com/" + //
			 * urls.get(i) + "/contributors"); // 3. Run this lines than comment
			 * // Contributors.collectContributorsNames(projects.get(i)); //
			 * Users.generateUsersProfileCalls(projects.get(i)); // 4. Go to
			 * github_profiles and run the script run.sh // 5. Run this line
			 * than comment // Users.readUsersProfiles(projects.get(i));
			 **/

			// Set buggy_users.csv path
			System.out.println("Collect User Commit");
			Users.collectUsersCommits(project, url);

			System.out.println("Create Commit Calls");
			Commits.createGetCommitsCalls(project, url);
			System.out.println("Generating Repository Issues Call");
			Issues.generateRepositoryIssuesCall(project, url);
			System.out.println("Generating Issues ID");
			Issues.generateIssuesIds(project);
			System.out.println("Generating Individual Issues Call");
			Issues.generateIndividualIssuesCall(project, url);
			System.out.println("Reading Issues");
			Issues.readIssues(project);
			System.out.println("Filtering Issues by Users");
			Issues.filterIssuesByUser(project);
			System.out.println("Generating Comments Calls");
			Issues.generateCommentsCalls(project, url);
			System.out.println("Generate Pulls Calls");
			Issues.generatePullsCalls(project, url);
			System.out.println("Generate Individual Pulls Calls");
			Issues.generateIndividualPullsCalls(project, url);
			System.out.println("Read Pull Requests");
			Issues.readPullRequests(project);
			System.out.println("Read Comments");
			Issues.readComments(project);

			Commits.analyzeCommits(project);

		}

	}

}
