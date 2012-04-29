package controllers.profile;

import static controllers.errors.UploadError.SESSION_ATTR_ERROR_MESSAGE_KEY;
import static controllers.errors.UploadError.SESSION_ATTR_SUGGESTED_NAVIGATION;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import models.altermotif.MappedValue;
import play.mvc.Router;

import com.svend.dab.core.beans.DabUploadFailedException;
import com.svend.dab.core.beans.profile.UserProfile;

import controllers.BeanProvider;
import controllers.DabLoggedController;
import controllers.errors.UploadError;

public class ProfilePhotos extends DabLoggedController {

	private static Logger logger = Logger.getLogger(ProfilePhotos.class.getName());

	public static void profilePhotos() {

		UserProfile userProfile = BeanProvider.getUserProfileService().loadUserProfile(getSessionWrapper().getLoggedInUserProfileId(), true);
		if (userProfile == null) {
			logger.log(Level.WARNING, "Could not load profile for supposedly logged in user " + getSessionWrapper().getLoggedInUserProfileId()
					+ " => redirecting to home page");
			controllers.Application.index();
		}
		renderArgs.put("userProfile", userProfile);

		// TODO: security: delegate this to the "PEP" component + "max number of photo" is defined twice!
		renderArgs.put("uploadPhotoLinkActive", !userProfile.getPhotoAlbum().isFull());

		render();
	}

	public static void doUploadPhoto(File theFile) {

		// TODO: security: role check here
		try {
			BeanProvider.getProfilePhotoService().addOnePhoto(getSessionWrapper().getLoggedInUserProfileId(), theFile);
			profilePhotos();
		} catch (DabUploadFailedException e) {

			flash.put(SESSION_ATTR_SUGGESTED_NAVIGATION, Router.reverse("profile.ProfilePhotos.profilePhotos"));

			if (e.getReason() == null) {
				// this should never happen, defaulting to a generic error message
				logger.log(Level.SEVERE,
						"Could not process uploaded request, but the exception for the failed upload does not contain a reason, this is weird...", e);
				flash.put(SESSION_ATTR_ERROR_MESSAGE_KEY, DabUploadFailedException.failureReason.technicalError.getErrorMessageKey());
			} else {
				logger.log(Level.SEVERE, "Could not process uploaded request, upload failure reason: " + e.getReason(), e);
				flash.put(SESSION_ATTR_ERROR_MESSAGE_KEY, e.getReason().getErrorMessageKey());
			}

			UploadError.uploadError();

		} catch (Exception e) {
			logger.log(Level.SEVERE, "could not process upload request: generic error", e);
			flash.put(SESSION_ATTR_SUGGESTED_NAVIGATION, Router.reverse("profile.ProfilePhotos.profilePhotos"));
			flash.put(SESSION_ATTR_ERROR_MESSAGE_KEY, DabUploadFailedException.failureReason.technicalError.getErrorMessageKey());
			UploadError.uploadError();
		}

	}

	public static void doDeletePhoto(int deletedPhotoIdx) {

		// TODO: security: role check here
		UserProfile userProfile = BeanProvider.getUserProfileService().loadUserProfile(getSessionWrapper().getLoggedInUserProfileId(), true);

		if (userProfile == null) {
			logger.log(Level.WARNING, "Could delete photo: no user found for  " + getSessionWrapper().getLoggedInUserProfileId()
					+ "This is very weird! => redirecting to home page");
			controllers.Application.index();
		}

		BeanProvider.getProfilePhotoService().removeProfilePhoto(userProfile, deletedPhotoIdx);
		profilePhotos();
	}

	public static void doUpdatePhotoCaption(int photoIndex, String photoCaption) {

		// TODO: security: role check here
		UserProfile userProfile = BeanProvider.getUserProfileService().loadUserProfile(getSessionWrapper().getLoggedInUserProfileId(), true);

		if (userProfile == null) {
			logger.log(Level.WARNING, "Could edit caption: no user found for  " + getSessionWrapper().getLoggedInUserProfileId()
					+ "This is very weird! => redirecting to home page");
			controllers.Application.index();
		}

		BeanProvider.getProfilePhotoService().updatePhotoCaption(userProfile, photoIndex, photoCaption);

		logger.log(Level.INFO, "rendeing profie");
		renderJSON(new MappedValue("response", "ok"));
	}

	public static void doSetAsMainPhoto(int photoIndex) {

		// TODO: security: role check here
		UserProfile userProfile = BeanProvider.getUserProfileService().loadUserProfile(getSessionWrapper().getLoggedInUserProfileId(), true);

		if (userProfile == null) {
			logger.log(Level.WARNING, "Could not set photo as main photo: no user found for  " + getSessionWrapper().getLoggedInUserProfileId()
					+ "This is very weird! => redirecting to home page");
			controllers.Application.index();
		}

		BeanProvider.getProfilePhotoService().movePhotoToFirstPosition(userProfile, photoIndex);

		profilePhotos();

	}

}
