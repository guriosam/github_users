package endpoints;

import utils.Config;
import utils.JSONManager;
import utils.LocalPaths;
import utils.Util;

public class IssuesAPI {

	public static void generateRepositoryIssuesCall(String project, String url) {

		// repos/:owner/:repo/issues
		String path = Util.getGeneralIssuesPath(project);

		for (int i = 1; i < 1000; i++) {
			String command = LocalPaths.CURL + " -i -u " + Config.USERNAME + ":" + Config.PASSWORD
					+ " \"https://api.github.com/repos/" + url + "/issues?state=all&page=" + i + "\"";

			boolean empty = JSONManager.getJSON(path + i + ".json", command, false);

			if (empty) {
				break;
			}

		}

	}

}
