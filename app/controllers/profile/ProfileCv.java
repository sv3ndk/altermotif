package controllers.profile;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import models.altermotif.profile.ProfileManageCvVisibility;

import com.svend.dab.core.beans.profile.UserProfile;
import com.svend.dab.web.servlets.IUploadProcessor;

import controllers.BeanProvider;
import controllers.DabLoggedController;

public class ProfileCv extends DabLoggedController {

	private static Logger logger = Logger.getLogger(ProfileCv.class.getName());
	
	
    public static void profileCv() {
    	
    	
		UserProfile userProfile = BeanProvider.getUserProfileService().loadUserProfile(getSessionWrapper().getLoggedInUserProfileId(), true);
		if (userProfile == null) {
			logger.log(Level.WARNING, "Could not load profile for supposedly logged in user " + getSessionWrapper().getLoggedInUserProfileId() + " => redirecting to home page");
			controllers.Application.index();
		}

		renderArgs.put("userProfile", userProfile);

		new ProfileManageCvVisibility(userProfile).putInArgsList(renderArgs);
		
		
		
    	
        render();
    }
    
    
    public static void uploadCv(File theFile, String uploadtype) {
    	
    	logger.log(Level.INFO, "uploading file!");
    	
    	IUploadProcessor uploadProcessor = BeanProvider.getUploadProcessor();
    	
    	// TODO; handle erors here
    	
    	uploadProcessor.processUploadRequest(theFile, uploadtype, getSessionWrapper().getLoggedInUserProfileId());
    	
    	
    	
    	profileCv();
    	
    	
    	
    }
    

}
