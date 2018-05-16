package metrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import objects.UserInfo;
import utils.Util;

public class CommitMetrics {

	private String currentHash;

	private HashSet<String> dates;

	//private HashMap<Integer, Integer> weeks;

	private List<Double> additions;

	private List<Double> deletions;

	private List<Double> linesChanged;

	private List<Integer> files;

	private HashMap<String, Integer> subSystem;

	private List<String> currentSubSystem;

	public CommitMetrics() {
		setDates(new HashSet<String>());

		setAdditions(new ArrayList<Double>());

		setDeletions(new ArrayList<Double>());

		setLinesChanged(new ArrayList<Double>());

		setFiles(new ArrayList<Integer>());

		setSubSystem(new HashMap<String, Integer>());
	}

	public HashSet<String> getDates() {
		return dates;
	}

	public void setDates(HashSet<String> dates) {
		this.dates = dates;
	}

	public List<Double> getAdditions() {
		return additions;
	}

	public void setAdditions(List<Double> additions) {
		this.additions = additions;
	}

	public List<Double> getDeletions() {
		return deletions;
	}

	public void setDeletions(List<Double> deletions) {
		this.deletions = deletions;
	}

	public List<Double> getLinesChanged() {
		return linesChanged;
	}

	public void setLinesChanged(List<Double> linesChanged) {
		this.linesChanged = linesChanged;
	}

	public List<Integer> getFiles() {
		return files;
	}

	public void setFiles(List<Integer> files) {
		this.files = files;
	}

	public void setMetrics(UserInfo userInfo) {

		double adds = Util.getSumDouble(additions);
		double lchanged = Util.getSumDouble(linesChanged);
		double rems = Util.getSumDouble(deletions);
		double lines = adds + rems;
		double file = (double) Util.getSumInt(files);

		userInfo.setTotalAdditions(adds);
		userInfo.setMeanAdditions(additions);
		userInfo.setMedianAdditions(additions);

		userInfo.setTotalLinesChanged(lchanged);
		userInfo.setMeanLinesChanged(linesChanged);
		userInfo.setMedianLinesChanged(linesChanged);

		userInfo.setTotalDeletions(rems);
		userInfo.setMeanDeletions(deletions);
		userInfo.setMedianDeletions(deletions);

		userInfo.setTotalModifiedFiles(file);
		userInfo.setMeanModifiedFiles(files);
		userInfo.setMedianModifiedFiles(files);

		userInfo.setActiveDays(dates.size());

	}

	public HashMap<String, Integer> getSubSystem() {
		return subSystem;
	}

	public void setSubSystem(HashMap<String, Integer> subSystem) {
		this.subSystem = subSystem;
	}

	public List<String> getCurrentSubSystem() {
		return currentSubSystem;
	}

	public void setCurrentSubSystem(List<String> currentSubSystem) {
		this.currentSubSystem = currentSubSystem;
	}

	public String getCurrentHash() {
		return currentHash;
	}

	public void setCurrentHash(String currentHash) {
		this.currentHash = currentHash;
	}

	public int getSEXP() {

		int total = 0;
		for (String k : currentSubSystem) {
			if (subSystem.containsKey(k)) {
				total += subSystem.get(k);
			}
		}
		return total;

	}

}
