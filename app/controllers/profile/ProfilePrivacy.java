package controllers.profile;

import java.util.logging.Level;
import static controllers.profile.ProfileEdit.EDITED_PROFILE_RENDERARG_NAME;
import java.util.logging.Logger;

import models.altermotif.profile.EditedProfile;

import com.svend.dab.core.beans.profile.PrivacySettings;
import com.svend.dab.core.beans.profile.UserProfile;

import controllers.Application;
import controllers.BeanProvider;
import controllers.DabLoggedController;

public class ProfilePrivacy extends DabLoggedController {

	private static Logger logger = Logger.getLogger(ProfilePrivacy.class.getName());
	
	public static String EDITED_PRIVACY_RENDERARG_NAME = "editedPrivacy";

	
    public static void profilePrivacy(String s) {
    	
    	if (s == null) {
			UserProfile userProfile = BeanProvider.getUserProfileService().loadUserProfile(getSessionWrapper().getLoggedInUserProfileId(), true);
			if (userProfile == null) {
				logger.log(Level.WARNING, "Could not load profile for supposedly logged in user " + getSessionWrapper().getLoggedInUserProfileId() + " => redirecting to home page");
				controllers.Application.index();
				
			}
			renderArgs.put(EDITED_PRIVACY_RENDERARG_NAME , userProfile.getPrivacySettings());
    	}
    	
        render();
    }
    
    
    public static void doEdit(PrivacySettings settings) {

    	BeanProvider.getUserProfileService().updatePrivacySettings(getSessionWrapper().getLoggedInUserProfileId(), settings);
    	
    	Application.index();
    	
    }
    
    
    public static void cancel() {
    	profilePrivacy(null);
    }
    

}
