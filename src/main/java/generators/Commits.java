package generators;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import endpoints.CommitsAPI;
import objects.CommitInfo;
import objects.CommitSize;
import objects.NatureCommit;
import objects.UserComment;
import objects.UserCommit;
import objects.UserInfo;
import objects.UserIssue;
import objects.UserPoint;
import objects.UserPullRequest;
import utils.Config;
import utils.IO;
import utils.JSONManager;
import utils.LocalPaths;
import utils.Util;

public class Commits {

	@SuppressWarnings("unchecked")
	public static void analyzeCommits(String project, List<UserPoint> userPoints) {

		boolean approach = true;
		boolean heuristics = false;
		boolean withPulls = true;

		if (withPulls) {
			if (heuristics) {
				System.out.println("** Merged + H1 + H2 **");
			} else {
				System.out.println("** Merged **");
			}
		} else {
			System.out.println("** No Pulls **");
		}

		List<String> info = Util.getUserInfo(project);
		List<UserInfo> jsonUsers = new ArrayList<>();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		List<UserPullRequest> userPull = Issues.getPullRequests(project);

		List<UserIssue> userIssues = Issues.readIssues(project);
		boolean pull = false;
		double countTotalMerged = 0.0;

		HashSet<String> hashs = new HashSet<>();
		List<String> pulls_merged = new ArrayList<>();

		for (UserPoint userPoint : userPoints) {

			// String[] l = line.split(",");

			String user = userPoint.getName();
			user = user.replace("\"", "");

			if (user.equals("NA")) {
				continue;
			}

			System.out.println(user);

			String lastDate = "";

			if (approach) {
				lastDate = userPoint.getCommitInfo().get(userPoint.getCommitInfo().size() - 1).getDate();
			}

			List<UserCommit> userCommits = readAllCommitsOnFolder(project,
					LocalPaths.PATH + project + "/users/" + user + "/commits/",
					LocalPaths.PATH + project + "/users/" + user + "/commits/", user, lastDate, heuristics);

			for (int i = 0; i < userCommits.size() - 1; i++) {
				for (int j = i + 1; j < userCommits.size(); j++) {
					if (!Util.checkPastDate(userCommits.get(i).getDate(), userCommits.get(j).getDate(), "-")) {
						UserCommit uc = userCommits.get(i);
						userCommits.set(i, userCommits.get(j));
						userCommits.set(j, uc);
					}
				}
			}

			File f1 = new File(LocalPaths.PATH + project + "/users/" + user + "/pulls/commits/");

			List<UserCommit> userPullCommits = new ArrayList<>();

			if (withPulls) {
				if (f1.exists()) {

					userPullCommits = readAllCommitsOnFolder(project,
							LocalPaths.PATH + project + "/users/" + user + "/pulls/",
							LocalPaths.PATH + project + "/users/" + user + "/pulls/commits/", user, lastDate,
							heuristics);

					// System.out.println(userPullCommits.size());

					for (int i = 0; i < userPullCommits.size() - 1; i++) {
						for (int j = i + 1; j < userPullCommits.size(); j++) {
							if (!Util.checkPastDate(userPullCommits.get(i).getDate(), userPullCommits.get(j).getDate(),
									"-")) {
								UserCommit uc = userPullCommits.get(i);
								userPullCommits.set(i, userPullCommits.get(j));
								userPullCommits.set(j, uc);
							}
						}
					}
					pull = true;
				}
			}

			if (!heuristics) {
				userCommits = removeHashs(project, user, userCommits);
				userPullCommits = removeHashs(project, user, userPullCommits);
			}

			List<UserCommit> hashPull = new ArrayList<>();

			for (UserCommit uc : userCommits) {
				for (UserCommit ucp : userPullCommits) {
					if (uc.getSha().equals(ucp.getSha())) {
						hashPull.add(uc);
					}
				}
			}

			for (UserCommit hashP : hashPull) {
				userCommits.remove(hashP);
			}

			HashSet<String> dates = new HashSet<>();

			HashMap<Integer, Integer> weeks = new HashMap<>();

			List<Double> additions = new ArrayList<>();

			List<Double> deletions = new ArrayList<>();

			List<Integer> files = new ArrayList<>();

			HashMap<String, Integer> commitsPerDay = new HashMap<>();

			List<String> usedHashs = new ArrayList<>();
			List<String> usedHashsPulls = new ArrayList<>();

			// for (UserCommit uc : userCommits) {
			// hashs.add(uc.getSha());
			// }
			int insertion = 1;
			List<String> countTests = new ArrayList<>();
			for (CommitInfo cm : userPoint.getCommitInfo()) {

				if (!approach) {
					cm = userPoint.getCommitInfo().get(userPoint.getCommitInfo().size() - 1);
				}

				System.out.println("Insertion point " + insertion + "/" + userPoint.getCommitInfo().size());

				String hash = cm.getHash();

				String authorDate = "";
				if (approach) {
					authorDate = cm.getDate();
					authorDate = authorDate.replace("\"", "");
				}

				HashMap<String, UserComment> userCount = Issues.readComments(project, user, authorDate);

				if (userCommits.size() > 0) {

					String minimumDate = userCommits.get(0).getDate();
					minimumDate = minimumDate.substring(0, minimumDate.indexOf("T"));
					String maximumDate = DateTime.now(DateTimeZone.UTC).toString();
					if (approach) {
						maximumDate = authorDate;
					}

					CommitSize cs = new CommitSize();
					NatureCommit nc = new NatureCommit();

					maximumDate = maximumDate.substring(0, maximumDate.indexOf("T"));

					readUserCommitInfo(project, gson, userCommits, minimumDate, maximumDate, dates, weeks, additions,
							deletions, files, commitsPerDay, authorDate, usedHashs, usedHashsPulls, countTests, cs, nc,
							false);

					if (pull) {
						readUserCommitInfo(project, gson, userPullCommits, minimumDate, maximumDate, dates, weeks,
								additions, deletions, files, commitsPerDay, authorDate, usedHashs, usedHashsPulls,
								countTests, cs, nc, true);
					}

					List<Date> commitDates = Util.orderDates(commitsPerDay);

					List<Integer> orderedDates = Util.iterateDates(commitsPerDay, commitDates);

					double adds = Util.getSumDouble(additions);
					double rems = Util.getSumDouble(deletions);
					double file = (double) Util.getSumInt(files);

					List<String> w = new ArrayList<>();

					// for (Integer i : weeks.keySet()) {
					// w.add(i + ":" + weeks.get(i) + "");
					// }

					UserInfo userInfo = new UserInfo();
					int userCommitsSize = 0;

					if (approach) {
						userCommitsSize = usedHashs.size() + usedHashsPulls.size();
						userInfo.setCommitsPulls(usedHashsPulls.size());
						userInfo.setHash(hash);
					} else {
						userCommitsSize = userCommits.size() + userPullCommits.size();
						userInfo.setCommitsPulls(userPullCommits.size());
					}

					userInfo.setCommits(userCommitsSize);

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

					userInfo.setEmptySizeCount(cs.getEmptyCount());
					userInfo.setTinyCount(cs.getTinyCount());
					userInfo.setSmallCount(cs.getSmallCount());
					userInfo.setMediumCount(cs.getMediumCount());
					userInfo.setLargeCount(cs.getLargeCount());

					userInfo.setEmptyNatureCount(nc.getEmptyCount());
					userInfo.setForwardEngineeringCount(nc.getForwardEngineeringCount());
					userInfo.setCorrectiveEngineeringCount(nc.getCorrectiveEngineeringCount());
					userInfo.setReengineeringCount(nc.getReengineeringCount());
					userInfo.setUncategorizedCount(nc.getUncategorizedCount());
					userInfo.setManagementCount(nc.getManagementCount());

					if (countTests.size() > 0) {
						userInfo.setInsertionTests(true);
					} else {
						userInfo.setInsertionTests(false);
					}
					userInfo.setInsertionTestsCount(countTests.size());

					if (userCommitsSize > 0) {
						double percent = (double) insertion / userCommitsSize;
						userInfo.setBuggyPercent(percent);
					} else {
						userInfo.setBuggyPercent(0.0);
					}

					userInfo.setActiveDays(dates.size());

					List<String> datesToOrder = new ArrayList<>();

					for (String date : dates) {

						String[] d1 = date.split("/");

						Integer year1 = Integer.parseInt(d1[0]);

						Integer m1 = Integer.parseInt(d1[1]);

						Integer day1 = Integer.parseInt(d1[2]);

						datesToOrder.add(day1 + "-" + m1 + "-" + year1);
					}

					if (approach) {
						maximumDate = authorDate;// DateTime.now(DateTimeZone.UTC).toString();
					} else {
						maximumDate = DateTime.now(DateTimeZone.UTC).toString();
						userInfo.setInsertionPoints(userPoint.getCommitInfo().size());
					}
					maximumDate = maximumDate.substring(0, maximumDate.indexOf("T"));

					userInfo.setTimeOnProject(datesToOrder, maximumDate);
					// userInfo.setWeeks(w);
					userInfo.setLogin(user);

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

					// userInfo.setInsertionPoints(Integer.valueOf(insertionPoints));

					if (countOpened != 0.0) {
						openMerged = countMerged / countOpened;
					}

					userInfo.setPercentPullRequestsMerged(openMerged);

					userInfo.setPullRequestsMerged(countMerged);

					if (userCount.containsKey(user)) {
						if (approach) {
							if (Util.checkPastDate(userCount.get(user).getCreated_at(), authorDate, "-")) {
								userInfo.setNumberComments(userCount.get(user).getCount());
							} else {
								userInfo.setNumberComments(0);
							}
						} else {
							userInfo.setNumberComments(userCount.get(user).getCount());
						}

					}

					for (UserIssue ui : userIssues) {

						if (ui.getClosedBy().equals(user)) {

							if (approach) {
								if (!Util.checkPastDate(ui.getClosedAt(), authorDate, "-")) {
									continue;
								}
							}

							int o = userInfo.getNumberClosedIssues();
							o++;
							userInfo.setNumberClosedIssues(o);
						}

						if (ui.getCreator().equals(user)) {

							if (approach) {
								if (!Util.checkPastDate(ui.getCreatedAt(), authorDate, "-")) {
									continue;
								}
							}

							int o = userInfo.getNumberOpenIssues();
							o++;
							userInfo.setNumberOpenIssues(o);
						}

					}

					if (!jsonUsers.contains(userInfo)) {
						jsonUsers.add(userInfo);
					}

				}
				if (approach) {
					insertion++;

					hashs.addAll(usedHashs);
					hashs.addAll(usedHashsPulls);
				}

				if (!approach) {
					break;
				}

			}

		}

		try {

			String output = gson.toJson(jsonUsers);

			String pathOut = LocalPaths.PATH + project + "/users_" + project + "_with";

			if (pull) {
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

			if (pull) {
				IO.writeAnyFile(LocalPaths.PATH + project + "/total_pulls_merged_" + project + ".json", pulls_merged);
			}

			IO.writeAnyString(pathOut, output);
			if (approach) {
				List<String> htc = new ArrayList<>();
				htc.addAll(hashs);

				IO.writeAnyFile(LocalPaths.PATH + project + "/hashs_metrics.txt", htc);
			}

			// System.out.println("Total Merged: " + countTotalMerged);

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(jsonUsers);
			// TODO: handle exception
		}

		List<String> h2 = new ArrayList<>();
		for (String s : hashs) {
			h2.add(s);
		}

		IO.writeAnyFile(LocalPaths.PATH + project + "/commits_metrics.txt", h2);

	}

	private static List<UserCommit> removeHashs(String project, String user, List<UserCommit> userCommits) {

		List<String> missing = new ArrayList<>();
		File f4 = new File(Util.getPullsFolder(project) + "h1_hashs.txt");
		if (f4.exists()) {
			missing = IO.readAnyFile(Util.getPullsFolder(project) + "h1_hashs.txt");
		}
		List<String> missing2 = new ArrayList<>();
		File f5 = new File(Util.getPullsFolder(project) + "pulls_merged_git.txt");
		if (f5.exists()) {
			missing2 = IO.readAnyFile(Util.getUserPath(project, user) + "pulls_merged_git.txt");
		}

		List<UserCommit> userCommitsAux = new ArrayList<>();

		for (UserCommit uc : userCommits) {
			String h = uc.getSha();
			if (missing.contains(h)) {
				userCommitsAux.add(uc);
			}
		}

		for (UserCommit uc : userCommitsAux) {
			System.out.println(uc.getSha());
			userCommits.remove(uc);
		}

		return userCommits;
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
					readSingleCommit(userCommits, c, user, "", false);
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
	public static List<UserCommit> readAllCommitsOnFolder(String project, String pathInput, String pathOutput,
			String user, String authorDate, boolean heuristics) {

		try {

			List<UserCommit> userCommits = new ArrayList<>();
			Gson gson = new GsonBuilder().setPrettyPrinting().create();

			List<String> files = new ArrayList<>();
			List<String> userCommits2 = new ArrayList<>();

			boolean pulls = false;

			if (pathInput.equals(pathOutput)) {
				files = IO.filesOnFolder(pathInput);
				userCommits2 = IO.readAnyFile(Util.getUserPath(project, user) + "pulls/commits_hashs.txt");

			} else {
				files = IO.readAnyFile(pathInput + "commits_hashs.txt");

				pulls = true;

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

					readSingleCommit(userCommits, c, user, authorDate, heuristics);

					String output = gson.toJson(userCommits);

					String outputPath = pathOutput + "commits.json";
					IO.writeAnyString(outputPath, output);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			List<UserCommit> uc2 = new ArrayList<>();

			if (!pulls) {
				for (UserCommit u : userCommits) {
					boolean a = false;
					for (String h : userCommits2) {
						if (u.getSha().equals(h)) {
							a = true;
						}
					}

					if (a) {
						uc2.add(u);
					}
				}

				for (UserCommit uc : uc2) {
					userCommits.remove(uc);
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
	public static void readSingleCommit(List<UserCommit> userCommits, LinkedTreeMap c, String user, String authorDate,
			boolean heuristics) {

		UserCommit uc = new UserCommit();

		List<String> foward = new ArrayList<>();
		foward.add("implement");
		foward.add("add");
		foward.add("request");
		foward.add("new");
		foward.add("test");
		foward.add("start");
		foward.add("includ");
		foward.add("initial");
		foward.add("introduc");
		foward.add("creat");
		foward.add("increas");

		List<String> reeg = new ArrayList<>();

		reeg.add("optimiz");
		reeg.add("ajdust");
		reeg.add("update");
		reeg.add("delet");
		reeg.add("remov");
		reeg.add("chang");
		reeg.add("refactor");
		reeg.add("replac");
		reeg.add("modif");
		reeg.add("is now");
		reeg.add("are now");
		reeg.add("enhance");
		reeg.add("improv");
		reeg.add("design change");
		reeg.add("renam");
		reeg.add("eliminat");
		reeg.add("duplicat");
		reeg.add("restrutur");
		reeg.add("simplif");
		reeg.add("obsolete");
		reeg.add("rearrang");
		reeg.add("miss");
		reeg.add("enhanc");
		reeg.add("improv");

		List<String> correct = new ArrayList<>();

		correct.add("bug");
		correct.add("fix");
		correct.add("issue");
		correct.add("error");
		correct.add("correct");
		correct.add("proper");
		correct.add("deprecat");
		correct.add("broke");

		List<String> management = new ArrayList<>();

		management.add("clean");
		management.add("license");
		management.add("merge");
		management.add("release");
		management.add("structure");
		management.add("integrat");
		management.add("copyright");
		management.add("documentation");
		management.add("manual");
		management.add("javadoc");
		management.add("comment");
		management.add("migrat");
		management.add("repository");
		management.add("code review");
		management.add("polish");
		management.add("upgrade");
		management.add("style");
		management.add("formatting");
		management.add("organiz");
		management.add("TODO");

		if (c.containsKey("commit")) {
			LinkedTreeMap<String, ?> commit = (LinkedTreeMap) c.get("commit");

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

					if (!authorDate.equals("")) {
						if (!Util.checkPastDate(date, authorDate, "-")) {
							return;
						}
					}
				}
				if (c.containsKey("sha")) {
					String sha = (String) c.get("sha");
					uc.setSha(sha);
				}

				if (commit.containsKey("message")) {
					String patch = (String) commit.get("message");

					patch = patch.toLowerCase();

					String classification = "";

					if (patch.equals("")) {
						classification = "Empty";
					} else {

						boolean b = true;

						for (String mana : management) {
							if (patch.contains(mana)) {
								classification = "Management";
								b = false;
								break;
							}
						}

						if (b) {
							for (String re : reeg) {
								if (patch.contains(re)) {
									classification = "Reengineering";
									b = false;
									break;
								}
							}
						}
						if (b) {
							for (String co : correct) {
								if (patch.contains(co)) {
									classification = "Corrective Engineering";
									b = false;
									break;
								}
							}

						}
						if (b) {
							for (String fo : foward) {
								if (patch.contains(fo)) {
									classification = "Forward Engineering";
									b = false;
									break;
								}
							}
						}

						if (b) {
							classification = "Uncategorized";
						}

					}

					uc.setClassification(classification);

				}

				if (c.containsKey("files")) {
					List<LinkedTreeMap> f = (List) c.get("files");

					for (LinkedTreeMap<?, ?> files : f) {

						if (files.containsKey("filename")) {
							String filename = (String) files.get("filename");

							filename = filename.toLowerCase();

							if (filename.contains("test")) {
								uc.setTestInclusion(true);
							} else {
								uc.setTestInclusion(false);
							}
						}

					}
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
			List<Double> deletions, List<Integer> files, HashMap<String, Integer> commitsPerDay, String authorDate,
			List<String> usedHashs, List<String> usedHashsPulls, List<String> countTests, CommitSize cs,
			NatureCommit nc, boolean pull) {
		// TODO Auto-generated method stub

		int empty = 0;
		int tiny = 0;
		int small = 0;
		int medium = 0;
		int large = 0;

		int emptyCount = 0;
		int managementCount = 0;
		int reengineeringCount = 0;
		int correctiveEngineeringCount = 0;
		int forwardEngineeringCount = 0;
		int uncategorizedCount = 0;

		for (UserCommit uc : userCommits) {

			if (!authorDate.equals("")) {
				if (!Util.checkPastDate(uc.getDate(), authorDate, "-")) {
					continue;
				}

				if (usedHashs.contains(uc.getSha()) || usedHashsPulls.contains(uc.getSha())) {
					continue;
				} else {
					if (pull) {
						usedHashsPulls.add(uc.getSha());
					} else {
						usedHashs.add(uc.getSha());
					}
				}

			}

			// System.out.println("Hashs Size Normal: " + usedHashs.size());
			// System.out.println("Hashs Size Pull: " + usedHashsPulls.size());

			if (uc.hasTestInclusion()) {
				countTests.add("");
			}

			if (uc.getClassification() != null) {

				if (uc.getClassification().equals("Empty")) {
					emptyCount++;
				} else if (uc.getClassification().equals("Management")) {
					managementCount++;
				} else if (uc.getClassification().equals("Reengineering")) {
					reengineeringCount++;
				} else if (uc.getClassification().equals("Corrective Engineering")) {
					correctiveEngineeringCount++;
				} else if (uc.getClassification().equals("Forward Engineering")) {
					forwardEngineeringCount++;
				} else if (uc.getClassification().equals("Uncategorized")) {
					uncategorizedCount++;
				}
			}

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

						if (file.size() == 0) {
							empty++;
						} else if (file.size() > 0 && file.size() < 6) {
							tiny++;
						} else if (file.size() > 5 && file.size() < 26) {
							small++;
						} else if (file.size() > 25 && file.size() < 126) {
							medium++;
						} else if (file.size() > 125) {
							large++;
						}

						files.add(file.size());
					}

				}

			} catch (Exception e) {
				e.printStackTrace();// TODO: handle exception
			}

		}

		cs.setEmptyCount(cs.getEmptyCount() + empty);
		cs.setLargeCount(cs.getLargeCount() + large);
		cs.setTinyCount(cs.getTinyCount() + tiny);
		cs.setMediumCount(cs.getMediumCount() + medium);
		cs.setSmallCount(cs.getSmallCount() + small);

		nc.setEmptyCount(nc.getEmptyCount() + emptyCount);
		nc.setManagementCount(nc.getManagementCount() + managementCount);
		nc.setReengineeringCount(nc.getReengineeringCount() + reengineeringCount);
		nc.setCorrectiveEngineeringCount(nc.getCorrectiveEngineeringCount() + correctiveEngineeringCount);
		nc.setForwardEngineeringCount(nc.getForwardEngineeringCount() + forwardEngineeringCount);
		nc.setUncategorizedCount(nc.getUncategorizedCount() + uncategorizedCount);

	}

	public static void collectHashsFromUsers(String project) {

		String path = Util.getCommitsFolderPath(project);

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		List<String> files = IO.filesOnFolder(path);

		HashMap<String, List<String>> userHashs = new HashMap<>();
		List<String> allHashs = new ArrayList<>();

		for (String file : files) {
			try {
				String fileData = new String(Files.readAllBytes(Paths.get(path + file)));

				List<LinkedTreeMap> commits = gson.fromJson(fileData, List.class);

				for (LinkedTreeMap commit : commits) {

					String author = "";
					String hash = "";

					if (commit.containsKey("author")) {
						LinkedTreeMap a = (LinkedTreeMap) commit.get("author");

						if (a != null) {
							if (a.containsKey("login")) {
								author = (String) a.get("login");
							}
						} else {
							a = (LinkedTreeMap) commit.get("commit");
							if (a.containsKey("author")) {
								LinkedTreeMap b = (LinkedTreeMap) a.get("author");

								if (b.containsKey("name")) {
									author = (String) b.get("name");
								}
							}
						}
					}

					if (commit.containsKey("sha")) {
						hash = (String) commit.get("sha");
					}

					if (!userHashs.containsKey(author)) {
						userHashs.put(author, new ArrayList<>());
					}

					List<String> hashs = userHashs.get(author);
					hashs.add(hash);
					allHashs.add(hash);
					userHashs.replace(author, hashs);

				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		String output = gson.toJson(userHashs);

		IO.writeAnyString(Util.getCommitsPath(project) + "users_hashs.json", output);

		IO.writeAnyFile(Util.getCommitsPath(project) + "all_hashs.txt", allHashs);

	}

}
