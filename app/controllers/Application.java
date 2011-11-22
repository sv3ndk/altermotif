package controllers;

import java.util.logging.Level;

import models.altermotif.SessionWrapper;

public class Application extends DabController {

	private static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Application.class.getName()); 
	
	
    public static void index() {
    	
    	logger.log(Level.INFO, "index");
    	System.out.println("index");
    	
        render();
    }
    

    public static void udpateLanguage(String selection) {
    	new SessionWrapper(session).updateSelectedLanguage(selection);
    	renderJSON("{'ok': true}");
    }
    

}