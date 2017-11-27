package generators;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import objects.UserCommit;
import objects.UserIssue;
import objects.UserPullRequest;
import utils.Config;
import utils.IO;
import utils.JSONManager;
import utils.LocalPaths;
import utils.Util;

public class Issues {

	public static void filterIssuesByUser(String project) {

		List<String> names = Util.getBuggyUsers(project);
		List<UserIssue> issues = readIssues(project);
		HashMap<String, List<UserIssue>> userClosedByIssues = new HashMap<>();
		HashMap<String, List<UserIssue>> userOpenByIssues = new HashMap<>();

		for (String name : names) {

			for (UserIssue ui : issues) {
				if (ui.getClosedBy() != null && ui.getClosedBy().equals(name)) {
					if (!userClosedByIssues.containsKey(name)) {
						userClosedByIssues.put(name, new ArrayList<UserIssue>());
					}
					userClosedByIssues.get(name).add(ui);
				}
				if (ui.getCreator() != null && ui.getCreator().equals(name)) {
					if (!userOpenByIssues.containsKey(name)) {
						userOpenByIssues.put(name, new ArrayList<UserIssue>());
					}
					userOpenByIssues.get(name).add(ui);
				}

			}

		}

		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		for (String name : userClosedByIssues.keySet()) {
			String path = LocalPaths.PATH + project + "/users/" + name + "/closed_issues.json";
			String output = gson.toJson(userClosedByIssues.get(name));
			IO.writeAnyString(path, output);
		}

		for (String name : userOpenByIssues.keySet()) {
			String path = LocalPaths.PATH + project + "/users/" + name + "/opened_issues.json";
			String output = gson.toJson(userOpenByIssues.get(name));
			IO.writeAnyString(path, output);
		}

	}

	@SuppressWarnings("rawtypes")
	public static List<UserIssue> readIssues(String project) {

		try {

			String path = Util.getIssuesPath(project);
			List<String> ids = IO.readAnyFile(path + "ids.txt");

			List<UserIssue> userIssues = new ArrayList<>();
			Gson gson = new GsonBuilder().setPrettyPrinting().create();

			for (String id : ids) {

				String fileData = new String(
						Files.readAllBytes(Paths.get(Util.getIndividualIssuesFolder(project) + id + ".json")));
				LinkedTreeMap issues = gson.fromJson(fileData, LinkedTreeMap.class);

				UserIssue ui = new UserIssue();

				ui.setNumber(id);

				if (issues.containsKey("state")) {
					String state = (String) issues.get("state");
					ui.setState(state);
				}
				if (issues.containsKey("created_at")) {
					String createdAt = (String) issues.get("created_at");
					ui.setCreatedAt(createdAt);
				}
				if (issues.containsKey("closed_at")) {
					String closedAt = (String) issues.get("closed_at");
					ui.setClosedAt(closedAt);
				}
				if (issues.containsKey("closed_by")) {
					LinkedTreeMap user = (LinkedTreeMap) issues.get("closed_by");
					if (user != null && user.containsKey("login")) {
						String closedBy = (String) user.get("login");
						ui.setClosedBy(closedBy);
					} else {
						ui.setClosedBy("null");
					}

				}
				if (issues.containsKey("user")) {
					LinkedTreeMap user = (LinkedTreeMap) issues.get("user");
					String login = (String) user.get("login");
					ui.setCreator(login);
				}
				userIssues.add(ui);
			}

			String output = gson.toJson(userIssues);

			IO.writeAnyString(LocalPaths.PATH + project + "/all_issues.json", output);

			return userIssues;

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return new ArrayList<>();

	}

	public static HashMap<String, Integer> readComments(String project) {

		try {

			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			List<String> commentsUrls = new ArrayList<>();
			String path = LocalPaths.PATH + project + "/issues/comments/";
			List<String> files = IO.filesOnFolder(path);

			HashMap<String, Integer> userCount = new HashMap<String, Integer>();

			for (String file : files) {

				String fileData = new String(Files.readAllBytes(Paths.get(path + file)));
				List<LinkedTreeMap> comments = gson.fromJson(fileData, List.class);

				for (LinkedTreeMap<?, ?> comment : comments) {

					LinkedTreeMap user = (LinkedTreeMap) comment.get("user");
					String login = (String) user.get("login");

					if (!userCount.containsKey(login)) {
						userCount.put(login, 0);
					}

					int count = userCount.get(login);
					count++;
					userCount.replace(login, count);

				}

			}

			return userCount;

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return new HashMap<>();

	}

	public static List<UserPullRequest> getPullRequests(String project) {

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String path = LocalPaths.PATH + project + "/pull_requests.json";
		List<UserPullRequest> userPull = new ArrayList<>();

		List<String> pullMerged = IO.readAnyFile(Util.getPullsFolder(project) + "heuristic1.txt");
		List<String> heuristc2 = IO.readAnyFile(Util.getPullsFolder(project) + "pull_requests_h2.txt");

		int countH2 = 0;
		int countH1 = 0;

		try {
			String fileData = new String(Files.readAllBytes(Paths.get(path)));
			List<LinkedTreeMap> pulls = gson.fromJson(fileData, List.class);

			for (LinkedTreeMap pull : pulls) {

				boolean m = false;
				boolean h = true;
				UserPullRequest upr = new UserPullRequest();
				if (pull.containsKey("id")) {
					String number = (String) pull.get("id");
					upr.setId(number);

					for (String pm : pullMerged) {
						if (number.equals(pm)) {
							m = true;
						}
					}

					for (String pm : heuristc2) {
						if (number.equals(pm)) {

							if (!m) {
								countH2++;
							}

							h = false;
							m = true;

						}
					}
					
					if(h && m){
						countH1++;
					}

				}
				if (pull.containsKey("state")) {
					upr.setState((String) pull.get("state"));

				}
				if (pull.containsKey("merged")) {
					if (m) {
						upr.setMerged(true);
					} else {
						upr.setMerged((boolean) pull.get("merged"));
					}

				}
				if (pull.containsKey("merged_by")) {
					if (pull != null && pull.containsKey("merged_by")) {
						upr.setMerged_by((String) pull.get("merged_by"));
					}
				}
				if (pull.containsKey("user")) {
					String number = (String) pull.get("user");
					upr.setUser(number);
				}

				if (pull.containsKey("reviewers")) {
					List<String> users = (List<String>) pull.get("reviewers");

					List<String> rev = new ArrayList<>();
					for (String user : users) {
						rev.add(user);
					}
					upr.setReviewers(rev);
				}

				userPull.add(upr);

			}

			System.out.println("Heuristic 1: " + countH1);
			System.out.println("Heuristic 2: " + countH2);

			return userPull;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ArrayList<UserPullRequest>();

	}

	public static void readPullRequests(String project) {
		
		System.out.println("Reading Pull Requests");

		try {

			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String path = LocalPaths.PATH + project + "/pulls/individual/";
			List<String> files = IO.filesOnFolder(path);
			List<UserPullRequest> userPull = new ArrayList<>();

			for (String file : files) {

				if (!file.contains("json")) {
					continue;
				}

				String fileData = new String(Files.readAllBytes(Paths.get(path + file)));
				LinkedTreeMap pull = gson.fromJson(fileData, LinkedTreeMap.class);

				UserPullRequest upr = new UserPullRequest();

				if (pull.containsKey("user")) {

					LinkedTreeMap user = (LinkedTreeMap) pull.get("user");

					if (user != null && user.containsKey("login")) {
						String login = (String) user.get("login");
						upr.setUser(login);
					}

				}

				if (pull.containsKey("state")) {
					upr.setState((String) pull.get("state"));

				}
				if (pull.containsKey("merged")) {
					upr.setMerged((boolean) pull.get("merged"));
				}
				if (pull.containsKey("merged_by")) {
					LinkedTreeMap user = (LinkedTreeMap) pull.get("merged_by");
					if (user != null && user.containsKey("login")) {
						upr.setMerged_by((String) user.get("login"));
					}
				}
				if (pull.containsKey("requested_reviewers")) {
					List<LinkedTreeMap> users = (List<LinkedTreeMap>) pull.get("requested_reviewers");

					List<String> rev = new ArrayList<>();
					for (LinkedTreeMap<?, ?> user : users) {
						if (user != null && user.containsKey("login")) {
							rev.add((String) user.get("login"));
						}
					}

					upr.setReviewers(rev);
				}
				if (pull.containsKey("number")) {
					String number = pull.get("number") + "";
					number = number.replace(".", "");
					number = number.substring(0, number.length() - 1);
					upr.setId(number);
				}

				if (upr.getUser() != null && !upr.getUser().equals("")) {
					userPull.add(upr);
				}

			}

			String output = gson.toJson(userPull);

			IO.writeAnyString(LocalPaths.PATH + project + "/pull_requests.json", output);

		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}

	}

	public static void generatePullsCalls(String project, String url) {

		System.out.println("Generating Pulls Calls");
		
		String path = Util.getGeneralPullsFolder(project);

		for (int i = 1; i < 2000; i++) {

			String command = LocalPaths.CURL + " -i -u " + Config.USERNAME + ":" + Config.PASSWORD
					+ " \"https://api.github.com/repos/" + url + "/pulls" + "?state=all&page=" + i + "\"";

			boolean empty = JSONManager.getJSON(path + i + ".json", command);

			if (empty) {
				break;
			}
		}

	}

	public static void generateCommentsCalls(String project, String url) {

		String path = Util.getIssuesCommentsPath(project);

		for (int i = 1; i < 5000; i++) {

			String command = LocalPaths.CURL + " -i -u " + Config.USERNAME + ":" + Config.PASSWORD
					+ " \"https://api.github.com/repos/" + url + "/issues/comments?page=" + i + "\"";

			boolean empty = JSONManager.getJSON(path + "comments_" + i + ".json", command);

			if (empty) {
				break;
			}

		}

	}

	public static void generateIndividualIssuesCall(String project, String url) {

		String path = Util.getIssuesPath(project);

		List<String> ids = IO.readAnyFile(path + "issues_ids.txt");

		List<String> commands = new ArrayList<>();

		for (String id : ids) {

			File f = new File(Util.getIndividualIssuesFolder(project));
			if (!f.exists()) {
				f.mkdirs();
			}

			String command = LocalPaths.CURL + " -i -u " + Config.USERNAME + ":" + Config.PASSWORD
					+ " \"https://api.github.com/repos/" + url + "/issues/" + id + "\"";

			JSONManager.getJSON(path + "individual/" + id + ".json", command);

		}

	}

	public static void generateRepositoryIssuesCall(String project, String url) {

		// repos/:owner/:repo/issues
		String path = Util.getGeneralIssuesPath(project);

		for (int i = 1; i < 1000; i++) {
			String command = LocalPaths.CURL + " -i -u " + Config.USERNAME + ":" + Config.PASSWORD
					+ " \"https://api.github.com/repos/" + url + "/issues?state=all&page=" + i + "\"";

			boolean empty = JSONManager.getJSON(path + i + ".json", command);

			if (empty) {
				break;
			}

		}

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void generateIssuesIds(String project) {

		try {

			List<String> ids = new ArrayList<>();
			String path = Util.getIssuesPath(project);
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			List<String> files = IO.filesOnFolder(Util.getGeneralIssuesPath(project));

			for (String file : files) {

				if (!file.contains("json") || file.contains("ids")) {
					continue;
				}

				String fileData = new String(Files.readAllBytes(Paths.get(Util.getGeneralIssuesPath(project) + file)));
				List<LinkedTreeMap> issues = gson.fromJson(fileData, List.class);

				for (LinkedTreeMap<?, ?> c : issues) {

					if (c.containsKey("pull_request")) {
						continue;
					}

					String id = c.get("number") + "";
					id = id.replace(".", "");
					id = id.substring(0, id.length() - 1);
					ids.add(id);

				}

			}

			IO.writeAnyFile(path + "issues_ids.txt", ids);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	@SuppressWarnings("rawtypes")
	public static void compareHashs(String project, String url) {

		String pathGitProject = LocalPaths.PATH_GIT + project + "/hashs.txt";

		List<String> hashsGit = IO.readAnyFile(pathGitProject);

		String pathGitProfile = Util.getPullCommitsPath(project) + "hashs_commits_not_merged.txt";

		List<String> hashsGithub = IO.readAnyFile(pathGitProfile);

		// List<String> matches = new ArrayList<>();
		HashMap<String, List<String>> matchesByUser = new HashMap<>();

		for (String hashGit : hashsGit) {

			for (String hashGithub : hashsGithub) {

				String[] hash = hashGithub.split(",");

				if (hashGit.equals(hash[1])) {

					if (!matchesByUser.containsKey(hash[0])) {
						List<String> matches = new ArrayList<>();
						matchesByUser.put(hash[0], matches);
					}

					List<String> matches = matchesByUser.get(hash[0]);
					matches.add(hash[1]);
					matchesByUser.replace(hash[0], matches);
				}
			}

		}

		List<String> pulls = IO.readAnyFile(Util.getPullsFolder(project) + "pulls_merged_git.txt");
		HashSet<String> realPulls = new HashSet<>();
		List<String> realPulls2 = new ArrayList<>();

		for (String key : matchesByUser.keySet()) {
			List<String> matches = matchesByUser.get(key);
			String pathUser = LocalPaths.PATH + project + "/users/" + key + "/pulls/commits/";
			Commits.collectCommits(matches, url, pathUser);

			for (String pull : pulls) {

				for (String match : matches) {
					if (pull.contains(match)) {
						String[] p = pull.split(",");
						realPulls.add(p[0]);
					}
				}
			}

			IO.writeAnyFile(LocalPaths.PATH + project + "/users/" + key + "/pulls/commits_hashs_missing.txt", matches);

		}
		
		for(String p : realPulls){
			realPulls2.add(p);
		}
		
		IO.writeAnyFile(Util.getPullsFolder(project) + "heuristic1.txt", realPulls2);
	}

}
