package objects;

public class UserRepository {

	private String url;
	private String owner; // repos .json
	private String repositoryName; // repos .json
	private String numberOfCommits; // repos .json
	private String numberOfContributors; // repos .json
	private String createdAt; // repos .json
	private String language; // repos .json
	private String numberOfFiles; // repos .json

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getRepositoryName() {
		return repositoryName;
	}

	public void setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
	}

	public String getNumberOfCommits() {
		return numberOfCommits;
	}

	public void setNumberOfCommits(String numberOfCommits) {
		this.numberOfCommits = numberOfCommits;
	}

	public String getNumberOfContributors() {
		return numberOfContributors;
	}

	public void setNumberOfContributors(String numberOfContributors) {
		this.numberOfContributors = numberOfContributors;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getNumberOfFiles() {
		return numberOfFiles;
	}

	public void setNumberOfFiles(String numberOfFiles) {
		this.numberOfFiles = numberOfFiles;
	}

	@Override
	public String toString() {
		return "UserRepository [url=" + url + ", owner=" + owner + ", repositoryName=" + repositoryName
				+ ", numberOfCommits=" + numberOfCommits + ", numberOfContributors=" + numberOfContributors
				+ ", createdAt=" + createdAt + ", language=" + language + ", numberOfFiles=" + numberOfFiles + "]";
	}

}
