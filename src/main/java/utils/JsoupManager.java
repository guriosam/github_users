package utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import com.google.gson.JsonElement;
import com.google.gson.JsonStreamParser;

public class JsoupManager {

	public static String getNumberCommits(String url) {
		
		
		Document doc = null;
		try {
			doc = Jsoup.connect(url).get();
			String commits = doc.getElementsByClass("commits").toString();
			int start = commits.indexOf("<span class=\"num text-emphasized\">");
			start += 34;
			int end = commits.indexOf("</span> commits");
			if (start >= 0 && end >= 0) {
				String numberCommits = commits.substring(start, end);
				return numberCommits.trim();
			}

			return "";

		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(url);
			e.printStackTrace();
		}

		return "";
	}

	public static String getNumberContributors(String url) {
		Document doc = null;
		try {

			WebDriver driver = new ChromeDriver();
			driver.manage().window().setSize(new Dimension(0, 0));
			driver.manage().window().setPosition(new Point(-2000, 0));
			driver.get(url);

			doc = Jsoup.connect(url).get();
			for (int second = 0;; second++) {
				if (second >= 5)
					try {
						break;
					} catch (Exception e) {

					}
				Thread.sleep(100);
			}
			doc = Jsoup.parse(driver.getPageSource());
			driver.close();

			String contributors = doc.getElementsByClass("num text-emphasized").get(3).toString();

			int start2 = 34;
			int end2 = contributors.indexOf("</span>");
			String numberContributors = contributors.substring(start2, end2);

			return numberContributors.trim();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "";
	}

	public static void getStats(String project, String url) {

		HashMap<String, String> userStats = new HashMap<>();

		Document doc = null;
		try {
			// doc = Jsoup.connect(url).get();
			WebDriver driver = new ChromeDriver();
			driver.get(url);

			// doc = Jsoup.connect(url).get();
			for (int second = 0;; second++) {
				if (second >= 60)
					try {
						if (isElementPresent(driver, By.id("contributors")))
							break;
					} catch (Exception e) {
					}
				Thread.sleep(100);
			}
			doc = Jsoup.parse(driver.getPageSource());
			driver.close();

			Elements contributorsStats = doc.getElementsByClass("text-normal");
			Elements contributorsStatsA = doc.getElementsByClass("text-green text-normal");
			Elements contributorsStatsD = doc.getElementsByClass("text-red text-normal");
			Elements contributorsStatsC = doc.getElementsByClass("link-gray text-normal");

			int j = 0;
			for (int i = 0; i < contributorsStats.size(); i++) {

				if (contributorsStats.get(i).toString().contains("a class")) {
					String contributorName = contributorsStats.get(i).toString();
					String add = contributorsStatsA.get(j).toString();
					String rem = contributorsStatsD.get(j).toString();
					String commits = contributorsStatsC.get(j).toString();

					contributorName = contributorName.substring(contributorName.indexOf("href=") + 7,
							contributorName.indexOf(">") - 1);
					add = add.substring(add.indexOf(">") + 1, add.indexOf("++") - 1);
					rem = rem.substring(rem.indexOf(">") + 1, rem.indexOf("--") - 1);
					commits = commits.substring(commits.indexOf(">") + 1, commits.indexOf("</a>"));

					if (!userStats.containsKey(contributorName)) {
						userStats.put(contributorName, contributorName + ";" + commits + ";" + add + ";" + rem);
					}

					j++;
				}

			}

			List<String> usersStats = new ArrayList<>();
			for (String s : userStats.keySet()) {
				usersStats.add(userStats.get(s));
			}

			IO.writeAnyFile(LocalPaths.PATH + project + "/stats.csv", usersStats);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static boolean isElementPresent(WebDriver driver, By by) {
		try {
			driver.findElement(by);
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}

}
