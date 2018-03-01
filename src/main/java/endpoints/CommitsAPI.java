package endpoints;

import java.util.ArrayList;
import java.util.List;

import utils.Config;
import utils.IO;
import utils.JSONManager;
import utils.LocalPaths;
import utils.Util;

public class CommitsAPI {

	public static void downloadIndividualCommitsByHash(List<String> hashs, String url, String path) {

		for (String hash : hashs) {
			String command = LocalPaths.CURL + " -i -u " + Config.USERNAME + ":" + Config.PASSWORD
					+ " \"https://api.github.com/repos/" + url + "/commits/" + hash + "\"";
			boolean empty = JSONManager.getJSON(path + hash + ".json", command);

			if (empty) {
				break;
			}

		}

	}

	public static void downloadGroupOfCommitsByAuthor(String project, String url) {
		List<String> names = Util.getBuggyUsers(project);
		for (String name : names) {
			String path = Util.getUserPath(project, name);
			for (int j = 1; j < 10000; j++) {
				String command = LocalPaths.CURL + " -i -u " + Config.USERNAME + ":" + Config.PASSWORD
						+ " \"https://api.github.com/repos/" + url + "/commits?author=" + name + "&page=" + j + "\"";
				boolean empty = JSONManager.getJSON(path + j + ".json", command);
				if (empty) {
					break;
				}
			}

		}

	}

	public static void downloadAllCommits(String project, String url) {
		String path = Util.getCommitsFolderPath(project);
		// List<String> commands = new ArrayList<>();
		for (int j = 1; j < 10000; j++) {
			String command = LocalPaths.CURL + " -i -u " + Config.USERNAME + ":" + Config.PASSWORD
					+ " \"https://api.github.com/repos/" + url + "/commits?page=" + j + "\"";
			boolean empty = JSONManager.getJSON(path + j + ".json", command);
			if (empty) {
				break;
			}
		}

	}

	public static void downloadAllIndividualCommits(String project, String url) {
		List<String> hashs = IO.readAnyFile(Util.getCommitsPath(project) + "all_hashs.txt");

		downloadIndividualCommitsByHash(hashs, url, Util.getIndividualCommitsPath(project));
	}
}
