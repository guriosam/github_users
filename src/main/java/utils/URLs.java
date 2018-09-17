package utils;

public class URLs {

	//JavaScript
	public static final String angularJS = "angular/angular.js";
	public static final String atom = "atom/atom";
	public static final String threeJS = "mrdoob/three.js";
	public static final String webpack = "webpack/webpack";
	public static final String meteor = "meteor/meteor";

	//Python
	public static final String ansible = "ansible/ansible";
	public static final String scikit_learn = "scikit-learn/scikit-learn";
	public static final String pandas = "pandas-dev/pandas";
	public static final String ipython = "ipython/ipython";
	public static final String salt = "saltstack/salt";
	
	//Java
	public static final String elasticsearch = "elastic/elasticsearch";
	public static final String spring_boot = "spring-projects/spring-boot";
	public static final String netty = "netty/netty";
	public static final String bazel = "bazelbuild/bazel";
	public static final String presto = "prestodb/presto";
	
	//Ruby
	public static final String fastlane = "fastlane/fastlane";
	public static final String vagrant = "hashicorp/vagrant";
	public static final String metasploit_framework = "rapid7/metasploit-framework";
	public static final String diaspora = "diaspora/diaspora";
	public static final String logstash = "elastic/logstash";
	
	//PHP
	public static final String symfony = "symfony/symfony";
	public static final String composer = "composer/composer";
	public static final String yii2 = "yiisoft/yii2";
	public static final String matomo = "matomo-org/matomo";
	public static final String cakephp = "cakephp/cakephp";

	public static String getUrl(String project) {

		project = project.replace("-", "_");
		project = project.toLowerCase();

		System.out.println(project);

		switch (project) {
		case "angular.js":
			return angularJS;
		case "atom":
			return atom;
		case "three.js":
			return threeJS;
		case "webpack":
			return webpack;
		case "meteor":
			return meteor;
		case "ansible":
			return ansible;
		case "scikit-learn":
			return scikit_learn;
		case "pandas":
			return pandas;
		case "ipython":
			return ipython;
		case "salt":
			return salt;
		case "elasticsearch":
			return elasticsearch;
		case "spring-boot":
			return spring_boot;
		case "netty":
			return netty;
		case "bazel":
			return bazel;
		case "presto":
			return presto;
		case "fastlane":
			return fastlane;
		case "vagrant":
			return vagrant;
		case "metasploit-framework":
			return metasploit_framework;
		case "diaspora":
			return diaspora;
		case "logstash":
			return logstash;
		case "symfony":
			return symfony;
		case "composer":
			return composer;
		case "yii2":
			return yii2;
		case "matomo":
			return matomo;
		case "cakephp":
			return cakephp;
		}

		return "";
	}

}
