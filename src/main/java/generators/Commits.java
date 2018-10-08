package generators;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import metrics.CommentsMetrics;
import metrics.CommitMetrics;
import metrics.IssuesMetrics;
import metrics.NatureMetrics;
import metrics.PullRequestsMetrics;
import metrics.SizeMetrics;
import objects.CommentDAO;
import objects.CommitDAO;
import objects.CommitInfo;
import objects.IssueDAO;
import objects.NatureCommit;
import objects.NatureDAO;
import objects.PullRequestDAO;
import objects.SizeDAO;
import objects.UserCommit;
import objects.UserInfo;
import objects.UserPoint;
import utils.IO;
import utils.LocalPaths;
import utils.Util;

public class Commits {

	@SuppressWarnings("unchecked")
	public static void analyzeCommits(String project, List<UserPoint> userPoints) {

		boolean approach = true;
		boolean heuristics = true;
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

		List<String> gitHashs = IO.readAnyFile(LocalPaths.PATH_GIT + project + "/hashs.txt");

		String firstHash = gitHashs.get(gitHashs.size() - 1);
		String firstDate = Util.getDate(project, gitHashs);

		List<UserInfo> jsonUsers = new ArrayList<>();

		HashSet<String> hashs = new HashSet<>();

		List<UserCommit> commits = readCommitsInfo(project);

		HashMap<String, NatureDAO> natures = NatureMetrics.readNatureAnalysis(project);

		HashMap<String, SizeDAO> sizes = SizeMetrics.readSizeAnalysis(project);

		HashMap<String, IssueDAO> issues = IssuesMetrics.readIssuesAnalysis(project);

		HashMap<String, CommentDAO> comments = CommentsMetrics.readCommentAnalysis(project);

		HashMap<String, PullRequestDAO> pullRequests = PullRequestsMetrics.readPullRequestAnalysis(project);

		List<String> ownership = IO.readAnyFile(Util.getMetricsPath(project) + "ownership.csv");

		HashMap<String, Double> ownerships = new HashMap<>();

		for (String owner : ownership) {
			String[] line = owner.split(",");

			String hash = line[0];
			String user = line[1];
			String date = line[2];
			String value = line[3];
			Double ownershipValue = Double.parseDouble(value);

			ownerships.put(hash + "_" + user, ownershipValue);
		}

		double percentRun = 0;
		double maxRun = 0;

		for (UserPoint userPoint : userPoints) {
			maxRun += userPoint.getCommitInfo().size();
		}

		DecimalFormat numberFormat = new DecimalFormat("0.00");

		for (UserPoint userPoint : userPoints) {

			// String[] l = line.split(",");

			String user = userPoint.getName();
			user = user.replace("\"", "");

			if (user.equals("NA") || user.equals("(no author)")) {
				continue;
			}

			List<UserCommit> userCommits = new ArrayList<>();
			HashSet<UserCommit> userCommitsSet = new HashSet<>();

			for (UserCommit u : commits) {
				if (u.getAuthorLogin().equals(user)) {
					userCommitsSet.add(u);
				} else if (u.getAuthorName().equals(user)) {
					userCommitsSet.add(u);
				}
			}

			for (UserCommit u : userCommitsSet) {
				userCommits.add(u);
			}

			userCommits = Util.orderCommits(userCommits);

			CommitMetrics commitMetrics = new CommitMetrics();

			HashMap<String, Integer> commitsPerDay = new HashMap<>();

			HashSet<String> usedHashs = new HashSet<>();

			int insertion = 0;
			List<Integer> countTests = new ArrayList<>();
			double commitsWithTests = 0;

			// Gson gson2 = new GsonBuilder().setPrettyPrinting().create();
			// String o = gson2.toJson(userCommits);

			// Util.checkDirectory(Util.getCommitsPath(project) +
			// "user_commits/");
			// IO.writeAnyString(Util.getCommitsPath(project) + "user_commits/"
			// + user + ".json", o);

			List<Double> userOwnerships = new ArrayList<>();

			for (CommitInfo cm : userPoint.getCommitInfo()) {

				percentRun++;

				System.out.println(numberFormat.format((percentRun / maxRun) * 100) + "%");

				// System.out.println("Insertion point " + insertion + "/" +
				// userPoint.getCommitInfo().size());

				String hash = cm.getHash();

				commitMetrics.setCurrentHash(hash);
				commitMetrics.setCurrentSubSystem(new ArrayList<String>());

				String authorDate = "";
				if (approach) {
					authorDate = cm.getDate();
					authorDate = authorDate.replace("\"", "");
				}

				if (userCommits.size() > 0) {

					double expRecentComment = CommentsMetrics.getRecentExperience(comments, user, authorDate);
					double expRecentPull = PullRequestsMetrics.getRecentExperience(pullRequests, user, authorDate);
					double expRecentIssue = IssuesMetrics.getRecentExperience(issues, user, authorDate);

					double recentActivityEXP = 0.0;
					double recentReviewEXP = 0.0;

					recentActivityEXP += expRecentComment;
					recentActivityEXP += expRecentIssue;
					recentActivityEXP += expRecentPull;

					recentReviewEXP += recentActivityEXP;

					String minimumDate = "";
					if (userCommits.size() > 0) {
						minimumDate = userCommits.get(0).getDate();
					}
					minimumDate = minimumDate.substring(0, minimumDate.indexOf("T"));
					String maximumDate = DateTime.now(DateTimeZone.UTC).toString();
					maximumDate = authorDate;

					maximumDate = maximumDate.substring(0, maximumDate.indexOf("T"));

					readUserCommitInfo(project, userCommits, minimumDate, maximumDate, commitMetrics, commitsPerDay,
							authorDate, usedHashs, countTests, user);

					List<Date> commitDates = Util.orderDates(commitsPerDay);

					List<Integer> orderedDates = Util.iterateDates(commitsPerDay, commitDates);

					String frequency = Util.calculateCommitFrequency(userCommits, hash);

					double rEXPCommit = Util.calculateREXPCommit(userPoint, hash);

					recentActivityEXP += rEXPCommit;

					UserInfo userInfo = new UserInfo();
					int userCommitsSize = usedHashs.size();

					userInfo.setREXP(rEXPCommit);

					if (userCommitsSize > 0) {
						double ucs = userCommitsSize;
						if (ucs == 0) {
							ucs = 1;
						}

						double percent = (double) ((double) insertion / (double) ucs);
						userInfo.setPreviousBuggyPercent(percent);
					} else {
						userInfo.setBuggyPercent(0.0);
					}

					userInfo.setPreviousInsertionPoints(insertion);

					if (cm.isBuggy()) {
						insertion++;
					}

					userInfo.setSEXP(commitMetrics.getSEXP());

					userInfo.setREXPActivity(recentActivityEXP);
					userInfo.setREXPReview(recentReviewEXP);

					if (ownerships.containsKey(hash + "_" + user)) {
						double d = ownerships.get(hash + "_" + user);
						userOwnerships.add(d);
						userInfo.setOwnership(d);
						userInfo.setMedianOwnership(userOwnerships);
					} else {
						System.out.println("Not contains ownership: " + user + ": " + hash);
					}

					int pullSize = 0;
					UserCommit u = null;
					for (UserCommit userCommit : userCommits) {
						if (userCommit.getSha().equals(hash)) {

							userInfo.setAdditions(userCommit.getAdditions());
							userInfo.setDeletions(userCommit.getDeletions());
							userInfo.setLinesChanged(userCommit.getLinesChanged());
							userInfo.setModifiedFiles(userCommit.getModifiedFiles());

						}
						if (usedHashs.contains(userCommit.getSha())) {
							if (userCommit.isPullCommit()) {
								pullSize++;
							}
						}
					}

					userInfo.setCommitsPulls(pullSize);
					userInfo.setHash(hash);
					userInfo.setCommitFrequency(frequency);

					userInfo.setBuggy(cm.isBuggy());

					userInfo.setCommits(userCommitsSize);

					userInfo.setMeanCommits(minimumDate, maximumDate);
					userInfo.setMedianCommits(orderedDates);

					commitMetrics.setMetrics(userInfo);

					if (natures.containsKey(hash)) {
						NatureDAO nature = natures.get(hash);
						NatureMetrics.setUserInfo(nature, userInfo);
					} else {
						System.out.println("Nature hash:" + hash + " not found!");
					}

					if (sizes.containsKey(hash)) {
						SizeDAO size = sizes.get(hash);
						SizeMetrics.setUserInfo(size, userInfo);
					} else {
						System.out.println("Size hash:" + hash + " not found!");
					}

					int activityEXP = 0;
					int codeReviewEXP = 0;

					activityEXP += userCommitsSize;

					if (comments.containsKey(hash)) {
						CommentDAO comment = comments.get(hash);

						userInfo.setNumberCommitComments(comment.getCommitCommentCount());
						userInfo.setNumberIssueComments(comment.getIssueCommentCount());
						userInfo.setNumberPullComments(comment.getPullCommentCount());

						activityEXP += comment.getCommitCommentCount();
						activityEXP += comment.getIssueCommentCount();
						activityEXP += comment.getPullCommentCount();

						codeReviewEXP += comment.getCommitCommentCount();
						codeReviewEXP += comment.getIssueCommentCount();
						codeReviewEXP += comment.getPullCommentCount();

					} else {
						System.out.println("Comment hash:" + hash + " not found!");
					}

					if (pullRequests.containsKey(hash)) {
						PullRequestDAO pullDAO = pullRequests.get(hash);

						userInfo.setNumberOpenPullRequests(pullDAO.getCountOpened());
						userInfo.setNumberClosedPullRequests(pullDAO.getCountClosed());
						userInfo.setPullRequestsMerged(pullDAO.getCountMerged());
						userInfo.setPercentPullRequestsMerged(pullDAO.getOpenMerged());
						userInfo.setNumberRequestedReviewer(pullDAO.getCountRequested());

						activityEXP += pullDAO.getCountOpened();
						activityEXP += pullDAO.getCountClosed();

						codeReviewEXP += pullDAO.getCountOpened();
						codeReviewEXP += pullDAO.getCountClosed();

					} else {
						System.out.println("Pull Request hash:" + hash + " not found!");
					}

					if (issues.containsKey(hash)) {
						IssueDAO issueDAO = issues.get(hash);

						userInfo.setNumberOpenIssues(issueDAO.getCountOpened());
						userInfo.setNumberClosedIssues(issueDAO.getCountClosed());

						activityEXP += issueDAO.getCountClosed();
						activityEXP += issueDAO.getCountOpened();

						codeReviewEXP += issueDAO.getCountClosed();
						codeReviewEXP += issueDAO.getCountOpened();

					} else {
						System.out.println("Issue hash:" + hash + " not found!");
					}

					if (countTests.size() > 0) {
						commitsWithTests++;
					}

					for (UserCommit uc : userCommits) {
						if (uc.getSha().equals(hash)) {
							if (uc.hasTestInclusion()) {
								userInfo.setInsertionTests(true);
							}
							break;
						}
					}

					userInfo.setExpActivity(activityEXP);
					userInfo.setExpReview(codeReviewEXP);

					userInfo.setInsertionTestsCount(countTests.size());

					if (userCommitsSize > 0) {
						double percent = (double) ((double) insertion / (double) userCommitsSize);
						userInfo.setBuggyPercent(percent);
					} else {
						userInfo.setBuggyPercent(0.0);
					}

					if (user.contains("cdberry")) {
						System.out.println(commitsWithTests);
						System.out.println(userCommitsSize);
					}
					userInfo.setTestPresence((double) (commitsWithTests / userCommitsSize));

					List<String> datesToOrder = new ArrayList<>();

					for (String date : commitMetrics.getDates()) {

						String[] d1 = date.split("/");

						Integer year1 = Integer.parseInt(d1[0]);

						Integer m1 = Integer.parseInt(d1[1]);

						Integer day1 = Integer.parseInt(d1[2]);

						datesToOrder.add(day1 + "-" + m1 + "-" + year1);
					}

					userInfo.setTimeOnProject(minimumDate, maximumDate);
					userInfo.setRepositoryTime(firstDate, maximumDate);
					// userInfo.setWeeks(w);
					userInfo.setLogin(user);

					userInfo.setInsertionPoints(insertion);

					if (!jsonUsers.contains(userInfo)) {
						jsonUsers.add(userInfo);
					}

				} else {
					System.out.println("Zero commits: " + userCommits.size());
					System.out.println(cm.getHash());
				}

				if (!approach) {
					break;
				}

			}

		}

		try {

			Gson gson = new GsonBuilder().setPrettyPrinting().create();

			String output = gson.toJson(jsonUsers);

			String pathOut = LocalPaths.PATH + project + "/users_" + project + "_with";

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
					readSingleCommit(userCommits, c, user, "");
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
			String user, String authorDate) {

		try {

			List<UserCommit> userCommits = new ArrayList<>();
			Gson gson = new GsonBuilder().setPrettyPrinting().create();

			List<String> files = new ArrayList<>();

			boolean pulls = false;

			if (pathInput.equals(pathOutput)) {
				files = IO.filesOnFolder(pathInput);
				// userCommits2 = IO.readAnyFile(Util.getUserPath(project, user)
				// + "pulls/commits_hashs.txt");

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

					userCommits = readSingleCommit(userCommits, c, user, authorDate);

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
	public static List<UserCommit> readSingleCommit(List<UserCommit> userCommits, LinkedTreeMap c, String user,
			String authorDate) {

		UserCommit uc = new UserCommit();

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
					login = name;
				}

				if (login.equals("")) {
					System.out.println(user + ": empty userCommits");
					return userCommits;
				}

				if (!user.equals("")) {
					if (!user.equals(login)) {
						System.out.println(user + ": different user-login - " + login);
						return userCommits;
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
							return userCommits;
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

						for (String mana : NatureCommit.getNatureList("management")) {
							if (patch.contains(mana)) {
								classification = "Management";
								b = false;
								break;
							}
						}

						if (b) {
							for (String re : NatureCommit.getNatureList("reeg")) {
								if (patch.contains(re)) {
									classification = "Reengineering";
									b = false;
									break;
								}
							}
						}
						if (b) {
							for (String co : NatureCommit.getNatureList("correct")) {
								if (patch.contains(co)) {
									classification = "Corrective Engineering";
									b = false;
									break;
								}
							}

						}
						if (b) {
							for (String fo : NatureCommit.getNatureList("foward")) {
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

					uc.setNatureClassification(classification);

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

		return userCommits;

	}

	@SuppressWarnings({ "unchecked", "rawtypes", "static-access" })
	private static void readUserCommitInfo(String project, List<UserCommit> userCommits, String minimumDate,
			String maximumDate, CommitMetrics commitMetrics, HashMap<String, Integer> commitsPerDay, String authorDate,
			HashSet<String> usedHashs, List<Integer> countTests, String user) {
		// TODO Auto-generated method stub

		for (UserCommit uc : userCommits) {

			if (uc.getAuthorLogin() != null) {
				if (!uc.getAuthorLogin().equals(user)) {
					if (uc.getAuthorName() != null) {
						if (!uc.getAuthorName().equals(user)) {
							continue;
						}
					}
				}
			}

			if (!authorDate.equals("")) {

				if (!Util.checkPastDate(uc.getDate(), authorDate, "-")) {
					continue;
				}

				if (usedHashs.contains(uc.getSha())) {
					continue;
				} else {

					usedHashs.add(uc.getSha());
				}

			}

			// System.out.println("Hashs Size Normal: " + usedHashs.size());
			// System.out.println("Hashs Size Pull: " + usedHashsPulls.size());

			if (uc.hasTestInclusion()) {
				countTests.add(1);
			}

			DateTime dt = new DateTime(DateTimeZone.UTC);
			LocalDateTime ldt = new LocalDateTime(DateTimeZone.UTC);
			long date = 0;
			long minimum = 0;
			long maximum = 0;
			try {
				date = dt.parse(uc.getDate()).getMillis();
				minimum = dt.parse(minimumDate).getMillis();
				maximum = dt.parse(maximumDate).getMillis();
			} catch (Exception e) {
				date = ldt.parse(uc.getDate().replace("Z", "")).getMillisOfDay();
				minimum = ldt.parse(minimumDate.replace("Z", "")).getMillisOfDay();
				maximum = ldt.parse(maximumDate.replace("Z", "")).getMillisOfDay();
			}

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
			commitMetrics.getDates().add(d.getDayOfMonth() + "/" + d.getMonthOfYear() + "/" + d.getYear());

			if (date < minimum) {
				minimum = date;
				minimumDate = uc.getDate();
			}
			if (date > maximum) {
				maximum = date;
				maximumDate = uc.getDate();
			}

			/*
			 * int week = d.getWeekOfWeekyear();
			 * 
			 * if (commitMetrics.getWeeks().containsKey(week)) { int count =
			 * commitMetrics.getWeeks().get(week); count++;
			 * commitMetrics.getWeeks().replace(week, count); } else {
			 * commitMetrics.getWeeks().put(week, 1); }
			 */

			try {

				double lineChanged = 0;
				commitMetrics.getAdditions().add(uc.getAdditions());
				lineChanged += uc.getAdditions();

				commitMetrics.getDeletions().add(uc.getDeletions());
				lineChanged += uc.getDeletions();

				commitMetrics.getLinesChanged().add(lineChanged);

				commitMetrics.getFiles().add(uc.getFiles().size());

				String hash = uc.getSha();

				for (String subSystem : uc.getFiles()) {

					if (commitMetrics.getCurrentHash().equals(hash)) {
						if (!commitMetrics.getCurrentSubSystem().contains(subSystem)) {
							commitMetrics.getCurrentSubSystem().add(subSystem);
						}
					} else {
						if (commitMetrics.getSubSystem().containsKey(subSystem)) {
							commitMetrics.getSubSystem().replace(subSystem,
									(commitMetrics.getSubSystem().get(subSystem) + 1));
						} else {
							commitMetrics.getSubSystem().put(subSystem, 1);
						}
					}
				}

			} catch (Exception e) {
				e.printStackTrace();// TODO: handle exception
			}

		}
	}

	public static void collectHashsFromUsers(String project) {

		String path = Util.getCommitsFolderPath(project);

		List<String> files = IO.filesOnFolder(path);

		HashMap<String, HashSet<CommitDAO>> userHashs = new HashMap<>();
		List<String> allHashs = new ArrayList<>();

		readCommitJson(path, files, userHashs, allHashs);

		String output = "user, hash, date\n";
		for (String k : userHashs.keySet()) {
			for (CommitDAO commit : userHashs.get(k)) {
				output += k + "," + commit.getHash() + "," + commit.getDate() + "\n";
			}
		}

		IO.writeAnyString(Util.getCommitsPath(project) + "users_hashs.csv", output);

		IO.writeAnyFile(Util.getCommitsPath(project) + "all_hashs.txt", allHashs);

	}

	public static List<String> collectPullCommitHashs(String project) {

		String path2 = Util.getPullIndividualCommitsPath(project);
		List<String> files2 = IO.filesOnFolder(path2);
		List<String> allHashs = new ArrayList<>();
		HashMap<String, HashSet<CommitDAO>> userHashs = new HashMap<>();
		readCommitJson(path2, files2, userHashs, allHashs);

		IO.writeAnyFile(Util.getPullCommitsPath(project) + "pull_hashs.txt", allHashs);

		return allHashs;

	}

	private static void readCommitJson(String path, List<String> files, HashMap<String, HashSet<CommitDAO>> userHashs,
			List<String> allHashs) {
		// TODO Auto-generated method stub
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		HashSet<String> userWithoutLogin = new HashSet<>();

		for (String file : files) {
			try {
				String fileData = new String(Files.readAllBytes(Paths.get(path + file)));
				List<LinkedTreeMap> commits = new ArrayList<>();
				try {
					commits = gson.fromJson(fileData, List.class);
				} catch (Exception e) {
					LinkedTreeMap c = gson.fromJson(fileData, LinkedTreeMap.class);
					commits.add(c);
				}

				for (LinkedTreeMap commit : commits) {

					CommitDAO commitDAO = new CommitDAO();
					String hash = "";
					String authorName = "";

					if (commit.containsKey("author")) {

						LinkedTreeMap a = (LinkedTreeMap) commit.get("author");

						if (commit.containsKey("sha")) {
							hash = (String) commit.get("sha");
							commitDAO.setHash(hash);
						}

						if (a != null) {

							if (a.containsKey("login")) {
								authorName = (String) a.get("login");
								commitDAO.setAuthor(authorName);
							}

							a = (LinkedTreeMap) commit.get("commit");
							if (a.containsKey("author")) {
								LinkedTreeMap b = (LinkedTreeMap) a.get("author");

								if (b.containsKey("date")) {
									String date = (String) b.get("date");
									commitDAO.setDate(date);
								}
							}
						} else {
							a = (LinkedTreeMap) commit.get("commit");

							if (a.containsKey("author")) {
								LinkedTreeMap b = (LinkedTreeMap) a.get("author");

								if (b.containsKey("name")) {
									authorName = (String) b.get("name");
									if (!authorName.contains(" ")) {
										continue;
									}
									userWithoutLogin.add(authorName);
									commitDAO.setAuthor(authorName);

								}

								if (b.containsKey("date")) {
									String date = (String) b.get("date");
									commitDAO.setDate(date);
								}
							}
						}

					}

					if (commitDAO.check()) {
						String author = commitDAO.getAuthor();
						String h = commitDAO.getHash();

						if (!userHashs.containsKey(author)) {
							userHashs.put(author, new HashSet<CommitDAO>());
						}
						// if (!hashDates.containsKey(authorName)) {
						// hashDates.put(authorName, new
						// ArrayList<CommitDAO>());
						// }

						HashSet<CommitDAO> hashs = userHashs.get(author);
						hashs.add(commitDAO);
						userHashs.replace(author, hashs);
						if (!allHashs.contains(h)) {
							allHashs.add(h);
						}

						// List<String> dates = hashDates.get(authorName);
						// dates.add(date);
						// hashDates.replace(authorName, dates);
					} else {
						System.out.println(
								commitDAO.getAuthor() + " - " + commitDAO.getHash() + " - " + commitDAO.getDate());
					}

				}

			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(path + file);
			}

		}

		System.out.println("Users Without Login: " + userWithoutLogin.size());

	}

	public static void collectOwnership(String project) {
		String individualCommitsPath = Util.getIndividualCommitsPath(project);

		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		List<String> commitsFilename = IO.filesOnFolder(individualCommitsPath);

		HashMap<String, HashMap<String, Double>> ownership = new HashMap<>();
		HashMap<String, HashMap<String, Double>> ownershipData = new HashMap<>();

		for (String commitFilename : commitsFilename) {

			String fileData = "";

			File f = new File(individualCommitsPath + commitFilename);

			if (!f.exists()) {
				continue;
			}

			try {
				fileData = new String(Files.readAllBytes(Paths.get(individualCommitsPath + commitFilename)));
			} catch (IOException e) {
				e.printStackTrace();
			}

			LinkedTreeMap<String, ?> commit = gson.fromJson(fileData, LinkedTreeMap.class);

			if (commit != null) {

				String name = "";
				// List<String> subsystem = new ArrayList<>();

				if (commit.containsKey("author")) {

					if (commit.get("author") != null) {

						LinkedTreeMap<String, ?> author = (LinkedTreeMap<String, ?>) commit.get("author");

						if (author != null) {
							if (author.containsKey("login")) {
								name = (String) author.get("login");
								if (name == null || name.equals("")) {
									continue;
								}
							}
						}

					}
				}

				if (name.equals("")) {

					if (commit.containsKey("commit")) {

						if (commit.get("commit") != null) {

							LinkedTreeMap<String, ?> commitInfo = (LinkedTreeMap<String, ?>) commit.get("commit");

							if (commitInfo != null) {
								if (commitInfo.containsKey("author")) {

									LinkedTreeMap<String, ?> author = (LinkedTreeMap<String, ?>) commitInfo
											.get("author");

									if (author != null) {
										if (author.containsKey("name")) {

											name = (String) author.get("name");
											if (name == null || name.equals("")) {
												continue;
											}

										}

									}
								}
							}

						}
					}

				}

				if (name.equals("")) {
					continue;
				}

				if (commit.containsKey("files")) {

					if (commit.get("files") != null) {

						List<LinkedTreeMap<String, ?>> files = (List) commit.get("files");

						for (LinkedTreeMap<String, ?> file : files) {

							String filename = (String) file.get("filename");

							if (!ownership.containsKey(filename)) {
								HashMap<String, Double> subsystemOwnership = new HashMap<>();
								ownership.put(filename, subsystemOwnership);
							}

							if (ownership.get(filename).containsKey(name)) {
								double count = ownership.get(filename).get(name);
								count++;
								ownership.get(filename).replace(name, count);
							} else {
								ownership.get(filename).put(name, 1.0);
							}

						}
					}

				}
			}

		}

		for (String k : ownership.keySet()) {

			HashMap<String, Double> subsystemOwnership = ownership.get(k);
			HashMap<String, Double> subsystemOwnershipData = new HashMap<>();

			int total = 0;
			for (String u : subsystemOwnership.keySet()) {
				double count = subsystemOwnership.get(u);
				total += count;
			}

			for (String u : subsystemOwnership.keySet()) {
				double count = subsystemOwnership.get(u);

				double percent = ((double) count) / (double) total;

				if (!subsystemOwnershipData.containsKey(u)) {
					subsystemOwnershipData.put(u, percent);
				} else {
					subsystemOwnershipData.replace(u, percent);
				}

				if (!ownershipData.containsKey(k)) {
					ownershipData.put(k, subsystemOwnershipData);
				} else {
					ownershipData.replace(k, subsystemOwnershipData);
				}

			}

		}

		String output = gson.toJson(ownershipData);

		IO.writeAnyString(Util.getCommitsPath(project) + "ownership.json", output);

	}

	public static List<UserCommit> readUserCommits(String project, String path) {
		List<UserCommit> userCommits = new ArrayList<>();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		List<String> files = IO.filesOnFolder(path);

		for (String file : files) {

			try {
				UserCommit uc = new UserCommit();

				if (path.contains("pull")) {
					uc.setPullCommit(true);
				}

				String fileData = new String(Files.readAllBytes(Paths.get(path + file)));
				LinkedTreeMap commitFile = gson.fromJson(fileData, LinkedTreeMap.class);

				if (commitFile.containsKey("commit")) {
					LinkedTreeMap<String, ?> commit = (LinkedTreeMap) commitFile.get("commit");

					if (commit.containsKey("author") && commit.containsKey("committer")) {

						LinkedTreeMap<String, String> author = (LinkedTreeMap) commit.get("author");

						String name = author.get("name");

						if (!name.contains(" ")) {
							name = "";
						}

						String login = "";

						if (commitFile.containsKey("author")) {
							LinkedTreeMap<String, String> au = (LinkedTreeMap) commitFile.get("author");

							if (au != null) {
								login = (String) au.get("login");
							}

						}

						if (login.equals("")) {
							if (name.contains(" ")) {
								login = name;
							}

						}

						if (login.contains("\\")) {
							continue;
						}

						if (login.equals("NA") || login.equals("(no author)")) {
							continue;
						}

						if (author.containsKey("email")) {
							String email = author.get("email");
							uc.setAuthorEmail(email);
						}
						if (author.containsKey("date")) {
							String date = author.get("date");
							uc.setDate(date);
						}
						if (commitFile.containsKey("sha")) {
							String sha = (String) commitFile.get("sha");
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

								for (String mana : NatureCommit.getNatureList("management")) {
									if (patch.contains(mana)) {
										classification = "Management";
										b = false;
										break;
									}
								}

								if (b) {
									for (String re : NatureCommit.getNatureList("reeg")) {
										if (patch.contains(re)) {
											classification = "Reengineering";
											b = false;
											break;
										}
									}
								}
								if (b) {
									for (String co : NatureCommit.getNatureList("correct")) {
										if (patch.contains(co)) {
											classification = "Corrective Engineering";
											b = false;
											break;
										}
									}

								}
								if (b) {
									for (String fo : NatureCommit.getNatureList("foward")) {
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

							uc.setNatureClassification(classification);

						}

						if (commitFile.containsKey("files")) {
							List<LinkedTreeMap> f = (List) commitFile.get("files");
							List<String> filenames = new ArrayList<>();

							for (LinkedTreeMap<?, ?> commitFiles : f) {

								if (f.size() == 0) {
									uc.setSizeClassification("empty");
								} else if (f.size() > 0 && f.size() < 6) {
									uc.setSizeClassification("tiny");
								} else if (f.size() > 5 && f.size() < 26) {
									uc.setSizeClassification("small");
								} else if (f.size() > 25 && f.size() < 126) {
									uc.setSizeClassification("medium");
								} else if (f.size() > 125) {
									uc.setSizeClassification("large");
								}

								uc.setModifiedFiles(f.size());

								if (commitFiles.containsKey("filename") && commitFiles != null) {
									String filename = (String) commitFiles.get("filename");

									filename = filename.toLowerCase();
									filenames.add(filename);

									if (filename.contains("test")) {
										uc.setTestInclusion(true);
									} else {
										uc.setTestInclusion(false);
									}
								}
							}

							uc.setFiles(filenames);
						}

						if (uc.getSizeClassification() == null || uc.getSizeClassification().equals("")) {
							uc.setSizeClassification("empty");
						}

						if (commitFile.containsKey("stats")) {
							LinkedTreeMap<String, Double> stats = (LinkedTreeMap<String, Double>) commitFile
									.get("stats");

							if (stats.containsKey("additions")) {
								Double addition = stats.get("additions");
								uc.setAdditions(addition);
							}

							if (stats.containsKey("deletions")) {
								Double deletion = stats.get("deletions");
								uc.setDeletions(deletion);
							}

							uc.setLinesChanged(uc.getAdditions() + uc.getDeletions());
						}

						uc.setAuthorLogin(login);
						uc.setAuthorName(name);

						userCommits.add(uc);
					}

				}

			} catch (Exception e) {
				e.printStackTrace();
				System.err.println(file);
			}
		}

		return userCommits;
	}

	public static void generateCommitsInfo(String project) {
		// TODO Auto-generated method stub
		System.out.println("Generating commit info");
		File f = new File(Util.getCommitsPath(project) + "commits_info.json");

		// if (f.exists()) {
		// return;
		// }

		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		List<UserCommit> userCommits = readUserCommits(project, Util.getIndividualCommitsPath(project));
		List<UserCommit> userPullCommits = readUserCommits(project, Util.getPullIndividualCommitsPath(project));
		List<UserCommit> userAllCommits = new ArrayList<>();
		// List<UserCommit> removeCommits = new ArrayList<>();

		for (UserCommit userCommit : userCommits) {
			for (UserCommit userPullCommit : userPullCommits) {
				if (userCommit.getSha().equals(userPullCommit.getSha())) {
					userCommit.setPullCommit(true);
				}
			}
		}

		// for (UserCommit removeCommit : removeCommits) {
		// userCommits.remove(removeCommit);
		// }

		userAllCommits.addAll(userCommits);
		// userAllCommits.addAll(userPullCommits);

		String output = gson.toJson(userAllCommits);

		IO.writeAnyString(Util.getCommitsPath(project) + "commits_info.json", output);
	}

	public static List<UserCommit> readCommitsInfo(String project) {
		// TODO Auto-generated method stub
		String file = Util.getCommitsPath(project) + "commits_info.json";
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		List<UserCommit> userCommits = new ArrayList<>();

		try {
			String fileData = new String(Files.readAllBytes(Paths.get(file)));
			List<LinkedTreeMap> commitFile = gson.fromJson(fileData, List.class);

			for (LinkedTreeMap commit : commitFile) {
				String hash = (String) commit.get("sha");
				String date = (String) commit.get("date");
				String authorName = (String) commit.get("authorName");
				String authorEmail = (String) commit.get("authorEmail");
				String authorLogin = (String) commit.get("authorLogin");
				String natureClassification = (String) commit.get("natureClassification");
				String sizeClassification = (String) commit.get("sizeClassification");
				boolean testInclusion = (boolean) commit.get("testInclusion");
				boolean pullCommit = (boolean) commit.get("pullCommit");
				double additions = (double) commit.get("additions");
				double deletions = (double) commit.get("deletions");
				double linesChanged = (double) commit.get("linesChanged");
				double modifiedFiles = (double) commit.get("modifiedFiles");
				List<String> files = (List) commit.get("files");

				if (authorLogin.contains(" ") || authorLogin.equals("")) {
					continue;
				}

				UserCommit uc = new UserCommit();

				uc.setSha(hash);
				uc.setDate(date);
				uc.setAuthorName(authorName);
				uc.setAuthorEmail(authorEmail);
				uc.setAuthorLogin(authorLogin);
				uc.setNatureClassification(natureClassification);
				uc.setSizeClassification(sizeClassification);
				uc.setTestInclusion(testInclusion);
				uc.setPullCommit(pullCommit);
				uc.setAdditions(additions);
				uc.setDeletions(deletions);
				uc.setLinesChanged(linesChanged);
				uc.setModifiedFiles(modifiedFiles);
				uc.setFiles(files);

				userCommits.add(uc);
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(file);
		}

		return userCommits;
	}

	public static void analyzeOwnership(String project) {
		// TODO Auto-generated method stub

		List<UserCommit> commits = readCommitsInfo(project);
		HashMap<String, List<String>> hashFiles = new HashMap<>();

		commits = Util.orderCommits(commits);

		List<String> ownerships = new ArrayList<>();

		for (UserCommit uc : commits) {

			for (String file : uc.getFiles()) {

				if (!hashFiles.containsKey(file)) {
					hashFiles.put(file, new ArrayList<String>());
				}

				String login = "";

				if (uc.getAuthorLogin().equals("") || uc.getAuthorLogin().equals(" ")) {
					if (uc.getAuthorName().equals("") || uc.getAuthorName().equals(" ")) {
						continue;
					}
					login = uc.getAuthorName();
				} else {
					login = uc.getAuthorLogin();
				}

				if (login.equals("")) {
					System.out.println(uc.getSha());
				}

				hashFiles.get(file).add(login);

			}

			double owner = 0;
			double totalOwner = 0;

			for (String file : uc.getFiles()) {

				for (String name : hashFiles.get(file)) {
					if (name.equals(uc.getAuthorLogin())) {
						owner++;
					}
				}
				totalOwner += hashFiles.get(file).size();
			}

			if (totalOwner == 0) {
				totalOwner = 1;
			}

			double ownershipCommit = owner / totalOwner;

			ownerships.add(uc.getSha() + "," + uc.getAuthorLogin() + "," + uc.getDate() + "," + ownershipCommit);
			System.out.println(uc.getSha() + "," + uc.getAuthorLogin() + "," + uc.getDate() + "," + ownershipCommit);
		}

		// System.out.println(hashFiles);

		IO.writeAnyFile(Util.getMetricsPath(project) + "ownership.csv", ownerships);

	}

}
