package objects;

import java.util.ArrayList;
import java.util.List;

public class NatureCommit {

	private int emptyCount;
	int managementCount;
	int reengineeringCount;
	int correctiveEngineeringCount;
	int forwardEngineeringCount;
	int uncategorizedCount;

	public int getEmptyCount() {
		return emptyCount;
	}

	public void setEmptyCount(int emptyCount) {
		this.emptyCount = emptyCount;
	}

	public int getManagementCount() {
		return managementCount;
	}

	public void setManagementCount(int managementCount) {
		this.managementCount = managementCount;
	}

	public int getReengineeringCount() {
		return reengineeringCount;
	}

	public void setReengineeringCount(int reengineeringCount) {
		this.reengineeringCount = reengineeringCount;
	}

	public int getCorrectiveEngineeringCount() {
		return correctiveEngineeringCount;
	}

	public void setCorrectiveEngineeringCount(int correctiveEngineeringCount) {
		this.correctiveEngineeringCount = correctiveEngineeringCount;
	}

	public int getForwardEngineeringCount() {
		return forwardEngineeringCount;
	}

	public void setForwardEngineeringCount(int forwardEngineeringCount) {
		this.forwardEngineeringCount = forwardEngineeringCount;
	}

	public int getUncategorizedCount() {
		return uncategorizedCount;
	}

	public void setUncategorizedCount(int uncategorizedCount) {
		this.uncategorizedCount = uncategorizedCount;
	}

	public static List<String> getNatureList(String nature) {

		if (nature.equals("foward")) {
			List<String> foward = new ArrayList<>();
			foward.add("implement");
			foward.add("add");
			foward.add("request");
			foward.add("new");
			foward.add("test");
			foward.add("start");
			foward.add("includ");
			foward.add("initial");
			foward.add("introduc");
			foward.add("creat");
			foward.add("increas");

			return foward;

		}

		if (nature.equals("reeg")) {

			List<String> reeg = new ArrayList<>();

			reeg.add("optimiz");
			reeg.add("ajdust");
			reeg.add("update");
			reeg.add("delet");
			reeg.add("remov");
			reeg.add("chang");
			reeg.add("refactor");
			reeg.add("replac");
			reeg.add("modif");
			reeg.add("is now");
			reeg.add("are now");
			reeg.add("enhance");
			reeg.add("improv");
			reeg.add("design change");
			reeg.add("renam");
			reeg.add("eliminat");
			reeg.add("duplicat");
			reeg.add("restrutur");
			reeg.add("simplif");
			reeg.add("obsolete");
			reeg.add("rearrang");
			reeg.add("miss");
			reeg.add("enhanc");
			reeg.add("improv");

			return reeg;
		}

		if (nature.equals("correct")) {
			List<String> correct = new ArrayList<>();

			correct.add("bug");
			correct.add("fix");
			correct.add("issue");
			correct.add("error");
			correct.add("correct");
			correct.add("proper");
			correct.add("deprecat");
			correct.add("broke");

			return correct;

		}

		if (nature.equals("management")) {
			List<String> management = new ArrayList<>();

			management.add("clean");
			management.add("license");
			management.add("merge");
			management.add("release");
			management.add("structure");
			management.add("integrat");
			management.add("copyright");
			management.add("documentation");
			management.add("manual");
			management.add("javadoc");
			management.add("comment");
			management.add("migrat");
			management.add("repository");
			management.add("code review");
			management.add("polish");
			management.add("upgrade");
			management.add("style");
			management.add("formatting");
			management.add("organiz");
			management.add("TODO");

			return management;
		}
		
		return new ArrayList<>();
	}

}
