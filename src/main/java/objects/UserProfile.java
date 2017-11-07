package objects;

import java.util.List;

public class UserProfile {

	// Quantidade de commits do usuário, naquele determinado repositório.
	// Quantidade de arquivos na linguagem de programação predominante no
	// repositório.
	// Quantidade de seguidores, repositórios e estrelas.
	// Participação no repositório.
	// Tempo que utiliza o Github.

	private String username;
	private String name;
	private String email;
	private String location;
	// private int privateRepositories;
	private String followers;
	private String following;
	private String createdAt;
	private String profileUrl;
	// private List<UserRepository> publicRepositories;
	// private List<String> commits;
	private String numberRepositories;
	private String additions;
	private String deletions;
	private String numberCommits;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFollowers() {
		return followers;
	}

	public void setFollowers(String followers) {
		this.followers = followers;
	}

	public String getFollowing() {
		return following;
	}

	public void setFollowing(String following) {
		this.following = following;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getProfileUrl() {
		return profileUrl;
	}

	public void setProfileUrl(String profileUrl) {
		this.profileUrl = profileUrl;
	}

	public String getNumberRepositories() {
		return numberRepositories;
	}

	public void setNumberRepositories(String numberRepositories) {
		this.numberRepositories = numberRepositories;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getAdditions() {
		return additions;
	}

	public void setAdditions(String additions) {
		this.additions = additions;
	}

	public String getDeletions() {
		return deletions;
	}

	public void setDeletions(String deletions) {
		this.deletions = deletions;
	}

	@Override
	public String toString() {
		return "UserProfile [username=" + username + ", name=" + name + ", email=" + email + ", location=" + location
				+ ", followers=" + followers + ", following=" + following + ", createdAt=" + createdAt + ", profileUrl="
				+ profileUrl + ", numberRepositories=" + numberRepositories + ", additions=" + additions
				+ ", deletions=" + deletions + "]";
	}

	public String getNumberCommits() {
		return numberCommits;
	}

	public void setNumberCommits(String numberCommits) {
		this.numberCommits = numberCommits;
	}

}
