package metrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import generators.PullRequests;
import objects.CommitInfo;
import objects.IssueDAO;
import objects.PullRequestDAO;
import objects.UserPoint;
import objects.UserPullRequest;
import utils.IO;
import utils.Util;

public class PullRequestsMetrics {

	public static String checkPullRequests(List<UserPullRequest> userPull, String user, String authorDate) {

		String userInfo = "";

		int countOpened = 0;
		int countClosed = 0;
		int countMerged = 0;
		double openMerged = 0.0;
		int countRequested = 0;
		
		HashSet<String> ids = new HashSet<>();
		
		for (UserPullRequest upr : userPull) {

			if(ids.contains(upr.getId())){
				continue;
			}
			
			ids.add(upr.getId());
			
			boolean m = false;

			// if (approach) {

			try {
				if (upr.isMerged() && upr.getClosed_at() != null) {

					if (!Util.checkPastDate(upr.getClosed_at(), authorDate, "-")) {
						continue;
					}
				} else {

					if (!Util.checkPastDate(upr.getCreated_at(), authorDate, "-")) {
						continue;
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(upr.getClosed_at());
				System.out.println(authorDate);
			}

			if (upr.getUser() != null) {
				if (upr.getUser().equals(user)) {
					countOpened++;
					m = true;
				}

			}
			if (upr.getMerged_by() != null) {
				if (upr.getMerged_by().equals(user)) {
					countClosed++;
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
						countRequested++;
					}
				}

			}
		}

		if (countOpened != 0.0) {
			openMerged = (double) ((double) countMerged) / (double) countOpened;
		}

		userInfo = countOpened + "," + countClosed + "," + countMerged + "," + countRequested + "," + openMerged;

		return userInfo;
	}

	public static void analyzePullRequestsMetrics(String project, List<UserPoint> userPoints) {

		System.out.println("Analyzing Pull Requests");
		List<UserPullRequest> userPull = PullRequests.getPullRequests(project);
		List<String> pullMetrics = new ArrayList<>();

		pullMetrics
				.add("hash, user, author_date, count_opened, count_closed, count_merged, count_requested, open_merged");

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

				String line = checkPullRequests(userPull, user, authorDate);

				line = hash + "," + user + "," + authorDate + "," + line;

				userLines.add(line);
			}

			pullMetrics.addAll(userLines);

		}

		IO.writeAnyFile(Util.getMetricsPath(project) + "pull_metrics.csv", pullMetrics);

	}

	public static HashMap<String, PullRequestDAO> readPullRequestAnalysis(String project){
		
		HashMap<String, PullRequestDAO> userPulls = new HashMap<>();
		List<String> pullRequests = IO.readAnyFile(Util.getMetricsPath(project) + "pull_metrics.csv");
		
		for(String pullRequest : pullRequests){
			String[] line = pullRequest.split(",");
			
			if(pullRequest.contains("hash")){
				continue;
			}
			
			PullRequestDAO pull = new PullRequestDAO();
			
			pull.setHash(line[0]);
			pull.setUser(line[1]);
			pull.setAuthorDate(line[2]);
			pull.setCountOpened(Integer.parseInt(line[3]));
			pull.setCountClosed(Integer.parseInt(line[4]));
			pull.setCountMerged(Integer.parseInt(line[5]));
			pull.setCountRequested(Integer.parseInt(line[6]));
			pull.setOpenMerged(Double.parseDouble(line[7]));
			
			userPulls.put(line[0], pull);
			
		}
		
		return userPulls;
		
	}
	
	public static double getRecentExperience(HashMap<String, PullRequestDAO> pulls, String user, String date) {

		HashMap<String, Double> dates = new HashMap<>();

		for (String key : pulls.keySet()) {

			PullRequestDAO pull = pulls.get(key);

			if (pull.getUser().equals(user)) {
				if (Util.checkPastDate(pull.getAuthorDate(), date, "-")) {
					String year = pull.getAuthorDate();
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
