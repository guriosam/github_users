package generators;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.collections.ComparatorUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import objects.UserCommit;
import objects.UserInfo;
import objects.UserIssue;
import objects.UserPullRequest;
import utils.Config;
import utils.IO;
import utils.JSONManager;
import utils.LocalPaths;

public class Commits {

	public static List<UserCommit> readCommits(String project, String user) {

		try {

			List<UserCommit> userCommits = new ArrayList<>();
			String path = LocalPaths.PATH + project + "/users/" + user + "/";
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			List<String> files = IO.filesOnFolder(path);

			for (String file : files) {

				if (!file.contains("json") || file.contains("commits") || file.contains("issues")) {
					continue;
				}

				String fileData = new String(Files.readAllBytes(Paths.get(path + file)));
				List<LinkedTreeMap> commits = gson.fromJson(fileData, List.class);

				for (LinkedTreeMap<?, ?> c : commits) {

					UserCommit uc = new UserCommit();

					if (c.containsKey("commit")) {
						LinkedTreeMap<String, LinkedTreeMap> commit = (LinkedTreeMap) c.get("commit");

						if (commit.containsKey("author") && commit.containsKey("committer")) {
							LinkedTreeMap<String, String> author = (LinkedTreeMap) commit.get("author");
							LinkedTreeMap<String, String> committer = (LinkedTreeMap) commit.get("committer");

							LinkedTreeMap<String, String> au = (LinkedTreeMap) c.get("author");
							String login = (String) au.get("login");

							String name = author.get("name");
							String committe = committer.get("name");

							if (!name.equals(committe)) {
							
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

				String output = gson.toJson(userCommits);

				String outputPath = path + "commits.json";
				IO.writeAnyString(outputPath, output);

			}

			return userCommits;

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return new ArrayList<>();

	}

	public static void analyzeCommits(String project) {

		List<String> users = IO.readAnyFile(LocalPaths.PATH + project + "/buggy_users.csv");
		// HashMap<String, UserInfo> jsonUsers = new HashMap<>();
		List<UserInfo> jsonUsers = new ArrayList<>();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		List<UserPullRequest> userPull = Issues.getPullRequests(project);
		HashMap<String, Integer> userCount = Issues.readComments(project);
		List<UserIssue> userIssues = Issues.readIssues(project);

		for (String line : users) {

			if (line.contains("username")) {
				continue;
			}

			String[] l = line.split(",");

			String name = l[0];
			name = name.replace("\"", "");

			String user = name;

			String insertionPoints = l[1];

			List<UserCommit> userCommits = readCommits(project, user);

			if (userCommits.size() > 1) {

				String minimumDate = DateTime.now(DateTimeZone.UTC).toString();
				String maximumDate = userCommits.get(0).getDate();
				HashSet<String> dates = new HashSet();
				HashMap<Integer, Integer> weeks = new HashMap<>();
				List<Double> additions = new ArrayList<>();
				List<Double> deletions = new ArrayList<>();
				List<Integer> files = new ArrayList<>();

				for (UserCommit uc : userCommits) {
					DateTime dt = new DateTime(DateTimeZone.UTC);
					long date = dt.parse(uc.getDate()).getMillis();
					long minimum = dt.parse(minimumDate).getMillis();
					long maximum = dt.parse(maximumDate).getMillis();

					DateTime d = dt.parse(uc.getDate());
					dates.add(d.getDayOfMonth() + "/" + d.getMonthOfYear() + "/" + d.getYear());

					if (date < minimum) {
						minimumDate = uc.getDate();
					}
					if (date > maximum) {
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

						File f = new File(LocalPaths.PATH + project + "/users/" + uc.getAuthorLogin() + "/commits/"
								+ uc.getSha() + ".json");

						if (!f.exists()) {
							continue;
						}

						String fileData = new String(Files.readAllBytes(Paths.get(LocalPaths.PATH + project + "/"
								+ uc.getAuthorLogin() + "/commits/" + uc.getSha() + ".json")));

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

				additions.sort(ComparatorUtils.NATURAL_COMPARATOR);
				double adds = 0.0;
				for (Double add : additions) {
					adds += add;
				}

				deletions.sort(ComparatorUtils.NATURAL_COMPARATOR);
				double rems = 0.0;
				for (Double rem : deletions) {
					rems += rem;
				}

				files.sort(ComparatorUtils.NATURAL_COMPARATOR);
				double file = 0.0;
				for (int f : files) {
					file += f;
				}
				List<String> w = new ArrayList<>();
				for (Integer i : weeks.keySet()) {
					w.add(i + ":" + weeks.get(i) + "");
				}

				UserInfo userInfo = new UserInfo();
				userInfo.setCommits(userCommits.size());
				userInfo.setAdditions(adds);

				if (additions.size() > 0) {
					userInfo.setMeanAdditions(adds / additions.size());

					if (additions.size() % 2 == 0) {
						userInfo.setMedianAdditions(
								(additions.get(additions.size() / 2) + additions.get(additions.size() / 2 - 1)) / 2);
					} else {
						userInfo.setMedianAdditions(additions.get(additions.size() / 2));
					}

				} else {
					userInfo.setMeanAdditions(0.0);
					userInfo.setMedianAdditions(0.0);
				}

				userInfo.setDeletions(rems);

				if (deletions.size() > 0) {
					userInfo.setMeanDeletions(rems / deletions.size());
					if (deletions.size() % 2 == 0) {
						userInfo.setMedianDeletions(
								(deletions.get(deletions.size() / 2) + deletions.get(deletions.size() / 2 - 1)) / 2);
					} else {
						userInfo.setMedianDeletions(deletions.get(deletions.size() / 2));
					}
				} else {
					userInfo.setMeanDeletions(0.0);
					userInfo.setMedianDeletions(0.0);
				}

				userInfo.setModifiedFiles(file);

				if (files.size() > 0) {
					userInfo.setMeanModified(file / files.size());
					if (files.size() % 2 == 0) {
						userInfo.setMedianModified(
								(double) ((files.get(files.size() / 2) + files.get(files.size() / 2 - 1)) / 2));
					} else {
						userInfo.setMedianModified((double) files.get(files.size() / 2));
					}
				} else {
					userInfo.setMeanModified(0.0);
					userInfo.setMedianModified(0.0);
				}

				userInfo.setActiveDays(dates.size());
				userInfo.setTimeOnProject(
						Days.daysBetween(DateTime.parse(minimumDate), DateTime.parse(maximumDate)).getDays());
				userInfo.setWeeks(w);

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
							countMerged++;
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

				// System.out.println(user);

				if (!jsonUsers.contains(userInfo)) {
					jsonUsers.add(userInfo);
				}

			} else {

			}

		}

		String output = gson.toJson(jsonUsers);

		IO.writeAnyString(LocalPaths.PATH + project + "/users.json", output);

	}

	public static void createGetCommitsCalls(String project, String url) {

		List<String> users = IO.readAnyFile(LocalPaths.PATH + project + "/buggy_users.csv");

		for (String line : users) {

			if (line.contains("username")) {
				continue;
			}

			String[] l = line.split(",");

			String name = l[0];
			name = name.replace("\"", "");

			String user = name;
			System.out.println(user);

			List<UserCommit> userCommits = readCommits(project, user);
			List<String> hashs = new ArrayList<>();

			for (UserCommit uc : userCommits) {
				hashs.add(uc.getSha());
			}

			/// repos/:owner/:repo/commits/:sha
			String path = LocalPaths.PATH + project + "/users/" + user + "/commits/";
			File f = new File(path);
			if (!f.exists()) {
				f.mkdirs();
			}

			List<String> commands = new ArrayList<>();
			for (String hash : hashs) {
				String command = LocalPaths.CURL + " -i -u " + Config.USERNAME + ":" + Config.PASSWORD + " \"https://api.github.com/repos/"
						+ url + "/commits/" + hash + "\"";

				boolean empty = JSONManager.getJSON(path + hash + ".json", command);

				if (empty) {
					break;
				}

			}

		}

	}

}
