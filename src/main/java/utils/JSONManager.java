package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JSONManager {

	public static boolean getJSON(String path, String command) {

		File file = new File(path);
		if (file.exists()) {
			return false;
		}

		try {

			boolean wait = true;
			long time = 0;

			while (wait) {

				Process p = Runtime.getRuntime().exec(command);

				BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

				String a = null;
				String json = "";
				boolean read = false;

				while ((a = input.readLine()) != null) {

					if (read) {

						if(a.equals("")){
							continue;
						}
						
						if (a.contains("https://developer.github.com/v3/#pagination")) {
							return true;
						}

						json += a + "\n";
					}

					if (a.contains("X-GitHub-Request-Id")) {
						read = true;
					}

					if (!read) {
						if (a.contains("Status:")) {
							if (a.contains("200")) {
								wait = false;
							} else {
								if (a.contains("500")) {
									return true;
								}
								System.out.println(a);
								wait = true;
							}
						}

						if (a.contains("X-RateLimit-Remaining:")) {
							if (wait) {
								String r = a.replace("X-RateLimit-Remaining: ", "");
								long remaining = Long.valueOf(r);

								System.out.println(remaining);
								if (remaining > 5) {
									wait = false;
								}
							}
						}

						if (a.contains("X-RateLimit-Reset:")) {
							String t = a.replace("X-RateLimit-Reset: ", "");
							time = Long.valueOf(t + "000");

							if (wait) {
								break;
							}
						}
					}

				}

				if (wait) {

					Calendar calendar = Calendar.getInstance();
					calendar.setTimeInMillis(time * 1000);

					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss dd MM yyyy");
					simpleDateFormat.setTimeZone(calendar.getTimeZone());

					System.out.println("Cooldown until: " + simpleDateFormat.format(calendar.getTime()));
					long current = System.currentTimeMillis();

					calendar.setTimeInMillis(current);

					simpleDateFormat = new SimpleDateFormat("HH:mm:ss dd MM yyyy");
					simpleDateFormat.setTimeZone(calendar.getTimeZone());

					System.out.println("Now: " + simpleDateFormat.format(calendar.getTime()));

					while (current <= time) {
						current = System.currentTimeMillis();
					}
					wait = false;
				} else {

					if (json.length() < 10) {
						return true;
					}

					String filePath = path.substring(0, path.lastIndexOf("/"));
					File f = new File(filePath);
					if (!f.exists()) {
						f.mkdirs();
					}

					IO.writeAnyString(path, json);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;

	}

}
