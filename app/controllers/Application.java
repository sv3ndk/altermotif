package controllers;

import models.altermotif.SessionWrapper;

public class Application extends DabController {
	
    public static void index() {
        render();
    }

    public static void udpateLanguage(String selection) {
    	new SessionWrapper(session).updateSelectedLanguage(selection);
    	renderJSON("{'ok': true}");
    }
    

}