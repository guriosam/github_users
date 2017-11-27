package utils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.ComparatorUtils;

public class Util {

	public static List<String> getBuggyUsers(String project) {

		List<String> buggyUsers = IO.readAnyFile(LocalPaths.PATH + project + "/buggy_users.csv");

		List<String> users = new ArrayList<>();

		for (String buggy : buggyUsers) {
			if (buggy.contains("username")) {
				continue;
			}

			String[] l = buggy.split(",");

			String name = l[0];
			name = name.replace("\"", "");

			users.add(name);
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

}
