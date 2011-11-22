package controllers.errors;

import controllers.DabController;

public class UploadError extends DabController{

	public static String SESSION_ATTR_ERROR_MESSAGE_KEY ="uploadErrorMessageKey";
	public static String SESSION_ATTR_SUGGESTED_NAVIGATION ="suggestedNavigation";

	
	public static void uploadError() {
		// makes sure all links are kept, even if the user refreshes (why would he do that?!)
		flash.keep();
		render();
	}
	
	
}
