package controllers.profile;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.svend.dab.core.beans.profile.UserProfile;

import controllers.BeanProvider;
import controllers.DabController;
import controllers.DabLoggedController;
import play.mvc.*;

public class ProfilePhotos extends DabLoggedController {

	private static Logger logger = Logger.getLogger(ProfilePhotos.class.getName());

	public static final int MAX_NUMBER_OF_PHOTOS = 20;

	public static void profilePhotos() {

		UserProfile userProfile = BeanProvider.getUserProfileService().loadUserProfile(getSessionWrapper().getLoggedInUserProfileId(), true);
		if (userProfile == null) {
			logger.log(Level.WARNING, "Could not load profile for supposedly logged in user " + getSessionWrapper().getLoggedInUserProfileId() + " => redirecting to home page");
			controllers.Application.index();
		}
		renderArgs.put("userProfile", userProfile);

		// we may upload more photos only if there is less then 10 or the list is still null
		boolean isActive = getSessionWrapper().isLoggedIn() && (userProfile.getPhotos() == null || userProfile.getPhotos().size() < MAX_NUMBER_OF_PHOTOS);
		renderArgs.put("uploadPhotoLinkActive", isActive);

		render();
	}

}
