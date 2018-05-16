package utils;

public class Git {

	public static void cloneProject(String project) {
		
		String url = URLs.getUrl(project);

		try {
			Process p = Runtime.getRuntime().exec("git clone https://github.com/" + url + " " + LocalPaths.PATH_GIT);
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	}
	
	public static void generateHashs(String project){
		try {
			Process p = Runtime.getRuntime().exec("git --git-dir=" + LocalPaths.PATH_GIT + project + " log --pretty=format:%H > hashs.txt");
			System.out.println("git --git-dir=" + LocalPaths.PATH_GIT + project + " log --pretty=format:%H > hashs.txt");
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	}

	/*public static void generateHashsByUser(String project, List<UserPoint> userPoints) {

		for (UserPoint userPoint : userPoints) {
			String user = userPoint.getName();
			try {
				Process p = Runtime.getRuntime().exec("git -C " + LocalPaths.PATH_GIT + " log --author=\"" + userPoints
						+ "\"--pretty=format:\"%H\" > " + Util.getUserPath(project, user) + "hashs.txt");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}*/

}
