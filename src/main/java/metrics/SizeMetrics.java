package metrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import generators.Commits;
import objects.CommitInfo;
import objects.CommitSize;
import objects.SizeDAO;
import objects.SizeTotal;
import objects.UserCommit;
import objects.UserInfo;
import objects.UserPoint;
import utils.IO;
import utils.Util;

public class SizeMetrics {

	public static String setSizeValues(CommitSize cs, SizeTotal sizeTotal, String sizeClassification) {

		String size = "";

		sizeTotal.setEmptySizeTotal(cs.getEmptyCount());
		sizeTotal.setTinyTotal(cs.getTinyCount());
		sizeTotal.setSmallTotal(cs.getSmallCount());
		sizeTotal.setMediumTotal(cs.getMediumCount());
		sizeTotal.setLargeTotal(cs.getLargeCount());

		size += sizeTotal.getEmptySizeTotal() + ",";
		size += sizeTotal.getTinyTotal() + ",";
		size += sizeTotal.getSmallTotal() + ",";
		size += sizeTotal.getMediumTotal() + ",";
		size += sizeTotal.getLargeTotal() + ",";

		double totalSize = sizeTotal.getTotalSize();

		if (totalSize == 0) {
			totalSize = 1;
		}

		size += sizeTotal.getEmptySizeTotal() / totalSize + ",";
		size += sizeTotal.getTinyTotal() / totalSize + ",";
		size += sizeTotal.getSmallTotal() / totalSize + ",";
		size += sizeTotal.getMediumTotal() / totalSize + ",";
		size += sizeTotal.getLargeTotal() / totalSize + ",";

		size += sizeClassification;

		return size;
	}

	public static CommitSize calculateSize(List<UserCommit> userCommits, String user, String authorDate) {

		CommitSize cs = new CommitSize();

		int empty = 0;
		int tiny = 0;
		int small = 0;
		int medium = 0;
		int large = 0;

		for (UserCommit uc : userCommits) {

			if (!uc.getAuthorName().equals(user) && !uc.getAuthorLogin().equals(user)) {
				continue;
			}

			if (!Util.checkPastDate(uc.getDate(), authorDate, "-")) {
				continue;
			}

			if (uc.getSizeClassification() != null) {
				if (uc.getSizeClassification().toLowerCase().equals("empty")) {
					empty++;
				} else if (uc.getSizeClassification().toLowerCase().equals("tiny")) {
					tiny++;
				} else if (uc.getSizeClassification().toLowerCase().equals("small")) {
					small++;
				} else if (uc.getSizeClassification().toLowerCase().equals("medium")) {
					medium++;
				} else if (uc.getSizeClassification().toLowerCase().equals("large")) {
					large++;
				}
			}

			cs.setEmptyCount(empty);
			cs.setLargeCount(large);
			cs.setTinyCount(tiny);
			cs.setMediumCount(medium);
			cs.setSmallCount(small);

		}

		return cs;
	}

	public static void analyzeSize(String project, List<UserPoint> userPoints) {
		System.out.println("Analyzing Size");
		List<String> sizeMetrics = new ArrayList<>();
		List<UserCommit> userCommits = Commits.readCommitsInfo(project);

		sizeMetrics.add("hash, user, author_date, empty, tiny, small, medium, large, "
				+ "empty_percent, tiny_percent, small_percent, medium_percent, large_percent, classification");

		for (UserPoint userPoint : userPoints) {

			String user = userPoint.getName();
			user = user.replace("\"", "");

			if (user.equals("NA") || user.equals("")) {
				continue;
			}

			List<String> userLines = new ArrayList<>();
			SizeTotal sizeTotal = new SizeTotal();

			for (CommitInfo cm : userPoint.getCommitInfo()) {

				String hash = cm.getHash();
				String authorDate = cm.getDate();
				authorDate = authorDate.replace("\"", "");

				CommitSize cs = calculateSize(userCommits, user, authorDate);

				String sizeClassification = "empty";
				for (UserCommit uc : userCommits) {
					if (uc.getSha().equals(hash)) {
						if (uc.getSizeClassification() != null) {
							sizeClassification = uc.getSizeClassification();
						}
					}
				}

				String line = setSizeValues(cs, sizeTotal, sizeClassification);

				line = hash + "," + user + "," + authorDate + "," + line;

				userLines.add(line);
			}

			sizeMetrics.addAll(userLines);

		}
		IO.writeAnyFile(Util.getMetricsPath(project) + "size_metrics.csv", sizeMetrics);
	}

	public static HashMap<String, SizeDAO> readSizeAnalysis(String project) {

		List<String> sizes = IO.readAnyFile(Util.getMetricsPath(project) + "size_metrics.csv");
		HashMap<String, SizeDAO> userSizes = new HashMap<>();

		for (String size : sizes) {
			String[] line = size.split(",");

			if (size.contains("hash")) {
				continue;
			}

			try {
				SizeDAO dao = new SizeDAO();
				dao.setHash(line[0]);
				dao.setUser(line[1]);
				dao.setAuthorDate(line[2]);
				dao.setEmpty(Double.parseDouble(line[3]));
				dao.setTiny(Double.parseDouble(line[4]));
				dao.setSmall(Double.parseDouble(line[5]));
				dao.setMedium(Double.parseDouble(line[6]));
				dao.setLarge(Double.parseDouble(line[7]));
				dao.setEmptyPercent(Double.parseDouble(line[8]));
				dao.setTinyPercent(Double.parseDouble(line[9]));
				dao.setSmallPercent(Double.parseDouble(line[10]));
				dao.setMediumPercent(Double.parseDouble(line[11]));
				dao.setLargePercent(Double.parseDouble(line[12]));
				dao.setClassification(line[13]);
				userSizes.put(line[0], dao);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(size);
				// TODO: handle exception
			}

		}

		return userSizes;

	}

	public static void setUserInfo(SizeDAO size, UserInfo userInfo) {

		userInfo.setEmptyNatureCount((int) size.getEmpty());
		userInfo.setTinyCount((int) size.getTiny());
		userInfo.setSmallCount((int) size.getSmall());
		userInfo.setMediumCount((int) size.getMedium());
		userInfo.setLargeCount((int) size.getLarge());

		userInfo.setEmptyNaturePercent((double) size.getEmptyPercent());
		userInfo.setTinyPercent((double) size.getTinyPercent());
		userInfo.setSmallPercent((double) size.getSmallPercent());
		userInfo.setMediumPercent((double) size.getMediumPercent());
		userInfo.setLargePercent((double) size.getLargePercent());

		if (size.getClassification().contains("empty")) {
			userInfo.setEmptySize(true);
		} else if (size.getClassification().contains("tiny")) {
			userInfo.setTiny(true);
		} else if (size.getClassification().contains("small")) {
			userInfo.setSmall(true);
		} else if (size.getClassification().contains("medium")) {
			userInfo.setMedium(true);
		} else if (size.getClassification().contains("large")) {
			userInfo.setLarge(true);
		}

	}

}
