package endpoints;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import utils.Config;
import utils.IO;
import utils.JSONManager;
import utils.LocalPaths;
import utils.Util;

public class IssuesAPI {

	public static void generateRepositoryIssuesCall(String project, String url) {

		// repos/:owner/:repo/issues
		String path = Util.getGeneralIssuesPath(project);

		for (int i = 1; i < 1000; i++) {
			String command = LocalPaths.CURL + " -i -u " + Config.USERNAME + ":" + Config.PASSWORD
					+ " https://api.github.com/repos/" + url + "/issues?state=all&page=" + i;

			boolean empty = JSONManager.getJSON(path + i + ".json", command, false);

			if (empty) {
				break;
			}

		}

	}

	public static void generateCommentsCalls(String project, String url) {
	
		String path = Util.getIssuesCommentsPath(project);
	
		for (int i = 1; i < 10000; i++) {
	
			String command = LocalPaths.CURL + " -i -u " + Config.USERNAME + ":" + Config.PASSWORD
					+ " https://api.github.com/repos/" + url + "/issues/comments?page=" + i;
	
			boolean empty = JSONManager.getJSON(path + "comments_" + i + ".json", command, false);
	
			if (empty) {
				break;
			}
	
		}
	
	}

	public static void generateIndividualIssuesCall(String project, String url) {
	
		String path = Util.getIssuesPath(project);
	
		List<String> ids = IO.readAnyFile(path + "issues_ids.txt");
	
		List<String> commands = new ArrayList<>();
	
		for (String id : ids) {
	
			File f = new File(Util.getIndividualIssuesFolder(project));
			if (!f.exists()) {
				f.mkdirs();
			}
	
			String command = LocalPaths.CURL + " -i -u " + Config.USERNAME + ":" + Config.PASSWORD
					+ " https://api.github.com/repos/" + url + "/issues/" + id;
	
			JSONManager.getJSON(path + "individual/" + id + ".json", command, false);
	
		}
	
	}

}
