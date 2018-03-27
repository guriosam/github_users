package generators;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import objects.CommitInfo;
import objects.UserPoint;
import objects.UserProfile;
import utils.Config;
import utils.IO;
import utils.LocalPaths;
import utils.Util;

public class Users {

	public static void generateUsersProfileCalls(String project) {
		List<String> users = IO.readAnyFile(LocalPaths.PATH + project + "/contributors_names.txt");
		List<String> calls = new ArrayList<>();

		for (String user : users) {

			String path = LocalPaths.PATH + project + "/" + user;

			String call = "mkdir " + path + "\n" + LocalPaths.CURL + " -i -u " + Config.USERNAME + ":" + Config.PASSWORD
					+ " https://api.github.com/users/" + user + " > " + path + "/" + "profile.json";

			calls.add(call);

		}

		IO.writeAnyFile(LocalPaths.PATH + project + "/collect_profiles.sh", calls);

	}

	public static void readUsersProfiles(String project) {

		List<String> usernames = IO.readAnyFile(LocalPaths.PATH + project + "/contributors_names.txt");
		List<String> stats = IO.readAnyFile(LocalPaths.PATH + project + "/stats.csv");

		for (String username : usernames) {

			List<String> jsons = new ArrayList<>();

			String path = LocalPaths.PATH + project + "/" + username + "/profile.json";

			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String fileData = "";
			try {
				fileData = new String(Files.readAllBytes(Paths.get(path)));
				LinkedTreeMap c = gson.fromJson(fileData, LinkedTreeMap.class);

				String login = (String) c.get("login");
				String name = (String) c.get("name");
				String location = (String) c.get("location");
				String email = (String) c.get("email");
				Double publicRepos = (Double) c.get("public_repos");
				Double followers = (Double) c.get("followers");
				Double following = (Double) c.get("following");
				String createdAt = (String) c.get("created_at");
				String profileUrl = (String) c.get("html_url");

				String stat = "";
				for (String s : stats) {
					if (s.contains(login)) {
						stat = s;
					}
				}

				String additions = "";
				String deletions = "";
				String commits = "";

				if (!stat.equals("")) {
					String[] info = stat.split(";");
					commits = info[1];
					additions = info[2];
					deletions = info[3];

				}

				UserProfile user = new UserProfile();
				user.setUsername(login);
				user.setName(name);
				user.setEmail(email);
				user.setLocation(location);
				user.setNumberRepositories(publicRepos + "");
				user.setFollowers(followers + "");
				user.setFollowing(following + "");
				user.setCreatedAt(createdAt);
				user.setProfileUrl(profileUrl);
				user.setNumberCommits(commits);
				// user.setPublicRepositories(Repositories.readRepositories(project,
				// username));
				user.setAdditions(additions);
				user.setDeletions(deletions);

				String json = gson.toJson(user);
				jsons.add(json);

				IO.writeAnyFile(LocalPaths.PATH + project + "/" + username + "/" + username + ".json", jsons);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public static void generateUsersReposCalls(String project) {
		List<String> users = IO.readAnyFile(LocalPaths.PATH + project + "/contributors_names.txt");
		List<String> calls = new ArrayList<>();

		for (String user : users) {

			String path = LocalPaths.PATH + project + "/" + user;

			String call = LocalPaths.CURL + " -i -u " + Config.USERNAME + ":" + Config.PASSWORD
					+ " https://api.github.com/users/" + user + "/repos > " + path + "/" + "repos.json";

			calls.add(call);

		}

		IO.writeAnyFile(LocalPaths.PATH + project + "/collect_user_repos.sh", calls);

	}

	public static void generateRepoInfoCalls(String project, String url) {
		List<String> users = IO.readAnyFile(LocalPaths.PATH + project + "/contributors_names.txt");
		List<String> calls = new ArrayList<>();

		for (String user : users) {
			Repositories.generateUserRepositoriesNames(project, user);

			calls.add("mkdir " + LocalPaths.PATH + project + "/" + user + "/repo/");

			List<String> repos = IO.readAnyFile(LocalPaths.PATH + project + "/" + user + "/repos_names.txt");

			for (String repo : repos) {

				String[] rep = repo.split(";");

				String u = LocalPaths.CURL + " -i \"" + rep[1]
						+ "\" -H \"Host: github.com\" -H \"User-Agent: Mozilla/5.0 (Windows NT 10.0; WOW64; rv:55.0) Gecko/20100101 Firefox/55.0\" -H \"Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\" -H \"DNT: 1\" -H \"Connection: keep-alive\" -H \"Upgrade-Insecure-Requests: 1\" > "
						+ LocalPaths.PATH + project + "/" + user + "/repo/" + rep[0] + ".html";

				calls.add(u);

			}

		}

		IO.writeAnyFile(LocalPaths.PATH + project + "/collect_user_repos_info.sh", calls);

	}

	public static List<UserPoint> organizePoints(String project) {
		// TODO Auto-generated method stub

		List<String> users = Util.getBuggyUserInfo(project);
		HashMap<String, List<CommitInfo>> userPoint = new HashMap<>();
		List<UserPoint> userPoints = new ArrayList<>();

		HashSet<String> hashs = new HashSet<>();

		for (String line : users) {

			String[] l = line.split(",");

			String hash = l[0];
			hash = hash.replace("\"", "");

			if (hashs.contains(hash)) {
				continue;
			}

			hashs.add(hash);

			String user = l[1];
			user = user.replace("\"", "");

			if (user.equals("NA")) {
				continue;
			}

			String authorDate = l[4];
			authorDate = authorDate.replace("\"", "");

			// UserPoint p = new UserPoint();
			// p.setName(user);

			CommitInfo commit = new CommitInfo();
			commit.setHash(hash);
			commit.setDate(authorDate);
			commit.setBuggy(true);

			if (!userPoint.containsKey(user)) {
				// p.setCommitInfo(new ArrayList<CommitInfo>());
				userPoint.put(user, new ArrayList<CommitInfo>());
			}

			userPoint.get(user).add(commit);

		}

		List<String> cleanUsers = Util.getUserInfo(project);

		for (String line : cleanUsers) {
			String[] l = line.split(",");

			String hash = l[1];
			String user = l[0];
			String date = l[2];

			if (hashs.contains(hash)) {
				continue;
			}

			hashs.add(hash);

			CommitInfo commit = new CommitInfo();
			commit.setHash(hash);
			commit.setDate(date);
			commit.setBuggy(false);

			if (!userPoint.containsKey(user)) {
				// p.setCommitInfo(new ArrayList<CommitInfo>());
				userPoint.put(user, new ArrayList<CommitInfo>());
			}

			userPoint.get(user).add(commit);

		}

		for (String k : userPoint.keySet()) {

			List<CommitInfo> info = userPoint.get(k);

			HashSet<String> cHashs = new HashSet<>();

			for (int i = 0; i < info.size() - 1; i++) {
				for (int j = i + 1; j < info.size(); j++) {
					if (!Util.checkPastDate(info.get(i).getDate(), info.get(j).getDate(), "-")) {
						CommitInfo aux = info.get(i);
						info.set(i, info.get(j));
						info.set(j, aux);
					}
				}

			}

			UserPoint point = new UserPoint();
			point.setName(k);
			point.setCommitInfo(info);

			userPoints.add(point);

			// System.out.println(k);
			// for (CommitInfo c : info) {
			// System.out.println(c.getDate() + ": " + c.getHash());
			// }

		}

		return userPoints;

	}

}
