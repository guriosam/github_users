package generators;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import endpoints.CommitsAPI;
import objects.UserComment;
import objects.UserCommit;
import objects.UserIssue;
import utils.IO;
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
			List<String> ids = IO.readAnyFile(path + "issues_ids.txt");

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

	public static HashMap<String, List<String>> readComments(String project) {

		try {

			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String path = LocalPaths.PATH + project + "/issues/comments/";
			List<String> files = IO.filesOnFolder(path);

			HashMap<String, List<String>> userCount = new HashMap<>();

			for (String file : files) {
				
				String fileData = new String(Files.readAllBytes(Paths.get(path + file)));
				List<LinkedTreeMap> comments = gson.fromJson(fileData, List.class);

				for (LinkedTreeMap<?, ?> comment : comments) {

					LinkedTreeMap user = (LinkedTreeMap) comment.get("user");
					String login = (String) user.get("login");

					if (!userCount.containsKey(login)) {
						userCount.put(login, new ArrayList<String>());
					}
					String created_at = (String) comment.get("created_at");

					userCount.get(login).add(created_at);

				}

			}

			return userCount;

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return new HashMap<>();

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
			CommitsAPI.downloadIndividualCommitsByHash(matches, url, pathUser);

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

		for (String p : realPulls) {
			realPulls2.add(p);
		}

		IO.writeAnyFile(Util.getPullsFolder(project) + "heuristic1.txt", realPulls2);
	}

}
