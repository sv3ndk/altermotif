package controllers;

import models.altermotif.SessionWrapper;
import play.mvc.Before;
import play.mvc.Controller;

public class DabController extends Controller{


	@Before
	static void addDefaults() {
    	new SessionWrapper(session).putInArgsList(renderArgs);
    	renderArgs.put("numberOfUnreadReceivedMessages", "todo...");
	}
	
	
	public static SessionWrapper getSessionWrapper() {
		return (SessionWrapper) renderArgs.get(SessionWrapper.RENDER_PARAM_NAME);
	}
	
}
