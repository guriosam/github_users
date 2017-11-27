package generators;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import utils.Config;
import utils.IO;
import utils.JSONManager;
import utils.LocalPaths;
import utils.Util;

public class Forks {

	public static void generateForksBranchesInfo(String project, String url) {

		generateForksNames(project, url);

		List<String> forks = Util.getForksNames(project);

		List<String> buggy = Util.getBuggyUsers(project);

		List<String> usersWithBranches = new ArrayList<>();

		// user/fork , date
		for (String fork : forks) {

			// getting user name
			String[] line = fork.split("/");
			String user = line[0];
			boolean flag = true;

			for (String bug : buggy) {
				if (bug.contains(user)) {
					flag = false;
				}
			}

			if (flag) {
				continue;
			}

			String path = Util.getUserBranchPath(project, user);

			File f = new File(path);
			if (!f.exists()) {
				f.mkdirs();
			}

			System.out.println(user);

			String[] v = fork.split(",");

			String date = v[1];

			System.out.println("Generate Branches Names for Fork");
			Branches.generateBranchesNames(path, v[0]);
			System.out.println("Collecting Branches Info for Fork");
			Branches.collectBranchInfo(path, v[0]);
			System.out.println("Reading Branch Info for Fork");
			Branches.readBranchInfo(path, user);

			List<String> branches = IO.readAnyFile(path + "/branches_of_user.txt");

			if (branches.size() == 0) {
				System.out.println("No branchs\n");
				continue;
			}

			usersWithBranches.add(user);

			System.out.println("Generate Branches Calls for Fork");
			Branches.generateBranchesCalls(path, v[0], user);
			System.out.println("Generate Branches Hashs for Fork");
			Branches.generateBranchesHashs(path, user, date);
			System.out.println("Collecting Branches Commits for Fork");
			Branches.collectBranchCommits(path, v[0]);
			// System.out.println("Generate Individual Branches Commits for
			// Fork");
			// collectIndividualBranchesCommits(path, fork);

			IO.writeAnyFile(LocalPaths.PATH + project + "/users_with_branches.txt", usersWithBranches);
		}

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void generateForksNames(String project, String url) {

		listForks(project, url);

		try {

			List<String> ids = new ArrayList<>();
			String path = LocalPaths.PATH + project + "/forks/";
			Gson gson = new GsonBuilder().setPrettyPrinting().create();

			List<String> files = IO.filesOnFolder(path);

			for (String file : files) {

				if (!file.contains("forks") || file.contains("names")) {
					continue;
				}

				String fileData = new String(Files.readAllBytes(Paths.get(path + file)));
				List<LinkedTreeMap> forks = gson.fromJson(fileData, List.class);

				for (LinkedTreeMap<?, ?> c : forks) {

					if (c.containsKey("full_name")) {
						String name = (String) c.get("full_name");
						String date = (String) c.get("created_at");

						ids.add(name + "," + date);
					}

				}

			}

			IO.writeAnyFile(path + "forks_names.txt", ids);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	public static void listForks(String project, String url) {

		String path = LocalPaths.PATH + project + "/forks/";

		File f = new File(path);
		if (!f.exists()) {
			f.mkdirs();
		}

		for (int i = 1; i < 100; i++) {

			String command = LocalPaths.CURL + " -i -u " + Config.USERNAME + ":" + Config.PASSWORD
					+ " \"https://api.github.com/repos/" + url + "/forks?page=" + i + "\"";

			boolean empty = JSONManager.getJSON(path + "forks_" + i + ".json", command);
			if (empty) {
				break;
			}

		}

	}

}
