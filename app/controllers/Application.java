package controllers;


import static models.altermotif.SessionWrapper.*;

public class Application extends DabController {

	public static void index() {

		// "about us" link is now only displayed on the home page
		renderArgs.put("isAboutUsLinkVisible", true);
		render();
	}

	public static void aboutUs() {
		render();
	}

	public static void login() {
		render();
	}


	public static void udpateLanguage(String selection) {
		getSessionWrapper().updateSelectedLanguage(selection);
		renderJSON("{'ok': true}");
	}

	public static void termsAndConditions(String languageCode) {
		if (LANGUAGE_CODE_DUTCH.equalsIgnoreCase(languageCode)) {
			render("Application/termsAndConditions_nl.html");
		} else if (LANGUAGE_CODE_FRENCH.equalsIgnoreCase(languageCode)) {
			render("Application/termsAndConditions_fr.html");
		} else if (LANGUAGE_CODE_TURKISH.equalsIgnoreCase(languageCode)) {
			render("Application/termsAndConditions_tr.html");
		} else  {
			// always defaulting to English
			render("Application/termsAndConditions_en.html");
		}
	}
	
	public static void privacyStatement(String languageCode) {
		if (LANGUAGE_CODE_DUTCH.equalsIgnoreCase(languageCode)) {
			render("Application/privacyStatement_nl.html");
		} else if (LANGUAGE_CODE_FRENCH.equalsIgnoreCase(languageCode)) {
			render("Application/privacyStatement_fr.html");
		} else if (LANGUAGE_CODE_TURKISH.equalsIgnoreCase(languageCode)) {
			render("Application/privacyStatement_tr.html");
		} else  {
			// always defaulting to English
			render("Application/privacyStatement_en.html");
		}
	}
	
	
	public static void donate(String languageCode) {
		if (LANGUAGE_CODE_DUTCH.equalsIgnoreCase(languageCode)) {
			render("Application/donate_nl.html");
		} else if (LANGUAGE_CODE_FRENCH.equalsIgnoreCase(languageCode)) {
			render("Application/donate_fr.html");
		} else if (LANGUAGE_CODE_TURKISH.equalsIgnoreCase(languageCode)) {
			render("Application/donate_tr.html");
		} else  {
			// always defaulting to English
			render("Application/donate_en.html");
		}
	}
	
	
	
}