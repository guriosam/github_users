package generators;

import java.util.ArrayList;
import java.util.List;

import utils.IO;
import utils.LocalPaths;

public class Contributors {

	public static void collectContributorsNames(String project) {
		List<String> contributors = IO.readAnyFile(LocalPaths.PATH + project + "/contributors_stats.json");

		List<String> names = new ArrayList<>();

		for (String contributor : contributors) {
			if (contributor.contains("\"login\"")) {
				String username = contributor.substring(contributor.indexOf(":") + 1, contributor.length() - 1);
				username = username.replace("\"", "");
				username = username.replace(" ", "");
				names.add(username);
			}
		}

		IO.writeAnyFile(LocalPaths.PATH + project + "/contributors_names.txt", names);
	}

	public static void generateContributorsStats(List<String> projects, List<String> urls) {

		List<String> calls = new ArrayList<>();

		for (int i = 0; i < projects.size(); i++) {
			String project = projects.get(i);
			String url = urls.get(i);

			String call = "mkdir " + LocalPaths.PATH + project + "/"
					+ "\ncurl -u guriosam:pokemon3 https://api.github.com/repos/" + url + "/stats/contributors > ./"
					+ project + "/contributors_stats.json";

			calls.add(call);
		}

		IO.writeAnyFile(LocalPaths.PATH + "get_contributors_stats.sh", calls);

	}

}
