package controllers.profile;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.svend.dab.core.beans.profile.PrivacySettings;
import com.svend.dab.core.beans.profile.PrivacySettingsPep;
import com.svend.dab.core.beans.profile.UserProfile;

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
			renderArgs.put("privacyPep" , new PrivacySettingsPep(userProfile, BeanProvider.getProjectService()));
			
    	}
    	
        render();
    }
    
    
    public static void doEdit(PrivacySettings settings) {
    	BeanProvider.getUserProfileService().updatePrivacySettings(getSessionWrapper().getLoggedInUserProfileId(), settings);
    	ProfileHome.profileHome();
    }
    
    
    public static void cancel() {
    	profilePrivacy(null);
    }
    

}
