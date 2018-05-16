package generators;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import objects.InsertionPoint;
import utils.IO;
import utils.Util;

public class SZZ {

	public static void modifiedClassesSZZ() {
		// TODO Auto-generated method stub

		List<String> projects = new ArrayList<>();
		// projects.add("elasticsearch");
		// projects.add("spring-boot");
		// projects.add("netty");
		projects.add("bazel");
		// projects.add("presto");
		// projects.add("Signal-Android");
		// projects.add("okhttp");
		// projects.add("RxJava");

		for (String project : projects) {
			System.out.println(project);
			List<InsertionPoint> points = readInsertionPoints(project);

			for (InsertionPoint point : points) {
				String fix = point.getFixCommit();

				List<String> filesFix = getFilesFromCommit(project, fix);

				System.out.println("Fix:" + fix);

				for (String insertionPoint : point.getInsertionPoints()) {

					List<String> filesInsertion = getFilesFromCommit(project, insertionPoint);

					for (String fileFix : filesFix) {
						for (String fileInsertion : filesInsertion) {
							if (fileFix.equals(fileInsertion)) {
								System.out.println(insertionPoint + ","
										+ fileInsertion.substring(fileInsertion.lastIndexOf("/") + 1));
							}
						}
					}

				}
			}
			System.out.println();
		}
	}

	private static List<String> getFilesFromCommit(String project, String insertionPoint) {

		List<String> filesInsertion = new ArrayList<>();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String path = Util.getIndividualCommitsPath(project) + insertionPoint + ".json";

		try {

			String fileData = new String(Files.readAllBytes(Paths.get(path)));

			LinkedTreeMap commit = gson.fromJson(fileData, LinkedTreeMap.class);

			if (commit.containsKey("files")) {
				List<LinkedTreeMap> f = (List) commit.get("files");

				for (LinkedTreeMap<?, ?> files : f) {
					if (files.containsKey("filename")) {
						String filename = (String) files.get("filename");

						if (filename.contains(".java")) {
							filesInsertion.add(filename);
						}
					}
				}

			}

		} catch (Exception e) {
			System.out.println(path);
			e.printStackTrace();
		}

		return filesInsertion;

	}

	private static List<InsertionPoint> readInsertionPoints(String project) {
		// TODO Auto-generated method stub

		List<InsertionPoint> points = new ArrayList<>();
		List<String> insertionPoints = IO.readAnyFile("C:/Users/gurio/Desktop/szz/" + project + ".csv");

		for (String line : insertionPoints) {

			if (line.contains("issue_id")) {
				continue;
			}

			InsertionPoint in = new InsertionPoint();

			List<String> insertion = new ArrayList<>();

			if (line.contains("\"")) {
				String[] l = line.replace(" ", "").split("\"");

				String p1 = l[0].trim().replace("\"", "");
				String p2 = l[1].trim().replace("\"", "");

				String[] l1 = p1.split(",");
				in.setIssueID(l1[0]);
				in.setFixCommit(l1[1]);

				String[] l2 = p2.split(",");
				for (int i = 0; i < l2.length; i++) {
					insertion.add(l2[i]);
				}

				in.setInsertionPoints(insertion);
			} else {
				String[] l = line.replace(" ", "").split(",");

				in.setIssueID(l[0]);
				in.setFixCommit(l[1]);
				insertion.add(l[3]);
				in.setInsertionPoints(insertion);
			}

			points.add(in);

		}

		return points;
	}


}
