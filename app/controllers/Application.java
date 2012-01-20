package controllers;

public class Application extends DabController {

	public static void index() {

		// "about us" link is now only displayed on the home page
		renderArgs.put("isAboutUsLinkVisible", true);
		render();
	}

	public static void aboutUs() {
		render();
	}

	public static void termsAndConditions() {
		render();
	}

	public static void udpateLanguage(String selection) {
		getSessionWrapper().updateSelectedLanguage(selection);
		renderJSON("{'ok': true}");
	}

}