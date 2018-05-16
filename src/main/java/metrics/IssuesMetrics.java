package metrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import generators.Issues;
import objects.CommitInfo;
import objects.IssueDAO;
import objects.UserIssue;
import objects.UserPoint;
import utils.IO;
import utils.Util;

public class IssuesMetrics {

	public static String checkIssues(List<UserIssue> userIssues, String user, String authorDate) {

		String userInfo = "";

		int countOpened = 0;
		int countClosed = 0;

		HashSet<String> ids = new HashSet<>();

		for (UserIssue ui : userIssues) {

			if (ids.contains(ui.getNumber())) {
				continue;
			}

			ids.add(ui.getNumber());

			if (ui.getClosedBy().equals(user)) {

				if (!Util.checkPastDate(ui.getClosedAt(), authorDate, "-")) {
					continue;
				}

				countClosed++;
			}

			if (ui.getCreator().equals(user)) {

				if (!Util.checkPastDate(ui.getCreatedAt(), authorDate, "-")) {
					continue;
				}

				countOpened++;
			}
		}

		userInfo += countOpened + "," + countClosed;

		return userInfo;
	}

	public static void analyzeIssuesMetrics(String project, List<UserPoint> userPoints) {
		System.out.println("Analyzing Issues");
		List<UserIssue> userIssues = Issues.readIssues(project);
		List<String> issuesMetrics = new ArrayList<>();

		issuesMetrics.add("hash, user, author_date, count_opened, count_closed");

		for (UserPoint userPoint : userPoints) {

			String user = userPoint.getName();
			user = user.replace("\"", "");

			if (user.equals("NA") || user.equals("")) {
				continue;
			}

			List<String> userLines = new ArrayList<>();

			for (CommitInfo cm : userPoint.getCommitInfo()) {

				String hash = cm.getHash();
				String authorDate = cm.getDate();
				authorDate = authorDate.replace("\"", "");

				String line = checkIssues(userIssues, user, authorDate);

				line = hash + "," + user + "," + authorDate + "," + line;

				userLines.add(line);
			}

			issuesMetrics.addAll(userLines);

		}

		IO.writeAnyFile(Util.getMetricsPath(project) + "issue_metrics.csv", issuesMetrics);

	}

	public static HashMap<String, IssueDAO> readIssuesAnalysis(String project) {

		HashMap<String, IssueDAO> userIssues = new HashMap<>();
		List<String> issues = IO.readAnyFile(Util.getMetricsPath(project) + "issue_metrics.csv");

		for (String issue : issues) {
			String[] line = issue.split(",");

			if (issue.contains("hash")) {
				continue;
			}

			IssueDAO i = new IssueDAO();
			i.setHash(line[0]);
			i.setUser(line[1]);
			i.setAuthorDate(line[2]);
			i.setCountOpened(Integer.parseInt(line[3]));
			i.setCountClosed(Integer.parseInt(line[4]));

			userIssues.put(line[0], i);

		}

		return userIssues;

	}

	public static double getRecentExperience(HashMap<String, IssueDAO> issues, String user, String date) {

		HashMap<String, Double> dates = new HashMap<>();

		for (String key : issues.keySet()) {

			IssueDAO issue = issues.get(key);

			if (issue.getUser().equals(user)) {
				if (Util.checkPastDate(issue.getAuthorDate(), date, "-")) {
					String year = issue.getAuthorDate();
					year = year.substring(0, year.indexOf("-"));

					if (!dates.containsKey(year)) {
						dates.put(year, 0.0);
					}

					double v = dates.get(year);

					dates.replace(year, (v + 1));

				}
			}

		}

		double exp = 0;

		for (String k : dates.keySet()) {
			double currentYear = 2018;
			double year = Double.parseDouble(k);
			double issuesOfYear = dates.get(k);

			exp += (issuesOfYear / (currentYear - (year - 1)));

		}
		
		return exp;
	}

}
