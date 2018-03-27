package generators;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import endpoints.CommitsAPI;
import utils.Config;
import utils.IO;
import utils.JSONManager;
import utils.LocalPaths;

public class Branches {

	public static void generateBranchesCalls(final String path, final String url, String user) {

		List<String> branches = IO.readAnyFile(path + "branches_of_user.txt");

		for (final String branch : branches) {

			if (branch.equals("")) {
				continue;
			}

			if (branch.contains("master")) {
				continue;
			}

			final String pathFile = path + branch + "/";
			File f = new File(path);
			if (!f.exists()) {
				f.mkdir();
			}

			for (int j = 1; j < 1000; j++) {

				final int k = j;

				// Thread t = new Thread(){
				// @Override
				// public void run(){

				String command = LocalPaths.CURL + " -i -u " + Config.USERNAME + ":" + Config.PASSWORD
						+ " \"https://api.github.com/repos/" + url + "/commits?sha=" + branch + "&author=" + user
						+ "&page=" + k + "\"";

				// System.out.println(command);

				boolean empty = JSONManager.getJSON(pathFile + k + ".json", command, false);
				// }
				// };//

				// t.start();

				/*
				 * if(k%100 == 0){ int wait = 0; while(wait < 5000){ wait++; } }
				 */

				// String command = LocalPaths.CURL + " -i -u " +
				// Config.USERNAME + ":" + Config.PASSWORD
				// + " \"https://api.github.com/repos/" + url + "/commits?sha="
				// + branch + "&page=" + j + "\"";

				// boolean empty = JSONManager.getJSON(pathFile + j + ".json",
				// command);

				if (empty) {
					break;
				}
			}

		}

	}

	@SuppressWarnings("rawtypes")
	public static void readBranchInfo(String path, String user) {

		List<String> files = IO.filesOnFolder(path + "general/");
		List<String> branches = new ArrayList<>();

		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		for (String file : files) {

			if (!file.contains(".json")) {
				continue;
			}

			String fileData = "";
			try {
				fileData = new String(Files.readAllBytes(Paths.get(path + "general/" + file)));
				LinkedTreeMap branch = gson.fromJson(fileData, LinkedTreeMap.class);

				if (branch.containsKey("commit")) {

					LinkedTreeMap commit = (LinkedTreeMap) branch.get("commit");

					if (commit.containsKey("author")) {

						LinkedTreeMap author = (LinkedTreeMap) commit.get("author");

						if (author == null) {
							continue;
						}

						if (author.containsKey("login")) {
							String login = (String) author.get("login");

							if (login.equals(user)) {

								String name = (String) branch.get("name");

								if (name.contains("master")) {
									continue;
								}

								branches.add(name);
							}

						}

					}

				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}

		IO.writeAnyFile(path + "branches_of_user.txt", branches);

	}

	public static void collectBranchInfo(String path, String url) {

		File f = new File(path + "general/");

		if (!f.exists()) {
			f.mkdirs();
		}

		List<String> branchesNames = IO.readAnyFile(path + "branches_names.txt");

		for (String name : branchesNames) {

			if (name.contains("//")) {
				continue;
			}

			String command = LocalPaths.CURL + " -i -u " + Config.USERNAME + ":" + Config.PASSWORD
					+ " \"https://api.github.com/repos/" + url + "/branches/" + name + "\"";

			boolean empty = JSONManager.getJSON(path + "general/" + name + ".json", command, false);
			if (empty) {
				break;
			}

		}

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void generateBranchesNames(String path, String url) {

		listBranches(path, url);

		try {

			List<String> ids = new ArrayList<>();

			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			List<String> notBranches = new ArrayList<>();

			List<String> files = IO.filesOnFolder(path);

			for (String file : files) {

				if (!file.contains("branch") || !file.contains(".json") || file.contains("names")
						|| file.contains("user")) {
					continue;
				}

				String fileData = new String(Files.readAllBytes(Paths.get(path + file)));
				List<LinkedTreeMap> branches = gson.fromJson(fileData, List.class);

				for (LinkedTreeMap<?, ?> c : branches) {

					if (c.containsKey("name")) {
						String name = (String) c.get("name");

						if (name.contains("master")) {
							continue;
						}

						String aux = name.replace(".", "");
						aux = aux.replace("v", "");
						aux = aux.replace("x", "");

						if (aux.matches("[0-9]+") && aux.length() > 0) {
							notBranches.add(name);
							continue;
						}

						if (aux.contains("gh-pages")) {
							notBranches.add(name);
							continue;
						}

						if (aux.contains("master")) {
							notBranches.add(name);
							continue;
						}

						ids.add(name);
					}

				}

			}

			IO.writeAnyFile(path + "branches_names.txt", ids);
			IO.writeAnyFile(path + "not_branches_names.txt", notBranches);

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

		public static void listBranches(String path, String url) {

		for (int i = 1; i < 1000; i++) {

			String command = LocalPaths.CURL + " -i -u " + Config.USERNAME + ":" + Config.PASSWORD
					+ " \"https://api.github.com/repos/" + url + "/branches?page=" + i + "\"";

			boolean empty = JSONManager.getJSON(path + "branches_" + i + ".json", command, false);
			if (empty) {
				break;
			}

		}

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void generateBranchesHashs(String path, String user, String dateFork) {
		System.out.println(dateFork);
		List<String> branches = IO.readAnyFile(path + "branches_of_user.txt");
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		List<String> hashs = new ArrayList<>();

		try {

			for (String branchName : branches) {

				if (branchName.contains("+")) {
					continue;
				}

				List<String> files = IO.filesOnFolder(path + branchName + "/");
				List<String> hashsBranch = new ArrayList<>();
				boolean before = false;

				for (String file : files) {

					List<LinkedTreeMap> commits = null;
					String fileData = new String(Files.readAllBytes(Paths.get(path + branchName + "/" + file)));
					commits = gson.fromJson(fileData, List.class);

					for (LinkedTreeMap commit : commits) {

						boolean flag = true;
						if (commit.containsKey("author")) {
							LinkedTreeMap c = (LinkedTreeMap) commit.get("author");

							if (c != null) {
								String login = (String) c.get("login");

								if (login != null) {

									if (login.equals(user)) {
										flag = false;
									}
								}

							}

						}

						if (commit.containsKey("commit")) {
							LinkedTreeMap c = (LinkedTreeMap) commit.get("commit");

							LinkedTreeMap c2 = (LinkedTreeMap) c.get("author");

							String date = (String) c2.get("date");
							if (date != null && dateFork != null) {

								if (date.contains("T")) {
									date = date.substring(0, date.indexOf("T"));
								}
								if (dateFork.contains("T")) {
									dateFork = dateFork.substring(0, dateFork.indexOf("T"));
								}

								String[] d1 = date.split("-");
								String[] d2 = dateFork.split("-");

								Integer year1 = Integer.parseInt(d1[0]);
								Integer year2 = Integer.parseInt(d2[0]);

								Integer m1 = Integer.parseInt(d1[1]);
								Integer m2 = Integer.parseInt(d2[1]);

								Integer day1 = Integer.parseInt(d1[2]);
								Integer day2 = Integer.parseInt(d2[2]);

								if (year1 < year2) {
									System.out.println(date);
									before = true;
									break;
								} else if (year1 == year2) {
									if (m1 < m2) {
										System.out.println(date);
										before = true;
										break;
									} else if (m1 == m2) {
										if (day1 < day2) {
											System.out.println(date);
											before = true;
											break;
										}
									}
								}

							}

						}

						if (flag) {
							continue;
						}

						if (commit.containsKey("sha")) {
							String sha = (String) commit.get("sha");
							hashsBranch.add(sha);
						}

					}

					if (before) {
						break;
					}

				}

				if (!before) {
					hashs.addAll(hashsBranch);
				}

			}

			HashSet<String> h = new HashSet<>();

			for (String hash : hashs) {
				h.add(hash);
			}

			List<String> finalHashs = new ArrayList<>();

			for (String hash : h) {
				finalHashs.add(hash);
			}

			IO.writeAnyFile(path + "branches_hashs.txt", finalHashs);

		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}

	}

	public static void collectBranchCommits(String path, String url) {

		File f = new File(path + "commits/");
		if (!f.exists()) {
			f.mkdirs();
		}

		List<String> branchesHashs = IO.readAnyFile(path + "branches_hashs.txt");

		CommitsAPI.downloadIndividualCommitsByHash(branchesHashs, url, path + "commits/");
	}

	
}
