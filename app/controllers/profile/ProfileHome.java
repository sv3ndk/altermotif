package controllers.profile;

import com.svend.dab.core.beans.profile.UserProfile;

import controllers.BeanProvider;
import controllers.DabLoggedController;

public class ProfileHome extends DabLoggedController {

	public static void profileHome() {

		UserProfile visitedUserProfile = BeanProvider.getUserProfileService().loadUserProfile(getSessionWrapper().getLoggedInUserProfileId(), true);
		renderArgs.put("visitedUserProfile", visitedUserProfile);
		
		render();
	}

}
