package generators;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import endpoints.CommitsAPI;
import objects.CommitInfo;
import objects.UserCommit;
import objects.UserInfo;
import objects.UserPoint;
import objects.UserPullRequest;
import utils.Config;
import utils.IO;
import utils.JSONManager;
import utils.LocalPaths;
import utils.Util;

public class PullRequests {

	public static void joinPullsToOutput(String project) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String jsonToJoin = LocalPaths.PATH + project + "/users_" + project + "_with_pull_heuristics_merged_new.json";
		String jsonPulls = LocalPaths.PATH + project + "/users_" + project
				+ "_only_with_pull_heuristics_merged_new.json";

		try {
			String fileData1 = new String(Files.readAllBytes(Paths.get(jsonToJoin)));
			String fileData2 = new String(Files.readAllBytes(Paths.get(jsonPulls)));

			List<LinkedTreeMap> commits = gson.fromJson(fileData1, List.class);
			List<LinkedTreeMap> pulls = gson.fromJson(fileData2, List.class);

			for (int i = 0; i < commits.size(); i++) {
				LinkedTreeMap c = commits.get(i);
				String hashCommit = (String) c.get("hash");
				for (LinkedTreeMap p : pulls) {
					String hashPull = (String) p.get("hash");
					if (hashCommit.equals(hashPull)) {
						System.out.println(commits.get(i).get("login"));
						System.out.println(c.get("pullRequestsMerged"));
						System.out.println(p.get("pullRequestsMerged"));
						c.put("pullRequestsMerged", p.get("pullRequestsMerged"));
						c.put("percentPullRequestsMerged", p.get("percentPullRequestsMerged"));
						c.put("numberOpenPullRequests", p.get("numberOpenPullRequests"));
						c.put("numberClosedPullRequests", p.get("numberClosedPullRequests"));
						c.put("numberRequestedReviewer", p.get("numberRequestedReviewer"));
						commits.set(i, c);
					}
				}
			}

			String output = gson.toJson(commits);

			IO.writeAnyString(jsonToJoin, output);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void analysePulls(String project, List<UserPoint> userPoints) {

		List<UserPullRequest> userPull = Issues.getPullRequests(project);

		boolean approach = true;
		boolean heuristics = true;
		boolean withPulls = true;
		List<UserInfo> jsonUsers = new ArrayList<>();

		for (UserPoint userPoint : userPoints) {

			String user = userPoint.getName();
			user = user.replace("\"", "");

			if (user.equals("NA")) {
				continue;
			}

			System.out.println(user);

			for (CommitInfo cm : userPoint.getCommitInfo()) {

				UserInfo userInfo = new UserInfo();
				String hash = cm.getHash();

				String authorDate = "";
				authorDate = cm.getDate();
				authorDate = authorDate.replace("\"", "");

				userInfo.setHash(hash);

				double countOpened = 0.0;
				double countMerged = 0.0;
				double openMerged = 0.0;

				for (UserPullRequest upr : userPull) {

					boolean m = false;
					if (approach) {
						if (upr.isMerged() && upr.getClosed_at() != null) {
							if (!Util.checkPastDate(upr.getClosed_at(), authorDate, "-")) {
								continue;
							}
						} else {

							if (!Util.checkPastDate(upr.getCreated_at(), authorDate, "-")) {
								continue;
							}
						}
					}

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

				if (countOpened != 0.0) {
					openMerged = countMerged / countOpened;
				}

				userInfo.setPercentPullRequestsMerged(openMerged);

				userInfo.setPullRequestsMerged(countMerged);

				if (!jsonUsers.contains(userInfo)) {
					jsonUsers.add(userInfo);
				}

			}

		}

		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		String pathOut = LocalPaths.PATH + project + "/users_" + project + "_only_with";

		String output = gson.toJson(jsonUsers);

		if (withPulls) {
			pathOut += "_pull";

			if (heuristics) {
				pathOut += "_heuristics_merged";
			} else {
				pathOut += "_merged";
			}

			if (approach) {
				pathOut += "_new.json";
			} else {
				pathOut += "_old.json";
			}

		} else {
			pathOut += "out_pull";

			if (approach) {
				pathOut += "_new.json";
			} else {
				pathOut += "_old.json";
			}
		}

		IO.writeAnyString(pathOut, output);

	}

	@SuppressWarnings("rawtypes")
	public static void generatePullsIds(String project, boolean merged) {

		try {

			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String path = Util.getIndividualPullsFolder(project);
			List<String> files = IO.filesOnFolder(path);
			List<String> pulls = new ArrayList<>();
			List<String> hashs = new ArrayList<>();
			List<String> names = Util.getBuggyUsers(project);

			for (String file : files) {

				if (!file.contains("json")) {
					continue;
				}

				String fileData = new String(Files.readAllBytes(Paths.get(path + file)));
				LinkedTreeMap pull = gson.fromJson(fileData, LinkedTreeMap.class);

				UserPullRequest upr = new UserPullRequest();

				if (pull.containsKey("merged")) {
					upr.setMerged((boolean) pull.get("merged"));
					if (upr.isMerged() != merged) {
						continue;
					}

					if (pull.containsKey("merge_commit_sha")) {
						String hash = (String) pull.get("merge_commit_sha");
						hashs.add(hash);
					}

				}

				if (pull.containsKey("head")) {
					LinkedTreeMap head = (LinkedTreeMap) pull.get("head");

					if (head.containsKey("ref")) {
						String ref = (String) head.get("ref");

						if (ref.contains("gh-pages")) {
							continue;
						}
					}

				}

				if (pull.containsKey("user")) {

					LinkedTreeMap user = (LinkedTreeMap) pull.get("user");
					if (user == null) {
						continue;
					}

					String login = (String) user.get("login");

					boolean buggyUser = false;
					for (String name : names) {
						if (login.equals(name)) {
							buggyUser = true;
							break;
						}
					}

					if (!buggyUser) {
						continue;
					}

				} else {
					continue;
				}

				if (pull.containsKey("number")) {
					String number = pull.get("number") + "";
					number = number.replace(".", "");
					number = number.substring(0, number.length() - 1);
					upr.setId(number);

					pulls.add(number);
				}

			}

			if (merged) {
				IO.writeAnyFile(Util.getPullsFolder(project) + "merged_pulls_ids.txt", pulls);
				IO.writeAnyFile(Util.getPullsFolder(project) + "merged_pulls_hashs.txt", hashs);
			} else {
				IO.writeAnyFile(Util.getPullsFolder(project) + "users_pulls_not_merged.txt", pulls);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void collectPullCommitsByUser(String project, String url) {

		List<String> names = Util.getBuggyUsers(project);
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		int count = 0;

		for (String line : names) {

			if (line.contains("username")) {
				continue;
			}

			String[] l = line.split(",");

			String name = l[0];
			name = name.replace("\"", "");

			String pathFolders = LocalPaths.PATH + project + "/pulls/commits/general/";

			List<String> pathsFiles = IO.filesOnFolder(pathFolders);
			String outputPath = LocalPaths.PATH + project + "/users/" + name + "/pulls/";

			File f = new File(outputPath);
			if (!f.exists()) {
				f.mkdirs();
			}

			System.out.println(name);
			System.out.println("Collecting Commits Batchs for Pull Requests");
			List<UserCommit> userCommits = new ArrayList<>();
			for (String files : pathsFiles) {
				String path = pathFolders + files + "/";
				userCommits.addAll(Commits.mineCommits(path, outputPath, name));
			}

			String output = gson.toJson(userCommits);
			String pathOutput = outputPath + "commits.json";

			if (userCommits.size() > 0) {
				IO.writeAnyString(pathOutput, output);
			}

			System.out.println("Collecting Individual Commits for Pull Requests");

			HashSet<String> hashs = new HashSet<>();
			for (UserCommit uc : userCommits) {
				hashs.add(uc.getSha());
			}

			List<String> h = new ArrayList<>();

			for (String hash : hashs) {
				h.add(hash);
			}

			count += h.size();

			IO.writeAnyFile(outputPath + "commits_hashs.txt", h);

			String finalPath = outputPath + "commits/";

			File f2 = new File(finalPath);
			if (!f2.exists()) {
				f2.mkdirs();
			}

			CommitsAPI.downloadIndividualCommitsByHash(h, url, finalPath);

		}

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void collectPullCommitsHashs(String project) {

		String path = Util.getPullCommitsPath(project);
		List<String> folders = IO.filesOnFolder(path + "general_not_merged/");

		HashSet<String> pullsMerged = new HashSet<>();

		List<String> hashs = new ArrayList<>();

		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		for (String folder : folders) {

			List<String> files = IO.filesOnFolder(path + "general_not_merged/" + folder + "/");

			for (String file : files) {

				if (!file.contains("json")) {
					continue;
				}

				try {

					String fileData = new String(
							Files.readAllBytes(Paths.get(path + "general_not_merged/" + folder + "/" + file)));

					List<LinkedTreeMap> commits = gson.fromJson(fileData, List.class);

					for (LinkedTreeMap c : commits) {

						if (c.containsKey("sha")) {
							String sha = (String) c.get("sha");
							if (sha != null) {

								if (c.containsKey("author")) {

									LinkedTreeMap author = (LinkedTreeMap) c.get("author");

									if (author != null) {

										String login = (String) author.get("login");

										if (login != null) {
											hashs.add(login + "," + sha);
											pullsMerged.add(folder + "," + sha);

										}
									}
								}
							}
						}

					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}

		List<String> pullsMergedList = new ArrayList<>();

		for (String pullMerged : pullsMerged) {
			pullsMergedList.add(pullMerged);
		}

		Util.sortList(pullsMergedList);

		List<String> heuristic2 = IO.readAnyFile(LocalPaths.PATH + project + "/pulls/" + "pull_requests_h2.txt");

		// Só existe em H1

		List<String> distinct1 = new ArrayList<>();

		// Só existe em H2
		List<String> distinct2 = new ArrayList<>();

		Util.sortList(pullsMergedList);

		// Getting distinct
		for (String h2 : heuristic2) {
			boolean b = true;
			for (String mpr : pullsMergedList) {
				if (mpr.equals(h2)) {
					b = false;
					break;
				}
			}
			if (b) {
				distinct2.add(h2);
			}
		}

		int count = 0;
		for (String mpr : pullsMergedList) {
			boolean b = true;
			for (String prH2 : heuristic2) {
				if (prH2.equals(mpr)) {
					b = false;
					break;
				}
			}
			if (b) {
				distinct1.add(mpr);
			}
		}

		Util.sortList(distinct1);
		List<String> h1 = IO.readAnyFile(LocalPaths.PATH + project + "/pulls/" + "merged_pulls_ids.txt");

		System.out.println("Heuristic 1: " + distinct1.size());
		System.out.println("Heuristic 2: " + distinct2.size()); //

		IO.writeAnyFile(LocalPaths.PATH + project + "/pulls/" + "pulls_distinct.txt", distinct2);

		IO.writeAnyFile(path + "hashs_commits_not_merged.txt", hashs);
		IO.writeAnyFile(LocalPaths.PATH + project + "/pulls/" + "pulls_merged_git.txt", pullsMergedList);

	}

	public static void collectCommitsOnPullRequestsFromList(String project, String url, List<String> pullIds) {

		String pathCommits = LocalPaths.PATH + project + "/pulls/commits/";
		String subPath = "heuristic2/";

		File f = new File(pathCommits);

		if (!f.exists()) {
			f.mkdirs();
		}

		for (String id : pullIds) {

			File f1 = new File(pathCommits + subPath + id + "/");
			if (!f1.exists()) {
				f1.mkdirs();
			}

			for (int i = 1; i < 100; i++) {

				String command = LocalPaths.CURL + " -i -u " + Config.USERNAME + ":" + Config.PASSWORD
						+ " \"https://api.github.com/repos/" + url + "/pulls/" + id + "/commits?page=" + i + "\"";

				boolean empty = JSONManager.getJSON(pathCommits + subPath + id + "/" + i + ".json", command);

				if (empty) {
					break;
				}

			}

		}

	}

	public static void collectCommitsOnPullRequests(String project, String url, boolean merged) {

		String path = Util.getPullsFolder(project);
		String pathCommits = Util.getPullCommitsPath(project);
		String subPath = "";

		if (merged) {
			subPath = "general/";
		} else {
			subPath = "general_not_merged/";
		}

		File f = new File(pathCommits);

		System.out.println(pathCommits);

		if (!f.exists()) {
			f.mkdirs();
		}

		List<String> pullIds = new ArrayList<>();
		if (merged) {
			pullIds = IO.readAnyFile(path + "merged_pulls_ids.txt");
		} else {
			pullIds = IO.readAnyFile(path + "users_pulls_not_merged.txt");
			pullIds.addAll(IO.readAnyFile(path + "pull_requests_h2.txt"));
		}

		File f1 = new File(pathCommits + subPath);
		if (!f1.exists()) {
			f1.mkdir();
		}

		for (String id : pullIds) {

			File f2 = new File(pathCommits + subPath + id + "/");
			if (!f2.exists()) {
				f2.mkdirs();
			}

			for (int i = 1; i < 100; i++) {

				String command = LocalPaths.CURL + " -i -u " + Config.USERNAME + ":" + Config.PASSWORD
						+ " \"https://api.github.com/repos/" + url + "/pulls/" + id + "/commits?page=" + i + "\"";

				boolean empty = JSONManager.getJSON(pathCommits + subPath + id + "/" + i + ".json", command);

				if (empty) {
					break;
				}

			}

		}

	}

	public static void downloadIndividualPulls(String project, String url, String pathIds) {

		String pathIndividual = Util.getIndividualPullsFolder(project);
		List<String> ids = IO.readAnyFile(pathIds);
		List<String> failedIds = new ArrayList<>();
		List<String> sucessfullIds = new ArrayList<>();

		for (String id : ids) {

			String command = LocalPaths.CURL + " -i -u " + Config.USERNAME + ":" + Config.PASSWORD
					+ " \"https://api.github.com/repos/" + url + "/pulls/" + id + "\"";

			boolean f = JSONManager.getJSON(pathIndividual + id + ".json", command);

			if (f) {
				failedIds.add(id);
			}

		}

		if (pathIds.contains("distinct")) {

			for (String id : ids) {
				boolean b = true;
				for (String fId : failedIds) {
					if (id.equals(fId)) {
						b = false;
					}
				}
				if (b) {
					sucessfullIds.add(id);
				}
			}
		}

	}

	@SuppressWarnings("unchecked")
	public static void generateIndividualPullsCalls(String project, String url) {

		System.out.println("Generating Individual Pulls Calls");

		try {

			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			List<String> ids = new ArrayList<>();
			String path = Util.getPullsFolder(project);
			List<String> files = IO.filesOnFolder(Util.getGeneralPullsFolder(project));

			for (String file : files) {

				if (!file.contains("json")) {
					continue;
				}

				String fileData = new String(Files.readAllBytes(Paths.get(Util.getGeneralPullsFolder(project) + file)));
				List<LinkedTreeMap> pulls = gson.fromJson(fileData, List.class);

				for (LinkedTreeMap<?, ?> pull : pulls) {

					String id = pull.get("number") + "";

					id = id.replace(".", "");
					id = id.substring(0, id.length() - 1);

					ids.add(id);

				}

			}

			IO.writeAnyFile(path + "pulls_ids.txt", ids);
			PullRequests.downloadIndividualPulls(project, url, path + "pulls_ids.txt");
		} catch (Exception e) {
			e.printStackTrace();// TODO: handle exception
		}

	}

	public static void collectH1Hashs(String project) {
		String pathAllHashs = LocalPaths.PATH_GIT + project + "/hashs.txt";
		String pathHashsPulls = LocalPaths.PATH + project + "/pulls/commits/hashs_commits_not_merged.txt";

		List<String> allHashs = IO.readAnyFile(pathAllHashs);
		List<String> notMerged = IO.readAnyFile(pathHashsPulls);
		List<String> should = new ArrayList<>();

		for (String not : notMerged) {
			String[] line = not.split(",");
			String notHash = line[1];

			for (String hash : allHashs) {
				if (hash.contains(notHash)) {
					should.add(hash);
				}
			}

		}
		
		IO.writeAnyFile(LocalPaths.PATH + project + "/pulls/h1_hashs.txt", should);

	}
}
