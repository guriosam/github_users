package metrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import generators.Commits;
import objects.CommitInfo;
import objects.NatureCommit;
import objects.NatureDAO;
import objects.NatureTotal;
import objects.UserCommit;
import objects.UserInfo;
import objects.UserPoint;
import utils.IO;
import utils.Util;

public class NatureMetrics {

	public static String setNatureValues(NatureCommit nc, NatureTotal natureTotal, String natureClassification) {

		String nature = "";

		natureTotal.setEmptyNatureTotal(nc.getEmptyCount());
		natureTotal.setForwardEngineeringTotal(nc.getForwardEngineeringCount());
		natureTotal.setCorrectiveEngineeringTotal(nc.getCorrectiveEngineeringCount());
		natureTotal.setReengineeringTotal(nc.getReengineeringCount());
		natureTotal.setManagementTotal(nc.getManagementCount());
		natureTotal.setUncategorizedTotal(nc.getUncategorizedCount());

		nature += natureTotal.getEmptyNatureTotal() + ",";
		nature += natureTotal.getForwardEngineeringTotal() + ",";
		nature += natureTotal.getCorrectiveEngineeringTotal() + ",";
		nature += natureTotal.getReengineeringTotal() + ",";
		nature += natureTotal.getUncategorizedTotal() + ",";
		nature += natureTotal.getManagementTotal() + ",";

		double totalNature = natureTotal.getTotalNature();

		if (totalNature == 0) {
			totalNature = 1;
		}

		nature += natureTotal.getEmptyNatureTotal() / totalNature + ",";
		nature += natureTotal.getForwardEngineeringTotal() / totalNature + ",";
		nature += natureTotal.getCorrectiveEngineeringTotal() / totalNature + ",";
		nature += natureTotal.getReengineeringTotal() / totalNature + ",";
		nature += natureTotal.getUncategorizedTotal() / totalNature + ",";
		nature += natureTotal.getManagementTotal() / totalNature + ",";
		nature += natureClassification;

		return nature;

	}

	public static NatureCommit calculateNature(List<UserCommit> userCommits, String user, String authorDate) {

		NatureCommit nc = new NatureCommit();

		int emptyCount = 0;
		int managementCount = 0;
		int reengineeringCount = 0;
		int correctiveEngineeringCount = 0;
		int forwardEngineeringCount = 0;
		int uncategorizedCount = 0;

		for (UserCommit uc : userCommits) {

			if (!uc.getAuthorName().equals(user) && !uc.getAuthorLogin().equals(user)) {
				continue;
			}

			if (!Util.checkPastDate(uc.getDate(), authorDate, "-")) {
				continue;
			}

			if (uc.getNatureClassification() != null) {
				if (uc.getNatureClassification().equals("Empty")) {
					emptyCount++;
				} else if (uc.getNatureClassification().equals("Management")) {
					managementCount++;
				} else if (uc.getNatureClassification().equals("Reengineering")) {
					reengineeringCount++;
				} else if (uc.getNatureClassification().equals("Corrective Engineering")) {
					correctiveEngineeringCount++;
				} else if (uc.getNatureClassification().equals("Forward Engineering")) {
					forwardEngineeringCount++;
				} else if (uc.getNatureClassification().equals("Uncategorized")) {
					uncategorizedCount++;
				}
			}

			nc.setEmptyCount(emptyCount);
			nc.setManagementCount(managementCount);
			nc.setReengineeringCount(reengineeringCount);
			nc.setCorrectiveEngineeringCount(correctiveEngineeringCount);
			nc.setForwardEngineeringCount(forwardEngineeringCount);
			nc.setUncategorizedCount(uncategorizedCount);

		}

		return nc;
	}

	public static void analyzeNature(String project, List<UserPoint> userPoints) {
		System.out.println("Analyzing Nature");
		List<String> natureMetrics = new ArrayList<>();
		List<UserCommit> userCommits = Commits.readCommitsInfo(project);

		natureMetrics.add("hash, user, author_date, empty, forward_engineering, corrective_engineering, "
				+ "reengineering, uncategorized, management, "
				+ "empty_percent, forward_engineering_percent, corrective_engineering_percent, "
				+ "reengineering_percent, uncategorized_percent, management_percent, classification");

		for (UserPoint userPoint : userPoints) {

			String user = userPoint.getName();
			user = user.replace("\"", "");

			if (user.equals("NA") || user.equals("")) {
				continue;
			}

			List<String> userLines = new ArrayList<>();
			NatureTotal natureTotal = new NatureTotal();

			for (CommitInfo cm : userPoint.getCommitInfo()) {

				String hash = cm.getHash();
				String authorDate = cm.getDate();
				authorDate = authorDate.replace("\"", "");

				NatureCommit nc = calculateNature(userCommits, user, authorDate);

				String natureClassification = "";
				for (UserCommit uc : userCommits) {
					if (uc.getSha().equals(hash)) {
						if (uc.getNatureClassification() != null) {
							natureClassification = uc.getNatureClassification();
						}
					}
				}

				if (natureClassification == null || natureClassification.equals("")) {
					continue;
				}

				String line = setNatureValues(nc, natureTotal, natureClassification);

				line = hash + "," + user + "," + authorDate + "," + line;

				userLines.add(line);
			}

			natureMetrics.addAll(userLines);

		}
		IO.writeAnyFile(Util.getMetricsPath(project) + "nature_metrics.csv", natureMetrics);
	}

	public static HashMap<String, NatureDAO> readNatureAnalysis(String project) {

		List<String> natures = IO.readAnyFile(Util.getMetricsPath(project) + "nature_metrics.csv");
		HashMap<String, NatureDAO> userNatures = new HashMap<>();

		for (String nature : natures) {
			String[] line = nature.split(",");

			if (nature.contains("hash")) {
				continue;
			}

			if (line.length != 16) {
				System.out.println(nature);
			}

			NatureDAO dao = new NatureDAO();
			dao.setHash(line[0]);
			dao.setUser(line[1]);
			dao.setAuthorDate(line[2]);
			dao.setEmpty(Double.parseDouble(line[3]));
			dao.setForwardEngineering(Double.parseDouble(line[4]));
			dao.setCorrectiveEngineering(Double.parseDouble(line[5]));
			dao.setReengineering(Double.parseDouble(line[6]));
			dao.setUncategorized(Double.parseDouble(line[7]));
			dao.setManagement(Double.parseDouble(line[8]));

			dao.setEmptyPercent(Double.parseDouble(line[9]));
			dao.setForwardEngineeringPercent(Double.parseDouble(line[10]));
			dao.setCorrectiveEngineeringPercent(Double.parseDouble(line[11]));
			dao.setReengineeringPercent(Double.parseDouble(line[12]));
			dao.setUncategorizedPercent(Double.parseDouble(line[13]));
			dao.setManagementPercent(Double.parseDouble(line[14]));
			dao.setClassification(line[15]);

			userNatures.put(line[0], dao);
		}

		return userNatures;

	}

	public static void setUserInfo(NatureDAO nature, UserInfo userInfo) {

		userInfo.setEmptyNatureCount((int) nature.getEmpty());
		userInfo.setForwardEngineeringCount((int) nature.getForwardEngineering());
		userInfo.setCorrectiveEngineeringCount((int) nature.getCorrectiveEngineering());
		userInfo.setReengineeringCount((int) nature.getReengineering());
		userInfo.setUncategorizedCount((int) nature.getUncategorized());
		userInfo.setManagementCount((int) nature.getManagement());

		userInfo.setEmptyNaturePercent((double) nature.getEmptyPercent());
		userInfo.setForwardEngineeringPercent((double) nature.getForwardEngineeringPercent());
		userInfo.setCorrectiveEngineeringPercent((double) nature.getCorrectiveEngineeringPercent());
		userInfo.setReengineeringPercent((double) nature.getReengineeringPercent());
		userInfo.setUncategorizedPercent((double) nature.getUncategorizedPercent());
		userInfo.setManagementPercent((double) nature.getManagementPercent());

		userInfo.setNature(nature.getClassification());

	}
}
