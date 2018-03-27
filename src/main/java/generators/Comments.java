package generators;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import objects.UserComment;
import utils.IO;
import utils.Util;

public class Comments {

	public static void generateCommentsIds(String project) {

		// Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String path = Util.getCommitCommentsFolder(project) + "general/";

		List<String> files = IO.filesOnFolder(path);

		List<String> ids = new ArrayList<>();

		for (String file : files) {
			try {
				String fileData = new String(Files.readAllBytes(Paths.get(path + file)));
				JsonArray object = Json.parse(fileData).asArray();

				for (JsonValue ob : object) {
					String id = ob.asObject().get("id").toString();
					ids.add(id);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		IO.writeAnyFile(Util.getCommitCommentsFolder(project) + "comments_ids.txt", ids);
	}

	public static HashMap<String, UserComment> readCommitComments(String project, String userLogin, String authorDate) {

		try {

			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String path = Util.getIndividualCommitCommentsFolder(project);
			List<String> files = IO.filesOnFolder(path);

			HashMap<String, UserComment> userCount = new HashMap<String, UserComment>();

			for (String file : files) {

				String fileData = new String(Files.readAllBytes(Paths.get(path + file)));
				LinkedTreeMap comment = gson.fromJson(fileData, LinkedTreeMap.class);

				LinkedTreeMap user = (LinkedTreeMap) comment.get("user");
				String login = (String) user.get("login");

				if (!login.equals(userLogin)) {
					continue;
				}

				if (!userCount.containsKey(login)) {
					userCount.put(login, new UserComment());
				}
				String created_at = (String) comment.get("created_at");

				if (!authorDate.equals("")) {
					if (!Util.checkPastDate(created_at, authorDate, "-")) {
						continue;

					}
				}

				UserComment count = userCount.get(login);
				count.setCount(count.getCount() + 1);
				count.setCreated_at(created_at);
				userCount.replace(login, count);

			}

			return userCount;

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return new HashMap<>();

	}

}
