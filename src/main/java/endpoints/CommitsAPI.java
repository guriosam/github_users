package endpoints;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import generators.Commits;
import objects.UserCommit;
import utils.Config;
import utils.IO;
import utils.JSONManager;
import utils.LocalPaths;
import utils.Util;

public class CommitsAPI {

	public static void downloadIndividualCommitsByHash(List<String> hashs, String url, String path) {

		//List<String> hashsToDownload = new ArrayList<>();

		boolean flag = true;
		for (String hash : hashs) {
			File f = new File(path + hash + ".json");
			if (!f.exists()) {
			//	flag = false;
				//hashsToDownload.add(hash);
			}
		}

		//if (flag) {
		//	return;
		//}

		
		for (String hash : hashs) {
			
			String command = LocalPaths.CURL + " -i -u " + Config.USERNAME + ":" + Config.PASSWORD
					+ " https://api.github.com/repos/" + url + "/commits/" + hash;
			JSONManager.getJSON(path + hash + ".json", command, true);

		}

	}

	public static void downloadGroupOfCommitsByAuthor(String project, String url) {
		List<String> names = Util.getBuggyUsers(project);
		List<String> cleanNames = Util.getCleanUsers(project);
		for (String name : names) {
			String path = Util.getUserPath(project, name);
			for (int j = 1; j < 10000; j++) {
				String command = LocalPaths.CURL + " -i -u " + Config.USERNAME + ":" + Config.PASSWORD
						+ " https://api.github.com/repos/" + url + "/commits?author=" + name + "&page=" + j;
				boolean empty = JSONManager.getJSON(path + j + ".json", command, false);
				if (empty) {
					break;
				}
			}

		}
		for (String name : cleanNames) {
			System.out.println(name);
			String path = Util.getUserPath(project, name);
			for (int j = 1; j < 10000; j++) {
				String command = LocalPaths.CURL + " -i -u " + Config.USERNAME + ":" + Config.PASSWORD
						+ " https://api.github.com/repos/" + url + "/commits?author=" + name + "&page=" + j;
				boolean empty = JSONManager.getJSON(path + j + ".json", command, false);

				if (empty) {
					break;
				}
			}

		}

	}

	public static void downloadAllCommits(String project, String url) {
		final String path = Util.getCommitsFolderPath(project);
		// List<String> commands = new ArrayList<>();
		for (int j = 1; j < 1100; j++) {

				final String command = LocalPaths.CURL + " -i -u " + Config.USERNAME + ":" + Config.PASSWORD
						+ " https://api.github.com/repos/" + url + "/commits?page=" + j;
				boolean empty = false;

				empty = JSONManager.getJSON(path + j + ".json", command, true);

				if (empty) {
					break;
				}

		}

	}

	public static void downloadAllIndividualCommits(String project, String url) {
		List<String> hashs = IO.readAnyFile(Util.getCommitsPath(project) + "all_hashs.txt");

		downloadIndividualCommitsByHash(hashs, url, Util.getIndividualCommitsPath(project));
	}

	public static void downloadUserCommitsFromMaster(String project, String url) {

		List<String> users = Util.getUserList(project);

		for (String user : users) {
			System.out.println(user);

			List<UserCommit> userCommits = Commits.mineCommits(Util.getUserPath(project, user),
					Util.getUserPath(project, user), user);
			List<String> hashs = new ArrayList<>();

			for (UserCommit uc : userCommits) {
				hashs.add(uc.getSha());

			}

			String path = Util.getUserCommitsPath(project, user);

			downloadIndividualCommitsByHash(hashs, url, path);

		}

	}

}
