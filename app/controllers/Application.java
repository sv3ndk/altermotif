package controllers;

import models.altermotif.SessionWrapper;

public class Application extends DabController {
	
    public static void index() {
        render();
    }

    public static void aboutUs() {
    	render();
    }
    
    
    public static void udpateLanguage(String selection) {
    	getSessionWrapper().updateSelectedLanguage(selection);
    	renderJSON("{'ok': true}");
    }
    

}