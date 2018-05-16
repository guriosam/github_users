package generators;

import java.io.File;
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

import endpoints.CommitsAPI;
import endpoints.PullsAPI;
import objects.CommitInfo;
import objects.UserComment;
import objects.UserCommit;
import objects.UserInfo;
import objects.UserPoint;
import objects.UserPullRequest;
import utils.Config;
import utils.Git;
import utils.IO;
import utils.JSONManager;
import utils.LocalPaths;
import utils.URLs;
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

		List<UserPullRequest> userPull = PullRequests.getPullRequests(project);

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
	public static void generatePullsIds(String project) {

		String f = "";
		try {

			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String path = Util.getIndividualPullsFolder(project);
			List<String> files = IO.filesOnFolder(path);
			List<String> pulls = new ArrayList<>();
			List<String> pullsInfo = new ArrayList<>();
			List<String> hashs = new ArrayList<>();

			for (String file : files) {

				if (!file.contains("json")) {
					continue;
				}

				f = file;
				String fileData = new String(Files.readAllBytes(Paths.get(path + file)));
				LinkedTreeMap pull = gson.fromJson(fileData, LinkedTreeMap.class);

				String login = "";
				String hash = "";

				if (pull.containsKey("user")) {

					LinkedTreeMap user = (LinkedTreeMap) pull.get("user");
					if (user == null) {
						continue;
					}

					login = (String) user.get("login");

				} else {
					continue;
				}

				boolean merged = false;

				if (pull.containsKey("merged")) {

					merged = (boolean) pull.get("merged");

					if (pull.containsKey("merge_commit_sha")) {
						hash = (String) pull.get("merge_commit_sha");
						hashs.add(login + "," + hash + "," + merged);
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

				String number = "";

				if (pull.containsKey("number")) {
					number = pull.get("number") + "";
					number = number.replace(".", "");
					number = number.substring(0, number.length() - 1);

					pulls.add(login + "," + number + "," + merged);
				}

				pullsInfo.add(login + "," + number + "," + hash + "," + merged);

			}

			IO.writeAnyFile(Util.getPullsFolder(project) + "pulls_ids.txt", pulls);
			IO.writeAnyFile(Util.getPullsFolder(project) + "pulls_hashs.txt", hashs);
			IO.writeAnyFile(Util.getPullsFolder(project) + "pulls_info.txt", pullsInfo);

		} catch (Exception e) {
			System.out.println(f);
			e.printStackTrace();
		}

	}

	public static void collectPullCommitsByUser(String project, String url) {

		List<String> names = Util.getUserList(project);
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
		List<String> folders = IO.filesOnFolder(path + "general/");

		HashMap<String, Integer> commitsOnPulls = new HashMap<>();

		List<String> hashs = new ArrayList<>();

		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		for (String folder : folders) {

			List<String> files = IO.filesOnFolder(path + "general/" + folder + "/");

			for (String file : files) {

				if (!file.contains("json")) {
					continue;
				}

				try {

					String fileData = new String(
							Files.readAllBytes(Paths.get(path + "general/" + folder + "/" + file)));

					List<LinkedTreeMap> commits = gson.fromJson(fileData, List.class);

					if (!commitsOnPulls.containsKey(folder)) {
						commitsOnPulls.put(folder, commits.size());
					} else {
						commitsOnPulls.replace(folder, commits.size());
					}

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

		List<String> folders2 = IO.filesOnFolder(path + "general_not_merged/");
		for (String folder : folders2) {

			List<String> files = IO.filesOnFolder(path + "general_not_merged/" + folder + "/");

			for (String file : files) {

				if (!file.contains("json")) {
					continue;
				}

				try {

					String fileData = new String(
							Files.readAllBytes(Paths.get(path + "general_not_merged/" + folder + "/" + file)));

					List<LinkedTreeMap> commits = gson.fromJson(fileData, List.class);

					if (!commitsOnPulls.containsKey(folder)) {
						commitsOnPulls.put(folder, commits.size());
					} else {
						commitsOnPulls.replace(folder, commits.size());
					}

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

		IO.writeAnyFile(path + "pulls_hashs.txt", hashs);
		String output = gson.toJson(commitsOnPulls);
		IO.writeAnyString(path + "pulls_commits_count.json", output);

	}

	public static void collectCommitsOnPullRequests(String project, String url) {

		String path = Util.getPullsFolder(project);
		String pathCommits = Util.getPullCommitsPath(project);
		String subPath = "general/";

		File f = new File(pathCommits);

		System.out.println(pathCommits);

		if (!f.exists()) {
			f.mkdirs();
		}

		List<String> pullIds = IO.readAnyFile(path + "pulls_ids.txt");

		File f1 = new File(pathCommits + subPath);
		if (!f1.exists()) {
			f1.mkdir();
		}

		for (String line : pullIds) {

			String[] l = line.split(",");
			String id = l[1];

			File f2 = new File(pathCommits + subPath + id + "/");
			if (!f2.exists()) {
				f2.mkdirs();
			}

			for (int i = 1; i < 100; i++) {

				String command = LocalPaths.CURL + " -i -u " + Config.USERNAME + ":" + Config.PASSWORD
						+ " \"https://api.github.com/repos/" + url + "/pulls/" + id + "/commits?page=" + i + "\"";

				boolean empty = JSONManager.getJSON(pathCommits + subPath + id + "/" + i + ".json", command, false);

				if (empty) {
					break;
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
			PullsAPI.downloadIndividualPulls(project, url);
		} catch (Exception e) {
			e.printStackTrace();// TODO: handle exception
		}

	}

	public static void getIdsFromPerilI(String project, String url) {
		// Git.cloneProject(url);
		// Git.generateHashs(project);
		List<String> hashs = IO.readAnyFile(LocalPaths.PATH_GIT + project + "/hashs.txt");
		List<String> pullsHashs = IO.readAnyFile(Util.getPullsFolder(project) + "pulls_info.txt");
		List<String> perilI = new ArrayList<>();
		List<String> perilIid = new ArrayList<>();

		for (String pullHashLine : pullsHashs) {
			String[] pullHashL = pullHashLine.split(",");
			String pullHash = pullHashL[2];
			for (String h : hashs) {
				if (pullHash.equals(h)) {
					perilI.add(h);
					perilIid.add(pullHashL[1]);
				}
			}
		}

		IO.writeAnyFile(Util.getPullsFolder(project) + "h1_hashs.txt", perilI);
		IO.writeAnyFile(Util.getPullsFolder(project) + "h1_ids.txt", perilIid);

	}

	public static void getIdsFromPerilII(String project) {
		List<String> pullsHashs = IO.readAnyFile(Util.getPullsFolder(project) + "pulls_info.txt");
		List<String> h2Ids = IO.readAnyFile(Util.getPullsFolder(project) + "pull_requests_h2.txt");
		List<String> perilII = new ArrayList<>();

		for (String pullHashLine : pullsHashs) {
			String[] pullHashL = pullHashLine.split(",");
			String pullId = pullHashL[1];
			for (String h : h2Ids) {
				if (pullId.equals(h)) {
					perilII.add(pullHashL[2]);
				}
			}
		}

		IO.writeAnyFile(Util.getPullsFolder(project) + "h2_hashs.txt", perilII);
	}

	public static HashMap<String, List<String>> readComments(String project) {

		try {

			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String path = Util.getCommentsPullsFolder(project);
			List<String> folders = IO.filesOnFolder(path);

			HashMap<String, List<String>> userCount = new HashMap<>();

			for (String folder : folders) {

				String subPath = path + folder + "/";
				Util.checkDirectory(subPath);
				List<String> files = IO.filesOnFolder(subPath);

				for (String file : files) {

					String fileData = new String(Files.readAllBytes(Paths.get(subPath + file)));

					try {

						List<LinkedTreeMap> comments = new ArrayList<>();

						try {
							comments = gson.fromJson(fileData, List.class);
						} catch (Exception e) {
							System.out.println(subPath + file);
							continue;
						}

						for (LinkedTreeMap<?, ?> comment : comments) {

							String login = "";

							if (comment != null && comment.containsKey("user")) {
								LinkedTreeMap user = (LinkedTreeMap) comment.get("user");
								if (user != null && user.containsKey("login")) {
									login = (String) user.get("login");
								} else {
									continue;
								}

								if (!userCount.containsKey(login)) {
									userCount.put(login, new ArrayList<String>());
								}
								String created_at = (String) comment.get("created_at");

								userCount.get(login).add(created_at);

							}

						}
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println(subPath + file);
						// TODO: handle exception
					}

				}

			}

			return userCount;

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return new HashMap<>();

	}

	public static void downloadPullsCommits(String project) {
		// TODO Auto-generated method stub
		String path = Util.getPullCommitsPath(project);

		List<String> pullsInfo = IO.readAnyFile(path + "pulls_hashs.txt");

		List<String> hashs = new ArrayList<>();

		for (String pullInfo : pullsInfo) {
			String[] line = pullInfo.split(",");

			String hash = line[1];

			if (!hash.equals("null") && !hash.equals("")) {
				hashs.add(hash);
			}

		}

		CommitsAPI.downloadIndividualCommitsByHash(hashs, URLs.getUrl(project),
				Util.getPullIndividualCommitsPath(project));

	}

	public static List<UserPullRequest> getPullRequests(String project) {

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String path = LocalPaths.PATH + project + "/pull_requests.json";
		List<UserPullRequest> userPull = new ArrayList<>();

		List<String> heuristic1 = IO.readAnyFile(Util.getPullsFolder(project) + "h1_ids.txt");
		List<String> heuristic2 = IO.readAnyFile(Util.getPullsFolder(project) + "pull_requests_h2.txt");

		try {
			String fileData = new String(Files.readAllBytes(Paths.get(path)));
			List<LinkedTreeMap> pulls = gson.fromJson(fileData, List.class);

			for (LinkedTreeMap pull : pulls) {

				boolean m = false;
				UserPullRequest upr = new UserPullRequest();
				String id = "";

				if (pull.containsKey("id")) {
					String number = (String) pull.get("id");
					upr.setId(number);
					id = number;

					if (heuristic1.contains(id)) {
						m = true;
					}
					if (heuristic2.contains(id)) {
						m = true;
					}

				}
				if (pull.containsKey("state")) {
					upr.setState((String) pull.get("state"));

				}
				String name = "";
				if (pull.containsKey("user")) {
					String number = (String) pull.get("user");
					upr.setUser(number);
					name = number;
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

				if (pull.containsKey("created_at")) {
					String created_date = (String) pull.get("created_at");
					upr.setCreated_at(created_date);
				}
				if (pull.containsKey("closed_at")) {
					String closed_date = (String) pull.get("closed_at");
					upr.setClosed_at(closed_date);
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

				if (pull.containsKey("created_at")) {
					upr.setCreated_at((String) pull.get("created_at"));

				}

				if (pull.containsKey("closed_at")) {
					upr.setClosed_at((String) pull.get("closed_at"));

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

	public static void analyzePullCommits(String project) {
		// TODO Auto-generated method stub
		try{
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String fileData = new String(Files.readAllBytes(Paths.get(Util.getPullCommitsPath(project) + "pulls_commits_count.json")));
		LinkedTreeMap<String, Double> pullsCount = gson.fromJson(fileData, LinkedTreeMap.class);
		
		List<Double> counts = new ArrayList<>();
		double total = 0.0; 
		for(String key : pullsCount.keySet()){
			double value = pullsCount.get(key);
			counts.add(value);
			total += value;
		}
		
		double median = Util.calculateMedianDouble(counts);
		double mean = (double) total / (double) counts.size();
		
		System.out.println("Mean: " + mean + " Median: " + median);
		//IO.writeAnyString(Util.getPullCommitsPath(project) + "pulls_commit_size.txt",);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

}
