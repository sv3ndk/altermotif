package controllers;

import models.altermotif.SessionWrapper;
import play.mvc.Before;
import play.mvc.Controller;

public class DabController extends Controller{


	@Before
	static void addDefaults() {
    	new SessionWrapper(session).putInArgsList(renderArgs);
    	renderArgs.put("selectedUserLanguageCode", getSessionWrapper().getSelectedLg());
    	
   		renderArgs.put("dabStylesheetsPath", BeanProvider.getResourceLocator().getDabStylesheetPath());
   		renderArgs.put("dabImagesPath", BeanProvider.getResourceLocator().getDabImagesPath());
   		renderArgs.put("cdnPath", BeanProvider.getResourceLocator().getCdnPath());
    	
    	if (getSessionWrapper().isLoggedIn()) {
    		renderArgs.put("numberOfUnreadReceivedMessages", BeanProvider.getMessagesService().getNumberOfUnreadMessages(getSessionWrapper().getLoggedInUserProfileId()));
    	}
	}
	
	public static SessionWrapper getSessionWrapper() {
		return (SessionWrapper) renderArgs.get(SessionWrapper.RENDER_PARAM_NAME);
	}
	
}
