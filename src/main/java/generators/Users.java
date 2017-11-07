package generators;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import objects.UserProfile;
import utils.Config;
import utils.IO;
import utils.JSONManager;
import utils.LocalPaths;

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

			String call = LocalPaths.CURL + " -i -u " + Config.USERNAME + ":" + Config.PASSWORD + " https://api.github.com/users/" + user
					+ "/repos > " + path + "/" + "repos.json";

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

	public static void collectUsersCommits(String project, String url) {

		List<String> names = IO.readAnyFile(LocalPaths.PATH + project + "/buggy_users.csv");
		List<String> finalRun = new ArrayList<>();

		for (String line : names) {

			if (line.contains("username")) {
				continue;
			}

			String[] l = line.split(",");

			String name = l[0];
			name = name.replace("\"", "");
			
			System.out.println(name);

			String path = LocalPaths.PATH + project + "/users/" + name + "/";
			File f = new File(path);
			if (!f.exists()) {
				f.mkdir();
			}

			for (int j = 1; j < 1000; j++) {

				String command = LocalPaths.CURL + " -i -u " + Config.USERNAME + ":" + Config.PASSWORD + " \"https://api.github.com/repos/"
						+ url + "/commits?author=" + name + "&page=" + j + "\"";

				boolean empty = JSONManager.getJSON(path + j + ".json", command);
				
				if(empty){
					break;
				}
			}

		}

	}

}
