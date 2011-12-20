package controllers.profile;

import java.util.logging.Level;
import java.util.logging.Logger;

import models.altermotif.profile.EditedProfile;
import play.data.validation.Validation;
import web.utils.Utils;

import com.google.common.base.Strings;
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
		
		if (!Strings.isNullOrEmpty(editedProfile.getWebsite()) && ! editedProfile.getWebsite().startsWith("http")) {
			editedProfile.setWebsite("http://" + editedProfile.getWebsite());
		}
		
		DabValidators.postValidateEditedProfile(editedProfile, EDITED_PROFILE_RENDERARG_NAME, validation, flash);
		
		if (Validation.hasErrors()) {
			params.flash();
			Validation.keep();
			flash.put(FLASH_PARAM_SKIP_LOAD_PROFILE, true);
			profileEdit();
		} else {
			UserProfile partialProfile = new UserProfile();
			editedProfile.applyToPData(partialProfile.getPdata());
			partialProfile.setUsername(getSessionWrapper().getLoggedInUserProfileId());
			BeanProvider.getUserProfileService().updateProfilePersonalData(partialProfile);
			
			// waiting a bit because actual saving is asynchronous => this increase the probability that the user will see the impact directly
			Utils.waitABit();
			ProfileView.profileView(getSessionWrapper().getLoggedInUserProfileId());
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
