package start;

import java.util.ArrayList;
import java.util.List;

import endpoints.CommitsAPI;
import endpoints.IssuesAPI;
import endpoints.PullsAPI;
import generators.Commits;
import generators.Issues;
import generators.PullRequests;
import generators.Users;
import objects.UserPoint;

public class Main {

	public static void main(String[] args) {

		List<String> projects = new ArrayList<>();
		List<String> urls = new ArrayList<>();

		projects.add("elasticsearch");
		urls.add("elastic/elasticsearch");
		projects.add("spring-boot");
		urls.add("spring-projects/spring-boot");
		projects.add("netty");
		urls.add("netty/netty");
		projects.add("bazel");
		urls.add("bazelbuild/bazel");
		projects.add("presto");
		urls.add("prestodb/presto");
		projects.add("Signal-Android");
		urls.add("signalapp/Signal-Android");
		projects.add("okhttp");
		urls.add("square/okhttp");
		
		
		projects.add("elasticsearch-hadoop");
		urls.add("elastic/elasticsearch-hadoop");
		projects.add("HikariCP");
		urls.add("brettwooldridge/HikariCP");
		projects.add("ExoPlayer");
		urls.add("google/ExoPlayer");
		projects.add("MaterialDrawer");
		urls.add("mikepenz/MaterialDrawer");
		projects.add("Hystrix");
		urls.add("Netflix/Hystrix");
		projects.add("material-dialogs");
		urls.add("afollestad/material-dialogs");
		projects.add("guava");
		urls.add("google/guava");
		projects.add("glide");
		urls.add("bumptech/glide");
		projects.add("fresco");
		urls.add("facebook/fresco");
		projects.add("RxJava");
		urls.add("ReactiveX/RxJava");
		 
		int total = 0;

		for (int i = 0; i < projects.size(); i++) {

			String project = projects.get(i);
			String url = urls.get(i);
			
			System.out.println(project);
			
			//CommitsAPI.downloadAllCommits(project, url);
			//Commits.collectHashsFromUsers(project);
			//CommitsAPI.downloadAllIndividualCommits(project, url);
			//Git.cloneProject(url);

			// System.out.println("********** " + project.toUpperCase() + "
			// *********");
			
			//CommitsAPI.downloadGroupOfCommitsByAuthor(project, url);
			//CommitsAPI.downloadUserCommitsFromMaster(project, url);
			
			/*// ISSUES
			System.out.println("Generating Repository Issues Call");
			IssuesAPI.generateRepositoryIssuesCall(project, url);
			System.out.println("Generating Issues ID");
			Issues.generateIssuesIds(project);
			System.out.println("Generating Individual Issues Call");
			Issues.generateIndividualIssuesCall(project, url);
			System.out.println("Reading Issues");
			Issues.readIssues(project);
			System.out.println("Filtering Issues by Users");
			Issues.filterIssuesByUser(project);
			*/
			
			
			// COMMENTS
			//System.out.println("Generating Comments Calls");
			//Issues.generateCommentsCalls(project, url);
		
			// PULL REQUESTS

			 Issues.generatePullsCalls(project, url);
			 PullRequests.generateIndividualPullsCalls(project, url);
			 
			System.out.println("Collecting Not Merged Pulls");
			PullRequests.generatePullsIds(project);
			 
			System.out.println("Downloading Commits of Pulls");
			PullRequests.collectCommitsOnPullRequests(project, url);

			System.out.println("Collecting Pull Commits Hashs");
			PullRequests.collectPullCommitsHashs(project);
			
			PullRequests.collectPullCommitsByUser(project, url);
		
			System.out.println("Collecting Pull Comments");
			
			PullsAPI.downloadCommentsInReviews(project, url);
			// System.out.println("Comparing Hashs");
			/*
			 * TODO
			Issues.compareHashs(project, url);
			
			*/
			
		//	PullRequests.getIdsFromPerilI(project, url);
		//	PullRequests.getIdsFromPerilII(project);
			
			//Issues.readPullRequests(project);

			/*
			 * TODO
				PullRequests.collectPullCommitsByUser(project, url);
			 */
			// FINAL DATA
			 System.out.println("Analyzing all info");

			//Commits.collectHashsFromUsers(project);
			List<UserPoint> userPoints = Users.organizePoints(project);
			//Commits.analyzeCommits(project, userPoints);

			//PullRequests.analysePulls(project, userPoints);

			//PullRequests.joinPullsToOutput(project);
			// System.out.println("done");

			/*
			
			List<UserPullRequest> userPulls = Issues.getPullRequests(project);
			List<String> h1 = IO.readAnyFile(Util.getPullsFolder(project) + "heuristic1.txt");
			List<String> h2 = IO.readAnyFile(Util.getPullsFolder(project) + "pull_requests_h2.txt");
			int count = 0;
			for (UserPoint p : userPoints) {
				String name = p.getName();
				for (UserPullRequest userPull : userPulls) {
					if (userPull.getUser().equals(name)) {
						if (h1.contains(userPull.getId())) {
							count++;
							break;
						}

						if (h2.contains(userPull.getId())) {
							count++;
							break;
						}
					}
				}
			}
			
			System.out.println(project + ": " + count);
			
			*/
			
			/*
			 * int count = 0; for(UserPoint p : userPoints){ String name =
			 * p.getName();
			 * 
			 * count += IO.filesOnFolder(Util.getUserCommitsPath(project,
			 * name)).size(); } total += count; System.out.println(count);
			 */

		}

	}

}
