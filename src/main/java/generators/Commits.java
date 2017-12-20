package generators;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.ComparatorUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.JodaTimePermission;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.sun.deploy.uitoolkit.impl.fx.Utils;

import objects.UserCommit;
import objects.UserInfo;
import objects.UserIssue;
import objects.UserPullRequest;
import utils.Config;
import utils.DateIterator;
import utils.IO;
import utils.JSONManager;
import utils.LocalPaths;
import utils.Util;

public class Commits {

	@SuppressWarnings("unchecked")
	public static void analyzeCommits(String project) {

		List<String> users = IO.readAnyFile(LocalPaths.PATH + project + "/buggy_users.csv");
		List<UserInfo> jsonUsers = new ArrayList<>();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		List<UserPullRequest> userPull = Issues.getPullRequests(project);
		HashMap<String, Integer> userCount = Issues.readComments(project);
		List<UserIssue> userIssues = Issues.readIssues(project);
		boolean pull = false;
		double countTotalMerged = 0.0;

		List<String> pulls_merged = new ArrayList<>();

		for (String line : users) {

			if (line.contains("username")) {
				continue;
			}

			String[] l = line.split(",");

			String name = l[0];
			name = name.replace("\"", "");

			String user = name;

			String insertionPoints = l[1];

			if (insertionPoints.equals("0")) {
				continue;
			}

			List<UserCommit> userCommits = readAllCommitsOnFolder(
					LocalPaths.PATH + project + "/users/" + user + "/commits/",
					LocalPaths.PATH + project + "/users/" + user + "/commits/", user);

			File f1 = new File(LocalPaths.PATH + project + "/users/" + user + "/pulls/commits/");

			List<UserCommit> userPullCommits = new ArrayList<>();

			if (f1.exists()) {

				userPullCommits = readAllCommitsOnFolder(LocalPaths.PATH + project + "/users/" + user + "/pulls/",
						LocalPaths.PATH + project + "/users/" + user + "/pulls/commits/", user);
				//pull = true;
			}

			if (userCommits.size() > 0) {

				String minimumDate = userCommits.get(0).getDate();
				minimumDate = minimumDate.substring(0, minimumDate.indexOf("T"));
				String maximumDate = DateTime.now(DateTimeZone.UTC).toString();
				maximumDate = maximumDate.substring(0, maximumDate.indexOf("T"));
				HashSet<String> dates = new HashSet<>();

				HashMap<Integer, Integer> weeks = new HashMap<>();

				List<Double> additions = new ArrayList<>();

				List<Double> deletions = new ArrayList<>();

				List<Integer> files = new ArrayList<>();

				HashMap<String, Integer> commitsPerDay = new HashMap<>();

				readUserCommitInfo(project, gson, userCommits, minimumDate, maximumDate, dates, weeks, additions,
						deletions, files, commitsPerDay, false);

				readUserCommitInfo(project, gson, userPullCommits, minimumDate, maximumDate, dates, weeks, additions,
						deletions, files, commitsPerDay, true);

				List<Date> commitDates = Util.orderDates(commitsPerDay);

				List<Integer> orderedDates = Util.iterateDates(commitsPerDay, commitDates);

				double adds = Util.getSumDouble(additions);
				double rems = Util.getSumDouble(deletions);
				double file = (double) Util.getSumInt(files);

				List<String> w = new ArrayList<>();

				// for (Integer i : weeks.keySet()) {
				// w.add(i + ":" + weeks.get(i) + "");
				// }

				int userCommitsSize = userCommits.size() + userPullCommits.size();

				UserInfo userInfo = new UserInfo();
				userInfo.setCommits(userCommitsSize);
				userInfo.setCommitsPulls(userPullCommits.size());

				userInfo.setMeanCommits(minimumDate, maximumDate);
				userInfo.setMedianCommits(orderedDates);

				userInfo.setAdditions(adds);

				userInfo.setMeanAdditions(additions);
				userInfo.setMedianAdditions(additions);

				userInfo.setDeletions(rems);
				userInfo.setMeanDeletions(deletions);
				userInfo.setMedianDeletions(deletions);

				userInfo.setModifiedFiles(file);
				userInfo.setMeanModified(files);
				userInfo.setMedianModified(files);

				userInfo.setActiveDays(dates.size());

				List<String> datesToOrder = new ArrayList<>();

				for (String date : dates) {

					String[] d1 = date.split("/");

					Integer year1 = Integer.parseInt(d1[0]);

					Integer m1 = Integer.parseInt(d1[1]);

					Integer day1 = Integer.parseInt(d1[2]);

					datesToOrder.add(day1 + "-" + m1 + "-" + year1);
				}

				maximumDate = DateTime.now(DateTimeZone.UTC).toString();
				maximumDate = maximumDate.substring(0, maximumDate.indexOf("T"));

				userInfo.setTimeOnProject(datesToOrder, maximumDate);
				// userInfo.setWeeks(w);
				userInfo.setLogin(user);

				double countOpened = 0.0;
				double countMerged = 0.0;
				double openMerged = 0.0;

				for (UserPullRequest upr : userPull) {

					boolean m = false;

					if (upr.getUser() != null) {
						if (upr.getUser().equals(user)) {
							int o = userInfo.getNumberOpenPullRequests();
							o++;
							userInfo.setNumberOpenPullRequests(o);
							countOpened++;
							m = true;
						}

					}
					if (upr.getMerged_by() != null) {
						if (upr.getMerged_by().equals(user)) {
							int o = userInfo.getNumberClosedPullRequests();
							o++;
							userInfo.setNumberClosedPullRequests(o);

						}
					}

					if (upr.isMerged()) {
						if (m) {
							pulls_merged.add(upr.getId());
							countMerged++;
							countTotalMerged++;
						}
					}

					if (upr.getReviewers() != null) {
						for (String rev : upr.getReviewers()) {
							if (rev.equals(user)) {
								int o = userInfo.getNumberRequestedReviewer();
								o++;
								userInfo.setNumberRequestedReviewer(o);

							}
						}

					}
				}

				userInfo.setInsertionPoints(Integer.valueOf(insertionPoints));

				if (countOpened != 0.0) {
					openMerged = countMerged / countOpened;
				}

				userInfo.setPercentPullRequestsMerged(openMerged);

				if (userCount.containsKey(user)) {
					userInfo.setNumberComments(userCount.get(user));
				}

				for (UserIssue ui : userIssues) {

					if (ui.getClosedBy().equals(user)) {
						int o = userInfo.getNumberClosedIssues();
						o++;
						userInfo.setNumberClosedIssues(o);
					}

					if (ui.getCreator().equals(user)) {
						int o = userInfo.getNumberOpenIssues();
						o++;
						userInfo.setNumberOpenIssues(o);
					}

				}

				if (!jsonUsers.contains(userInfo)) {
					jsonUsers.add(userInfo);
				}

			}

		}

		try

		{

			String output = gson.toJson(jsonUsers);

			if (pull) {
				IO.writeAnyString(LocalPaths.PATH + project + "/users_" + project + "_with_pull.json", output);
				IO.writeAnyFile(LocalPaths.PATH + project + "/total_pulls_merged_" + project + ".json", pulls_merged);
			} else {
				IO.writeAnyString(LocalPaths.PATH + project + "/users_" + project + "_without_pull.json", output);
			}

			System.out.println("Total Merged: " + countTotalMerged);

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(jsonUsers);
			// TODO: handle exception
		}

	}

	// Read json with 30 commits
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List<UserCommit> mineCommits(String path, String pathOutput, String user) {

		List<UserCommit> userCommits = new ArrayList<>();

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		List<String> files = IO.filesOnFolder(path);

		for (String file : files) {

			if (!file.contains("json") || file.contains("commits") || file.contains("issues")) {
				continue;
			}

			try {

				String fileData = new String(Files.readAllBytes(Paths.get(path + file)));

				List<LinkedTreeMap> commits = gson.fromJson(fileData, List.class);

				for (LinkedTreeMap c : commits) {

					readSingleCommit(userCommits, c, user);

				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		String output = gson.toJson(userCommits);
		String outputPath = pathOutput + "commits.json";

		if (userCommits.size() > 0) {
			IO.writeAnyString(outputPath, output);
		}

		return userCommits;

	}

	// Read all commit files on folder
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<UserCommit> readAllCommitsOnFolder(String pathInput, String pathOutput, String user) {

		try {

			List<UserCommit> userCommits = new ArrayList<>();
			Gson gson = new GsonBuilder().setPrettyPrinting().create();

			List<String> files = new ArrayList<>();

			if (pathInput.equals(pathOutput)) {
				files = IO.filesOnFolder(pathInput);
			} else {
				files = IO.readAnyFile(pathInput + "commits_hashs.txt");

				//File f3 = new File(pathInput + "commits_hashs_missing.txt");
				//if (f3.exists()) {
					//files.addAll(IO.readAnyFile(pathInput + "commits_hashs_missing.txt"));
				//}
			}

			for (String file : files) {

				if (file.contains(".txt") || file.contains("commits") || file.contains("issues")) {
					continue;
				}

				String fileData = "";

				if (pathInput.equals(pathOutput)) {
					fileData = new String(Files.readAllBytes(Paths.get(pathInput + file)));
				} else {

					File f = new File(pathOutput + file + ".json");
					if (!f.exists()) {
						continue;
					}

					fileData = new String(Files.readAllBytes(Paths.get(pathOutput + file + ".json")));

				}

				try {
					LinkedTreeMap c = gson.fromJson(fileData, LinkedTreeMap.class);

					readSingleCommit(userCommits, c, user);

					String output = gson.toJson(userCommits);

					String outputPath = pathOutput + "commits.json";
					IO.writeAnyString(outputPath, output);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			return userCommits;

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return new ArrayList<>();

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void readSingleCommit(List<UserCommit> userCommits, LinkedTreeMap c, String user) {

		UserCommit uc = new UserCommit();

		if (c.containsKey("commit")) {
			LinkedTreeMap<String, LinkedTreeMap> commit = (LinkedTreeMap) c.get("commit");

			if (commit.containsKey("author") && commit.containsKey("committer")) {

				LinkedTreeMap<String, String> author = (LinkedTreeMap) commit.get("author");

				String name = author.get("name");

				String login = "";

				if (c.containsKey("author")) {
					LinkedTreeMap<String, String> au = (LinkedTreeMap) c.get("author");

					if (au != null) {
						login = (String) au.get("login");
					}

				}

				if (login.equals("")) {
					return;
				}

				if (!user.equals("")) {
					if (!user.equals(login)) {
						return;
					}
				}

				if (author.containsKey("email")) {
					String email = author.get("email");
					uc.setAuthorEmail(email);
				}
				if (author.containsKey("date")) {
					String date = author.get("date");
					uc.setDate(date);
				}
				if (c.containsKey("sha")) {
					String sha = (String) c.get("sha");
					uc.setSha(sha);
				}

				uc.setAuthorLogin(login);
				uc.setAuthorName(name);

				userCommits.add(uc);

			}

		}

	}

	@SuppressWarnings({ "unchecked", "rawtypes", "static-access" })
	private static void readUserCommitInfo(String project, Gson gson, List<UserCommit> userCommits, String minimumDate,
			String maximumDate, HashSet<String> dates, HashMap<Integer, Integer> weeks, List<Double> additions,
			List<Double> deletions, List<Integer> files, HashMap<String, Integer> commitsPerDay, boolean pull) {
		// TODO Auto-generated method stub

		for (UserCommit uc : userCommits) {

			DateTime dt = new DateTime(DateTimeZone.UTC);
			long date = dt.parse(uc.getDate()).getMillis();
			long minimum = dt.parse(minimumDate).getMillis();
			long maximum = dt.parse(maximumDate).getMillis();

			if (uc.getDate().contains("T")) {
				String day = uc.getDate().substring(0, uc.getDate().indexOf("T"));

				if (!commitsPerDay.containsKey(day)) {
					commitsPerDay.put(day, 0);
				}

				int aux = commitsPerDay.get(day);
				aux++;
				commitsPerDay.replace(day, aux);

			}

			DateTime d = dt.parse(uc.getDate());
			dates.add(d.getDayOfMonth() + "/" + d.getMonthOfYear() + "/" + d.getYear());

			if (date < minimum) {
				minimum = date;
				minimumDate = uc.getDate();
			}
			if (date > maximum) {
				maximum = date;
				maximumDate = uc.getDate();
			}

			int week = d.getWeekOfWeekyear();

			if (weeks.containsKey(week)) {
				int count = weeks.get(week);
				count++;
				weeks.replace(week, count);
			} else {
				weeks.put(week, 1);
			}

			try {

				String path = "";

				if (pull) {

					path = LocalPaths.PATH + project + "/users/" + uc.getAuthorLogin() + "/pulls/commits/" + uc.getSha()
							+ ".json";

				} else {

					path = LocalPaths.PATH + project + "/users/" + uc.getAuthorLogin() + "/commits/" + uc.getSha()
							+ ".json";
				}

				File f = new File(path);

				if (!f.exists()) {
					continue;
				}

				String fileData = new String(Files.readAllBytes(Paths.get(path)));

				LinkedTreeMap<String, ?> commit = gson.fromJson(fileData, LinkedTreeMap.class);

				if (commit == null) {
					continue;
				}

				if (commit.containsKey("stats")) {
					LinkedTreeMap<String, Double> stats = (LinkedTreeMap<String, Double>) commit.get("stats");

					if (stats.containsKey("additions")) {
						Double addition = stats.get("additions");
						additions.add(addition);
					}

					if (stats.containsKey("deletions")) {
						Double deletion = stats.get("deletions");
						deletions.add(deletion);
					}

					if (commit.containsKey("files")) {

						List file = (ArrayList) commit.get("files");

						files.add(file.size());
					}
				}

			} catch (Exception e) {
				e.printStackTrace();// TODO: handle exception
			}

		}

	}

	//
	public static void downloadUserCommitsFromMaster(String project, String url) {

		List<String> users = Util.getBuggyUsers(project);

		for (String user : users) {

			List<UserCommit> userCommits = mineCommits(Util.getUserPath(project, user), Util.getUserPath(project, user),
					user);
			List<String> hashs = new ArrayList<>();

			for (UserCommit uc : userCommits) {
				hashs.add(uc.getSha());
			}

			String path = Util.getUserCommitsPath(project, user);

			collectCommits(hashs, url, path);

		}

	}

	public static void collectCommits(List<String> hashs, String url, String path) {

		for (String hash : hashs) {
			String command = LocalPaths.CURL + " -i -u " + Config.USERNAME + ":" + Config.PASSWORD
					+ " \"https://api.github.com/repos/" + url + "/commits/" + hash + "\"";

			boolean empty = JSONManager.getJSON(path + hash + ".json", command);

			if (empty) {
				break;
			}

		}

	}

}
