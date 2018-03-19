package endpoints;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import utils.Config;
import utils.IO;
import utils.JSONManager;
import utils.LocalPaths;
import utils.Util;

public class PullsAPI {

	public static void collectCommitsOnPullRequestsFromList(String project, String url, List<String> pullIds) {

		String pathCommits = LocalPaths.PATH + project + "/pulls/commits/";
		String subPath = "heuristic2/";

		File f = new File(pathCommits);

		if (!f.exists()) {
			f.mkdirs();
		}

		for (String id : pullIds) {

			File f1 = new File(pathCommits + subPath + id + "/");
			if (!f1.exists()) {
				f1.mkdirs();
			}

			for (int i = 1; i < 100; i++) {

				String command = LocalPaths.CURL + " -i -u " + Config.USERNAME + ":" + Config.PASSWORD
						+ " \"https://api.github.com/repos/" + url + "/pulls/" + id + "/commits?page=" + i + "\"";

				boolean empty = JSONManager.getJSON(pathCommits + subPath + id + "/" + i + ".json", command);

				if (empty) {
					break;
				}

			}

		}

	}

	public static void downloadIndividualPulls(String project, String url) {

		String pathIndividual = Util.getIndividualPullsFolder(project);
		String path = Util.getPullsFolder(project);
		List<String> ids = IO.readAnyFile(path + "pulls_ids.txt");
		List<String> failedIds = new ArrayList<>();
		List<String> sucessfullIds = new ArrayList<>();

		try {

			for (String line : ids) {
				String[] l = line.split(",");
				String id = "";

				if (l.length == 1) {
					id = l[0];
				} else {
					id = l[1];
				}

				String command = LocalPaths.CURL + " -i -u " + Config.USERNAME + ":" + Config.PASSWORD
						+ " \"https://api.github.com/repos/" + url + "/pulls/" + id + "\"";

				boolean f = JSONManager.getJSON(pathIndividual + id + ".json", command);

				if (f) {
					failedIds.add(id);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}

		System.out.println("Failed ids: " + failedIds.toString());
	}

	public static void downloadCommentsInReviews(String project, String url) {

		String pathPullsComments = Util.getCommentsPullsFolder(project);
		List<String> pullsIds = IO.readAnyFile(Util.getPullsFolder(project) + "pulls_ids.txt");

		for (String l : pullsIds) {

			String[] line = l.split(",");
			String id = line[1];
			
			String subPath = pathPullsComments + id;
			
			Util.checkDirectory(subPath);
			
			for (int i = 1; i <= 50; i++) {
				
				String command = LocalPaths.CURL + " -i -u " + Config.USERNAME + ":" + Config.PASSWORD
						+ " \"https://api.github.com/repos/" + url + "/pulls/" + id + "/comments?page=" + i + "\"";
				
				System.out.println(command);

				boolean empty = JSONManager.getJSON(pathPullsComments + id + "/comments_" + i + ".json", command);

				if (empty) {
					break;
				}
			}

		}

	}
}
