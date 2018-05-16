package metrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import generators.Comments;
import generators.Issues;
import generators.PullRequests;
import objects.CommentDAO;
import objects.CommitInfo;
import objects.IssueDAO;
import objects.NatureDAO;
import objects.UserPoint;
import utils.IO;
import utils.Util;

public class CommentsMetrics {

	public static String checkComments(String project, List<String> commentDates, String user, String authorDate) {

		int count = 0;

		if (commentDates == null) {
			return "0";
		}

		for (String date : commentDates) {

			if (Util.checkPastDate(date, authorDate, "-")) {
				count++;
			}
		}

		return count + "";
	}

	public static void analyzeComments(String project, List<UserPoint> userPoints) {
		System.out.println("Analyzing Comments");
		List<String> issueComments = new ArrayList<>();
		List<String> pullComments = new ArrayList<>();
		List<String> commitComments = new ArrayList<>();

		HashMap<String, List<String>> userCount = Issues.readComments(project);
		HashMap<String, List<String>> userPullsCommentCount = PullRequests.readComments(project);
		HashMap<String, List<String>> userCommitCommentCount = Comments.readCommitComments(project);

		issueComments.add("hash, user, author_date, count");
		pullComments.add("hash, user, author_date, count");
		commitComments.add("hash, user, author_date, count");

		for (UserPoint userPoint : userPoints) {

			String user = userPoint.getName();
			user = user.replace("\"", "");

			if (user.equals("NA") || user.equals("")) {
				continue;
			}

			List<String> userLines1 = new ArrayList<>();
			List<String> userLines2 = new ArrayList<>();
			List<String> userLines3 = new ArrayList<>();

			for (CommitInfo cm : userPoint.getCommitInfo()) {

				String hash = cm.getHash();
				String authorDate = cm.getDate();
				authorDate = authorDate.replace("\"", "");

				String line = checkComments(project, userCount.get(user), user, authorDate);

				line = hash + "," + user + "," + authorDate + "," + line;

				userLines1.add(line);

				String line2 = checkComments(project, userPullsCommentCount.get(user), user, authorDate);

				line2 = hash + "," + user + "," + authorDate + "," + line2;

				userLines2.add(line2);

				String line3 = checkComments(project, userCommitCommentCount.get(user), user, authorDate);

				line3 = hash + "," + user + "," + authorDate + "," + line3;

				userLines3.add(line3);
			}

			issueComments.addAll(userLines1);
			pullComments.addAll(userLines2);
			commitComments.addAll(userLines3);

		}

		IO.writeAnyFile(Util.getMetricsPath(project) + "issue_comments_metrics.csv", issueComments);
		IO.writeAnyFile(Util.getMetricsPath(project) + "pull_comments_metrics.csv", pullComments);
		IO.writeAnyFile(Util.getMetricsPath(project) + "commit_comments_metrics.csv", commitComments);

	}

	public static HashMap<String, CommentDAO> readCommentAnalysis(String project) {

		List<String> pullComments = IO.readAnyFile(Util.getMetricsPath(project) + "pull_comments_metrics.csv");
		List<String> issueComments = IO.readAnyFile(Util.getMetricsPath(project) + "issue_comments_metrics.csv");
		List<String> commitComments = IO.readAnyFile(Util.getMetricsPath(project) + "commit_comments_metrics.csv");
		HashMap<String, CommentDAO> userComments = new HashMap<>();

		for (String pullComment : pullComments) {
			String[] line = pullComment.split(",");
			
			if (pullComment.contains("hash")) {
				continue;
			}

			String hash = line[0];
			if (!userComments.containsKey(hash)) {
				CommentDAO comment = new CommentDAO();
				userComments.put(hash, comment);
			}

			

			CommentDAO comment = userComments.get(hash);
			comment.setHash(hash);
			
			comment.setUser(line[1]);
			comment.setAuthorDate(line[2]);
			comment.setPullCommentCount(Integer.parseInt(line[3]));

			userComments.replace(hash, comment);
		}

		for (String issueComment : issueComments) {
			String[] line = issueComment.split(",");

			if (issueComment.contains("hash")) {
				continue;
			}
			
			String hash = line[0];
			if (!userComments.containsKey(hash)) {
				CommentDAO comment = new CommentDAO();
				userComments.put(hash, comment);
			}



			CommentDAO comment = userComments.get(hash);
			comment.setHash(hash);
			comment.setUser(line[1]);
			comment.setIssueCommentCount(Integer.parseInt(line[3]));

			userComments.replace(hash, comment);
		}

		for (String commitComment : commitComments) {
			String[] line = commitComment.split(",");

			String hash = line[0];
			
			if (commitComment.contains("hash")) {
				continue;
			}
			
			if (!userComments.containsKey(hash)) {
				CommentDAO comment = new CommentDAO();
				userComments.put(hash, comment);
			}
		

			CommentDAO comment = userComments.get(hash);
			comment.setHash(hash);
			comment.setUser(line[1]);
			comment.setCommitCommentCount(Integer.parseInt(line[3]));

			userComments.replace(hash, comment);
		}

		return userComments;

	}

	public static double getRecentExperience(HashMap<String, CommentDAO> comments, String user, String date) {

		HashMap<String, Double> dates = new HashMap<>();
		
		
		for (String key : comments.keySet()) {

			CommentDAO comment = comments.get(key);
			
			if (comment.getUser().equals(user)) {
				if (Util.checkPastDate(comment.getAuthorDate(), date, "-")) {
					String year = comment.getAuthorDate();
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
			double commentsOfYear = dates.get(k);

			exp += (commentsOfYear / (currentYear - (year - 1)));

		}
		
		return exp;
	}

	
	
}
