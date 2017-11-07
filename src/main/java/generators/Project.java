package generators;

import java.util.ArrayList;
import java.util.List;

import utils.IO;
import utils.LocalPaths;

public class Project {

	public static void generateRunAllScript(List<String> projects) {

		List<String> commands = new ArrayList<>();

		for (String project : projects) {
			commands.add("sh " + LocalPaths.PATH + project + "/collect_profiles.sh");
			// + "sh " + LocalPaths.PATH + project + "/collect_user_repos.sh");
		}

		IO.writeAnyFile(LocalPaths.PATH + "run.sh", commands);
	}

}
