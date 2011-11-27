package controllers.profile;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import models.altermotif.MappedValue;
import models.altermotif.profile.EditedProfile;

import org.cloudfoundry.org.codehaus.jackson.map.ObjectMapper;

import play.data.validation.Validation;
import web.utils.Utils;

import com.apple.eawt.Application;
import com.svend.dab.core.beans.profile.UserProfile;

import controllers.BeanProvider;
import controllers.DabLoggedController;
import controllers.validators.DabValidators;

public class ProfileEdit extends DabLoggedController {
	
	public static String EDITED_PROFILE_RENDERARG_NAME = "editedProfile";

	private static Logger logger = Logger.getLogger(ProfileEdit.class.getName());
	
	public final static String FLASH_PARAM_SKIP_LOAD_PROFILE = "skipLoadPrf";
	
	
	
	public static void profileEdit() {
		
		if (! flash.contains(FLASH_PARAM_SKIP_LOAD_PROFILE)) {
			UserProfile userProfile = BeanProvider.getUserProfileService().loadUserProfile(getSessionWrapper().getLoggedInUserProfileId(), true);
			if (userProfile == null) {
				logger.log(Level.WARNING, "Could not load profile for supposedly logged in user " + getSessionWrapper().getLoggedInUserProfileId() + " => redirecting to home page");
				controllers.Application.index();
				
			}
			renderArgs.put(EDITED_PROFILE_RENDERARG_NAME, new EditedProfile(userProfile));
		}
		
		Utils.addAllPossibleLanguageNamesToRenderArgs(getSessionWrapper(), renderArgs);
		render();
	}

	

	public static void doEdit(EditedProfile editedProfile) {
		
		if (editedProfile.getWebsite() != null && ! editedProfile.getWebsite().startsWith("http")) {
			editedProfile.setWebsite("http://" + editedProfile.getWebsite());
		}
		
		DabValidators.postValidateEditedProfile(editedProfile, EDITED_PROFILE_RENDERARG_NAME, validation, flash);
		
		if (Validation.hasErrors()) {
			
			params.flash();
			Validation.keep();
			flash.put(FLASH_PARAM_SKIP_LOAD_PROFILE, true);
			profileEdit();
		} else {
			// update the toUserPRoilfe...
			UserProfile partialProfile = new UserProfile();
			editedProfile.applyToPData(partialProfile.getPdata());
			partialProfile.setUsername(getSessionWrapper().getLoggedInUserProfileId());
			BeanProvider.getUserProfileService().updateProfilePersonalData(partialProfile);
			ProfileHome.profileHome();
		}
		
		
	}
	
	
	/**
	 * 
	 */
	public static void cancelEdit() {
		flash.remove(FLASH_PARAM_SKIP_LOAD_PROFILE);
		profileEdit();
	}
	
	
	
	
	
	
}
