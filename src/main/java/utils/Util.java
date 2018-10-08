package utils;

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
import org.joda.time.Days;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import objects.CommitInfo;
import objects.UserCommit;
import objects.UserPoint;

public class Util {

	public static List<String> getBuggyUsers(String project) {

		List<String> buggyUsers = IO.readAnyFile(
				LocalPaths.PROJECT_PATH + "insertion_points" + "/insertion_points_data_" + project + ".csv");

		List<String> users = new ArrayList<>();

		for (String buggy : buggyUsers) {
			if (buggy.contains("hash")) {
				continue;
			}

			String[] l = buggy.split(",");

			String name = l[1];
			name = name.replace("\"", "");

			if (name.equals("NA")) {
				continue;
			}

			if (!users.contains(name)) {
				users.add(name);
			}
		}

		return users;
	}

	public static List<String> getBuggyUserInfo(String project) {

		List<String> buggyUsers = IO.readAnyFile(
				LocalPaths.PROJECT_PATH + "insertion_points" + "/insertion_points_data_" + project + ".csv");

		List<String> users = new ArrayList<>();

		for (String buggy : buggyUsers) {
			if (buggy.contains("hash")) {
				continue;
			}

			users.add(buggy);
		}

		return users;
	}

	public static List<String> getUserList(String project) {
		List<String> buggy = getBuggyUsers(project);
		List<String> clean = getCleanUsers(project);

		List<String> users = new ArrayList<>();
		users.addAll(buggy);
		users.addAll(clean);

		return users;
	}

	public static List<String> getCleanUsers(String project) {
		List<String> clean = getUserInfo(project);
		HashSet<String> users = new HashSet<>();
		List<String> names = new ArrayList<>();

		for (String c : clean) {
			String[] c1 = c.split(",");
			users.add(c1[0]);
		}

		for (String s : users) {
			names.add(s);
		}

		return names;

	}

	public static List<String> getUserInfo(String project) {

		List<String> cleanUsers = IO.readAnyFile(getCommitsPath(project) + "users_hashs.csv");

		List<String> users = new ArrayList<>();

		for (String clean : cleanUsers) {
			if (clean.contains("hash")) {
				continue;
			}

			users.add(clean);
		}

		return users;
	}

	public static List<Date> orderDates(HashMap<String, Integer> commitsPerDay) {

		List<Date> commitDates = new ArrayList<>();

		SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");

		for (String date : commitsPerDay.keySet()) {
			try {
				Date data = formato.parse(date);
				commitDates.add(data);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		commitDates.sort(ComparatorUtils.NATURAL_COMPARATOR);

		return commitDates;

	}

	public static List<Integer> iterateDates(HashMap<String, Integer> commitsPerDay, List<Date> commitDates) {

		List<Integer> orderedDates = new ArrayList<>();

		SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");

		if (commitDates.size() > 1) {
			Date d1 = commitDates.get(0);
			Date d2 = commitDates.get(commitDates.size() - 1);

			Iterator<Date> i = new DateIterator(d1, d2);
			while (i.hasNext()) {
				Date date = i.next();

				String day = formato.format(date);
				if (commitsPerDay.containsKey(day)) {
					int count = commitsPerDay.get(day);
					orderedDates.add(count);
				} else {
					orderedDates.add(0);
				}
			}

			orderedDates.sort(ComparatorUtils.NATURAL_COMPARATOR);
		} else if (commitDates.size() == 1) {
			orderedDates.add(1);
		}

		return orderedDates;

	}

	public static String calculateCommitFrequency(UserPoint userPoint, String hash) {

		List<String> dates = new ArrayList<>();

		if (userPoint.getCommitInfo().size() == 1) {
			return "single";
		}

		if (userPoint.getCommitInfo().size() > 1 && userPoint.getCommitInfo().size() <= 20) {
			return "other";
		}

		for (CommitInfo cm : userPoint.getCommitInfo()) {

			if (cm.getHash().equals(hash)) {
				break;
			}

			String date = cm.getDate();
			date = date.replace("T", " ");
			date = date.replace("Z", "");
			// date = date.replace(":", "-");
			dates.add(date);
		}

		List<Integer> differences = new ArrayList<>();

		if (dates.size() > 0) {

			for (int i = 0; i < dates.size() - 1; i++) {
				String date1 = dates.get(i);
				String date2 = dates.get(i + 1);

				DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

				int days = Days.daysBetween(LocalDateTime.parse(date1, format), LocalDateTime.parse(date2, format))
						.getDays();
				differences.add(days);
			}

		}

		double frequency = 0;

		if (differences.size() == 0) {
			return "single";
		} else if (differences.size() > 0) {
			if (differences.size() % 2 == 0) {
				frequency = (double) (differences.get(differences.size() / 2)
						+ differences.get(differences.size() / 2 - 1)) / 2;
			} else {
				frequency = (double) differences.get(differences.size() / 2);
			}
		}

		if (frequency >= 0 && frequency <= 7) {
			return "daily";
		}

		if (frequency > 7 && frequency <= 30) {
			return "weekly";
		}

		if (frequency > 30) {
			return "monthly";
		}

		System.out.println("Frequency: " + frequency);
		return "daily";
	}

	public static String calculateCommitFrequency(List<UserCommit> userCommits, String hash) {

		List<String> dates = new ArrayList<>();

		if (userCommits.size() == 1) {
			return "single";
		}

		if (userCommits.size() > 1 && userCommits.size() <= 20) {
			return "other";
		}

		for (UserCommit uc : userCommits) {

			if (uc.getSha().equals(hash)) {
				break;
			}

			String date = uc.getDate();
			date = date.replace("T", " ");
			date = date.replace("Z", "");
			// date = date.replace(":", "-");
			dates.add(date);
		}

		List<Integer> differences = new ArrayList<>();

		if (dates.size() > 0) {

			for (int i = 0; i < dates.size() - 1; i++) {
				String date1 = dates.get(i);
				String date2 = dates.get(i + 1);

				DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

				int days = Days.daysBetween(LocalDateTime.parse(date1, format), LocalDateTime.parse(date2, format))
						.getDays();
				differences.add(days);
			}

		}

		double frequency = 0;

		if (differences.size() == 0) {
			return "single";
		} else if (differences.size() > 0) {
			if (differences.size() % 2 == 0) {
				frequency = (double) (differences.get(differences.size() / 2)
						+ differences.get(differences.size() / 2 - 1)) / 2;
			} else {
				frequency = (double) differences.get(differences.size() / 2);
			}
		}

		if (frequency >= 0 && frequency <= 7) {
			return "daily";
		}

		if (frequency > 7 && frequency <= 30) {
			return "weekly";
		}

		if (frequency > 30) {
			return "monthly";
		}

		System.out.println("Frequency: " + frequency);
		return "daily";
	}

	public static double calculateREXPCommit(UserPoint userPoint, String hash) {

		HashMap<String, Double> dates = new HashMap<>();

		for (CommitInfo cm : userPoint.getCommitInfo()) {

			String year = cm.getDate();
			year = year.substring(0, year.indexOf("-"));

			if (!dates.containsKey(year)) {
				dates.put(year, 0.0);
			}

			double v = dates.get(year);

			dates.replace(year, (v + 1));

			if (cm.getHash().equals(hash)) {
				break;
			}
		}

		double exp = 0;

		for (String k : dates.keySet()) {
			double currentYear = 2018;
			double year = Double.parseDouble(k);
			double commitsOfYear = dates.get(k);

			// System.out.println(currentYear + "-" + (year - 1) + " = " +
			// (currentYear - (year - 1)));

			exp += (commitsOfYear / (currentYear - (year - 1)));

		}

		return exp;
	}

	public static int getSumInt(List<Integer> list) {
		sortList(list);
		int sum = 0;
		for (int s : list) {
			sum += s;
		}
		return sum;
	}

	public static double getSumDouble(List<Double> list) {
		sortList(list);
		double sum = 0.0;
		for (double s : list) {
			sum += s;
		}
		return sum;
	}

	public static double calculateMedianDouble(List<Double> list) {
		double medianCommits = 0;
		if (list.size() == 0) {
			medianCommits = 0;
		} else if (list.size() > 0) {

			if (list.size() % 2 == 0) {
				medianCommits = (double) (list.get(list.size() / 2) + list.get(list.size() / 2 - 1)) / 2;
			} else {
				medianCommits = (double) list.get(list.size() / 2);
			}
		}

		return medianCommits;
	}

	@SuppressWarnings("unchecked")
	public static <E> void sortList(List<E> list) {
		list.sort(ComparatorUtils.NATURAL_COMPARATOR);
	}

	public static String getUserCommitsPath(String project, String name) {
		String path = getUserPath(project, name) + "commits/";
		checkDirectory(path);
		return path;
	}

	public static String getUserPath(String project, String name) {
		String path = getUsersFolderPath(project) + name + "/";
		checkDirectory(path);
		return path;
	}

	public static void checkDirectory(String path) {
		File f = new File(path);
		if (!f.exists()) {
			f.mkdirs();
		}
	}

	public static String getUsersFolderPath(String project) {
		String path = LocalPaths.PATH + project + "/users/";
		checkDirectory(path);
		return path;

	}

	public static String getPullsFolder(String project) {
		String path = LocalPaths.PATH + project + "/pulls/";
		checkDirectory(path);
		return path;
	}

	public static String getGeneralPullsFolder(String project) {
		String path = getPullsFolder(project) + "general/";
		checkDirectory(path);
		return path;
	}

	public static String getIndividualPullsFolder(String project) {
		String path = getPullsFolder(project) + "individual/";
		checkDirectory(path);
		return path;
	}

	public static String getCommentsPullsFolder(String project) {
		String path = getPullsFolder(project) + "comments/";
		checkDirectory(path);
		return path;
	}

	public static String getForkFolderPath(String project) {
		String path = LocalPaths.PATH + project + "/forks/";
		checkDirectory(path);
		return path;
	}

	public static List<String> getForksNames(String project) {
		List<String> forksNames = IO.readAnyFile(getForkFolderPath(project) + "forks_names.txt");
		return forksNames;
	}

	public static String getUserBranchPath(String project, String user) {
		String path = getUserPath(project, user) + "branches/";
		checkDirectory(path);
		return path;
	}

	public static String getGeneralIssuesPath(String project) {
		String path = getIssuesPath(project) + "general/";
		checkDirectory(path);
		return path;
	}

	public static String getIssuesPath(String project) {
		String path = LocalPaths.PATH + project + "/issues/";
		checkDirectory(path);
		return path;
	}

	public static String getPullCommitsPath(String project) {
		String path = getPullsFolder(project) + "commits/";
		checkDirectory(path);
		return path;
	}

	public static String getIndividualIssuesFolder(String project) {
		String path = getIssuesPath(project) + "individual/";
		checkDirectory(path);
		return path;
	}

	public static String getIssuesCommentsPath(String project) {
		String path = getIssuesPath(project) + "comments/";
		checkDirectory(path);
		return path;
	}

	public static boolean checkPastDate(String date1, String date2, String splitter) {

		if (date1 == null || date2 == null) {
			return false;
		}

		if (date1.contains("T")) {
			date1 = date1.replace("T", "-");
			date1 = date1.replace("Z", "");
			date1 = date1.replace(":", "-");
		}

		if (date2.contains("T")) {
			date2 = date2.replace("T", "-");
			date2 = date2.replace("Z", "");
			date2 = date2.replace(":", "-");
		}

		String[] d1 = date1.split(splitter);

		Integer year1 = Integer.parseInt(d1[0]);

		Integer m1 = Integer.parseInt(d1[1]);

		Integer day1 = Integer.parseInt(d1[2]);

		Integer hour1 = Integer.parseInt(d1[3]);

		Integer minute1 = Integer.parseInt(d1[4]);

		Integer second1 = Integer.parseInt(d1[5]);

		String[] d2 = date2.split(splitter);

		Integer year2 = Integer.parseInt(d2[0]);

		Integer m2 = Integer.parseInt(d2[1]);

		Integer day2 = Integer.parseInt(d2[2]);

		Integer hour2 = Integer.parseInt(d2[3]);

		Integer minute2 = Integer.parseInt(d2[4]);

		Integer second2 = Integer.parseInt(d2[5]);

		if (year1.compareTo(year2) > 0) {
			return false;
		} else if (year1.compareTo(year2) == 0) {
			if (m1.compareTo(m2) > 0) {
				return false;
			} else if (m1.compareTo(m2) == 0) {
				if (day1.compareTo(day2) > 0) {
					return false;
				} else if (day1.compareTo(day2) == 0) {
					if (hour1.compareTo(hour2) > 0) {
						return false;
					} else if (hour1.compareTo(hour2) == 0) {
						if (minute1.compareTo(minute2) > 0) {
							return false;
						} else if (minute1.compareTo(minute2) == 0) {
							if (second1.compareTo(second2) > 0) {
								return false;
							}
						}
					}
				}
			}
		}

		return true;

	}

	public static String getCollaboratorsPath(String project) {
		String path = LocalPaths.PATH + project + "/collaborators/";
		checkDirectory(path);
		return path;
	}

	public static String getCollaboratorsFolderPath(String project) {
		String path = getCollaboratorsPath(project) + "all/";
		checkDirectory(path);
		return path;
	}

	public static String getCommitsFolderPath(String project) {
		String path = getCommitsPath(project) + "all/";
		checkDirectory(path);
		return path;
	}

	public static String getCommitsPath(String project) {
		String path = LocalPaths.PATH + project + "/commits/";
		checkDirectory(path);
		return path;
	}

	public static String getIndividualCommitsPath(String project) {
		String path = getCommitsPath(project) + "individual/";
		checkDirectory(path);
		return path;
	}

	public static String getCommitCommentsFolder(String project) {
		String path = getCommitsPath(project) + "comments/";
		checkDirectory(path);
		return path;
	}

	public static String getCommitCommentsGeneralFolder(String project) {
		String path = getCommitCommentsFolder(project) + "general/";
		checkDirectory(path);
		return path;
	}

	public static String getIndividualCommitCommentsFolder(String project) {
		String path = getCommitCommentsFolder(project) + "individual/";
		checkDirectory(path);
		return path;
	}

	public static List<UserCommit> orderCommits(List<UserCommit> userCommits) {
		for (int i = 0; i < userCommits.size() - 1; i++) {
			for (int j = i + 1; j < userCommits.size(); j++) {
				if (!Util.checkPastDate(userCommits.get(i).getDate(), userCommits.get(j).getDate(), "-")) {
					UserCommit uc = userCommits.get(i);
					userCommits.set(i, userCommits.get(j));
					userCommits.set(j, uc);
				}
			}
		}

		return userCommits;
	}

	public static String getPullIndividualCommitsPath(String project) {
		String path = getPullCommitsPath(project) + "individual/";
		checkDirectory(path);
		return path;
	}

	public static String getMetricsPath(String project) {
		String path = LocalPaths.PATH + project + "/metrics/";
		checkDirectory(path);
		return path;
	}

	public static String getDate(String project, List<String> gitHashs) {
		String date = "";
		String file = "";

		for (int i = gitHashs.size() - 1; i > -1; i--) {
			String firstHash = gitHashs.get(i);
			
			file = Util.getIndividualCommitsPath(project) + firstHash + ".json";

			File f = new File(file);
			
			if (f.exists()) {
				break;
			}
		}
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		try {
			String fileData = new String(Files.readAllBytes(Paths.get(file)));
			LinkedTreeMap commitFile = gson.fromJson(fileData, LinkedTreeMap.class);

			if (commitFile.containsKey("commit")) {
				LinkedTreeMap commit = (LinkedTreeMap) commitFile.get("commit");

				if (commit != null && commit.containsKey("author")) {
					LinkedTreeMap author = (LinkedTreeMap) commit.get("author");
					gson = new GsonBuilder().setPrettyPrinting().create();
					if (author != null && author.containsKey("date")) {
						date = (String) author.get("date");
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (date.equals("")) {
			System.out.println(file);
			System.out.println("******** No date!! **********");
		} else {
			date = date.substring(0, date.indexOf("T"));
			System.out.println(date);
		}

		return date;
	}
}
