package generators;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import objects.UserIssue;
import objects.UserPullRequest;
import utils.Config;
import utils.IO;
import utils.JSONManager;
import utils.LocalPaths;

public class Issues {

	public static void filterIssuesByUser(String project) {

		List<String> names = IO.readAnyFile(LocalPaths.PATH + project + "/buggy_users.csv");
		List<UserIssue> issues = readIssues(project);
		HashMap<String, List<UserIssue>> userClosedByIssues = new HashMap<>();
		HashMap<String, List<UserIssue>> userOpenByIssues = new HashMap<>();

		for (String line : names) {

			if (line.contains("username")) {
				continue;
			}

			String[] l = line.split(",");

			String name = l[0];
			name = name.replace("\"", "");

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

	public static List<UserIssue> readIssues(String project) {

		try {

			String pullPath = LocalPaths.PATH + project + "/pulls/";
			File f = new File(pullPath);
			if (!f.exists()) {
				f.mkdirs();
			}
			String path = LocalPaths.PATH + project + "/issues/";
			List<String> ids = IO.readAnyFile(path + "ids.txt");

			List<UserIssue> userIssues = new ArrayList<>();
			Gson gson = new GsonBuilder().setPrettyPrinting().create();

			for (String id : ids) {

				String fileData = new String(Files.readAllBytes(Paths.get(path + id + ".json")));
				LinkedTreeMap issues = gson.fromJson(fileData, LinkedTreeMap.class);

				UserIssue ui = new UserIssue();

				ui.setNumber(id);
				// System.out.println("id: " + id);

				if (issues.containsKey("state")) {
					String state = (String) issues.get("state");
					ui.setState(state);
					// System.out.println(state);
				}
				if (issues.containsKey("created_at")) {
					String createdAt = (String) issues.get("created_at");
					ui.setCreatedAt(createdAt);
					// System.out.println(createdAt);
				}
				if (issues.containsKey("closed_at")) {
					String closedAt = (String) issues.get("closed_at");
					ui.setClosedAt(closedAt);
					// System.out.println(closedAt);
				}
				if (issues.containsKey("closed_by")) {
					LinkedTreeMap user = (LinkedTreeMap) issues.get("closed_by");
					if (user != null && user.containsKey("login")) {
						String closedBy = (String) user.get("login");
						ui.setClosedBy(closedBy);
						// System.out.println(closedBy);
					} else {
						ui.setClosedBy("null");
					}

				}
				if (issues.containsKey("user")) {
					LinkedTreeMap user = (LinkedTreeMap) issues.get("user");
					String login = (String) user.get("login");
					ui.setCreator(login);
					// System.out.println(login);
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
				
				System.out.println(file);

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

		try {
			String fileData = new String(Files.readAllBytes(Paths.get(path)));
			List<LinkedTreeMap> pulls = gson.fromJson(fileData, List.class);

			for (LinkedTreeMap pull : pulls) {

				UserPullRequest upr = new UserPullRequest();

				if (pull.containsKey("state")) {
					upr.setState((String) pull.get("state"));

				}
				if (pull.containsKey("merged")) {
					upr.setMerged((boolean) pull.get("merged"));

				}
				if (pull.containsKey("merged_by")) {
					if (pull != null && pull.containsKey("merged_by")) {
						upr.setMerged_by((String) pull.get("merged_by"));
					}
				}
				if (pull.containsKey("number")) {
					String number = (String) pull.get("number");
					upr.setId(number);
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

			return userPull;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ArrayList<UserPullRequest>();

	}

	public static void readPullRequests(String project) {

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
						if (rev.size() > 0) {
							System.out.println(file);
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

		String path = LocalPaths.PATH + project + "/pulls/general/";
		List<String> commands = new ArrayList<>();

		File f = new File(path);
		if (!f.exists()) {
			f.mkdirs();
		}

		for (int i = 1; i < 2000; i++) {

			String command = LocalPaths.CURL + " -i -u " + Config.USERNAME + ":" + Config.PASSWORD + " \"https://api.github.com/repos/"
					+ url + "/pulls" + "?state=all&page=" + i + "\"";
			
			boolean empty = JSONManager.getJSON(path + i + ".json", command);

			if (empty) {
				break;
			}
		}

	}

	public static void generateIndividualPullsCalls(String project, String url) {

		try {

			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			List<String> ids = new ArrayList<>();
			String path = LocalPaths.PATH + project + "/pulls/";
			List<String> files = IO.filesOnFolder(path + "general/");

			for (String file : files) {

				if (!file.contains("json")) {
					continue;
				}

				String fileData = new String(Files.readAllBytes(Paths.get(path + "general/" + file)));
				List<LinkedTreeMap> pulls = gson.fromJson(fileData, List.class);

				for (LinkedTreeMap<?, ?> pull : pulls) {

					String id = pull.get("number") + "";

					id = id.replace(".", "");
					id = id.substring(0, id.length() - 1);

					ids.add(id);

				}

			}

			IO.writeAnyFile(path + "ids.txt", ids);

			String pathIndividual = LocalPaths.PATH + project + "/pulls/individual/";

			File f = new File(pathIndividual);
			if (!f.exists()) {
				f.mkdirs();
			}

			for (String id : ids) {

				String command = LocalPaths.CURL + " -i -u " + Config.USERNAME + ":" + Config.PASSWORD + " \"https://api.github.com/repos/"
						+ url + "/pulls/" + id + "\"";

				JSONManager.getJSON(pathIndividual + id + ".json", command);

			}

		} catch (Exception e) {
			e.printStackTrace();// TODO: handle exception
		}

	}

	public static void generateCommentsCalls(String project, String url) {

		String path = LocalPaths.PATH + project + "/issues/comments/";
		List<String> commands = new ArrayList<>();

		File f = new File(path);
		if (!f.exists()) {
			f.mkdirs();
		}

		for (int i = 1; i < 5000; i++) {

			String command = LocalPaths.CURL + " -i -u " + Config.USERNAME + ":" + Config.PASSWORD + " \"https://api.github.com/repos/"
					+ url + "/issues/comments?page=" + i + "\"";

			boolean empty = JSONManager.getJSON(path + "comments_" + i + ".json", command);

			if (empty) {
				break;
			}

		}

	}

	public static void generateIndividualIssuesCall(String project, String url) {

		// repos/:owner/:repo/issues/:number

		String path = LocalPaths.PATH + project + "/issues/";

		List<String> ids = IO.readAnyFile(path + "ids.txt");

		List<String> commands = new ArrayList<>();

		for (String id : ids) {

			String idPath = path + id + "/";
			File f = new File(path);
			if (!f.exists()) {
				f.mkdirs();
			}

			String command = LocalPaths.CURL + " -i -u " + Config.USERNAME + ":" + Config.PASSWORD + " \"https://api.github.com/repos/"
					+ url + "/issues/" + id + "\"";

			JSONManager.getJSON(path + id + ".json", command);

		}

	}

	public static void generateRepositoryIssuesCall(String project, String url) {

		// repos/:owner/:repo/issues
		String path = LocalPaths.PATH + project + "/issues/general/";
		File f = new File(path);
		if (!f.exists()) {
			f.mkdirs();
		}

		List<String> commands = new ArrayList<>();

		for (int i = 1; i < 1000; i++) {
			String command = LocalPaths.CURL + " -i -u " + Config.USERNAME + ":" + Config.PASSWORD + " \"https://api.github.com/repos/"
					+ url + "/issues?state=all&page=" + i + "\"";

			boolean empty = JSONManager.getJSON(path + i + ".json", command);

			if (empty) {
				break;
			}

		}

	}

	public static void generateIssuesIds(String project) {

		try {

			List<String> ids = new ArrayList<>();
			String path = LocalPaths.PATH + project + "/issues/";
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			List<String> files = IO.filesOnFolder(path + "general/");

			for (String file : files) {

				if (!file.contains("json") || file.contains("ids")) {
					continue;
				}

				String fileData = new String(Files.readAllBytes(Paths.get(path + "general/" + file)));
				List<LinkedTreeMap> issues = gson.fromJson(fileData, List.class);

				for (LinkedTreeMap<?, ?> c : issues) {

					String id = c.get("number") + "";
					id = id.replace(".", "");
					id = id.substring(0, id.length() - 1);
					ids.add(id);

				}

			}

			IO.writeAnyFile(path + "ids.txt", ids);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

}
