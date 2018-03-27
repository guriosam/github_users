package endpoints;

import java.util.List;

import utils.Config;
import utils.IO;
import utils.JSONManager;
import utils.LocalPaths;
import utils.Util;

public class CommentsAPI {

	public static void downloadGroupOfCommitComments(String project, String url) {

		System.out.println("Downloading Group of Commit Comments");

		String path = Util.getCommitCommentsFolder(project) + "general/";

		for (int i = 1; i < 10000; i++) {

			String command = LocalPaths.CURL + " -i -u " + Config.USERNAME + ":" + Config.PASSWORD
					+ " \"https://api.github.com/repos/" + url + "/comments?page=" + i + "\"";

			boolean empty = JSONManager.getJSON(path + "comments_" + i + ".json", command, false);

			if (empty) {
				break;
			}

		}

	}

	public static void downloadIndividualCommitComments(String project, String url) {

		System.out.println("Download Individual Commit Comments");

		String path = Util.getIndividualCommitCommentsFolder(project);
		List<String> ids = IO.readAnyFile(Util.getCommitCommentsFolder(project) + "comments_ids.txt");

		for (String id : ids) {

			String command = LocalPaths.CURL + " -i -u " + Config.USERNAME + ":" + Config.PASSWORD
					+ " \"https://api.github.com/repos/" + url + "/comments/" + id + "\"";

			JSONManager.getJSON(path + id + ".json", command, false);

		}

	}

}
