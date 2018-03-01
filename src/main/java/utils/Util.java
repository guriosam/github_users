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

	public static List<String> getUserInfo(String project) {

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
	
	public static String getIndividualCommitsPath(String project){
		String path = getCommitsPath(project) + "individual/";
		checkDirectory(path);
		return path;
	}
}
