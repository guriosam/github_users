package generators;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import objects.UserRepository;
import utils.Config;
import utils.IO;
import utils.JsoupManager;
import utils.LocalPaths;

public class Repositories {

	public static void generateUserCommitsInRepositoryCalls(String project) {
		List<String> users = IO.readAnyFile(LocalPaths.PATH + project + "/contributors_names.txt");
		List<String> calls = new ArrayList<>();

		for (String user : users) {

			String path = LocalPaths.PATH + project + "/" + user;

			String call = "curl -u " + Config.USERNAME + ":" + Config.PASSWORD
					+ " https://api.github.com/repos/prestodb/presto/commits?author=" + user + " > " + path + "/"
					+ "commits.json";

			calls.add(call);

		}

		IO.writeAnyFile(LocalPaths.PATH + project + "/collect_commits_by_user.sh", calls);
	}

	public static void generateNumberOfFilesChangedCalls(String project) {
		List<String> users = IO.readAnyFile(LocalPaths.PATH + project + "/contributors_names.txt");
		List<String> calls = new ArrayList<>();

		for (String user : users) {

			String path = LocalPaths.PATH + project + "/" + user;

			String call = "curl -u " + Config.USERNAME + ":" + Config.PASSWORD
					+ " https://api.github.com/repos/square/okhttp/commits?author=" + user + " > " + path + "/"
					+ "commits.json";

			calls.add(call);

		}

		IO.writeAnyFile(LocalPaths.PATH + project + "/collect_commits_by_user.sh", calls);
	}

	public static void readStatsJson(String project) {
		String path = LocalPaths.PATH + project + "/contributors_stats.json";

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String fileData;
		List<String> logins = new ArrayList<String>();
		try {
			fileData = new String(Files.readAllBytes(Paths.get(path)));
			List<LinkedTreeMap> contributorsStats = gson.fromJson(fileData, List.class);

			for (LinkedTreeMap<?, ?> c : contributorsStats) {
				List<LinkedTreeMap> weeks = (List<LinkedTreeMap>) c.get("weeks");
				if (c.containsKey("author")) {
					LinkedTreeMap author = (LinkedTreeMap) c.get("author");
					// double total = (double) c.get("total");

					try {
						if (author != null) {
							String login = (String) author.get("login");
							logins.add(login);

						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				}

			}

			IO.writeAnyFile(LocalPaths.PATH + project + "/contributors_names.txt", logins);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	public static List<String> readCommitsJson(String project, String username) {
		String path = LocalPaths.PATH + project + "/" + username + "/commits.json";

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String fileData = "";
		List<String> hashs = new ArrayList<String>();
		try {
			fileData = new String(Files.readAllBytes(Paths.get(path)));
			List<LinkedTreeMap> commits = gson.fromJson(fileData, List.class);

			for (LinkedTreeMap<?, ?> c : commits) {

				LinkedTreeMap author = (LinkedTreeMap) c.get("author");
				if (author.get("login").equals(username)) {
					String hash = (String) c.get("sha");
					hashs.add(hash);
				}

			}

			// IO.writeAnyFile(LocalPaths.PATH + project + "/" + username +
			// "/hashs.txt", hashs);
			return hashs;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new ArrayList<>();

	}

	public static void generateUserRepositoriesNames(String project, String username) {
		String path = LocalPaths.PATH + project + "/" + username + "/repos.json";

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String fileData = "";
		final List<String> repos = new ArrayList<>();
		try {
			fileData = new String(Files.readAllBytes(Paths.get(path)));
			List<LinkedTreeMap> repositories = gson.fromJson(fileData, List.class);

			Thread t = null;

			for (final LinkedTreeMap<?, ?> c : repositories) {

				String s = (String) c.get("name");
				String url = (String) c.get("html_url");

				repos.add(s + ";" + url);
			}

			IO.writeAnyFile(LocalPaths.PATH + project + "/" + username + "/repos_names.txt", repos);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static List<UserRepository> readRepositories(String project, final String username) {
		String path = LocalPaths.PATH + project + "/" + username + "/repos.json";

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String fileData = "";
		final List<UserRepository> repos = new ArrayList<UserRepository>();
		try {
			fileData = new String(Files.readAllBytes(Paths.get(path)));
			List<LinkedTreeMap> repositories = gson.fromJson(fileData, List.class);

			Thread t = null;

			for (final LinkedTreeMap<?, ?> c : repositories) {

				UserRepository userRepo = new UserRepository();
				userRepo.setRepositoryName((String) c.get("name"));
				userRepo.setCreatedAt((String) c.get("created_at"));
				userRepo.setLanguage((String) c.get("language"));
				userRepo.setUrl((String) c.get("html_url"));
				userRepo.setNumberOfFiles((Double) c.get("size") + "");
				userRepo.setOwner(username);

				//String numberCommits = JsoupManager.getNumberCommits(userRepo.getUrl());
				//String numberContributors = JsoupManager.getNumberContributors(userRepo.getUrl());

				//userRepo.setNumberOfContributors(numberContributors);
				//userRepo.setNumberOfCommits(numberCommits);

				repos.add(userRepo);
			}

			// IO.writeAnyFile(LocalPaths.PATH + project + "/" + username +
			// "/hashs.txt", hashs);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return repos;

	}
}
