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
import com.svend.dab.web.upload.IUploadProcessor;

import controllers.BeanProvider;
import controllers.DabLoggedController;
import controllers.errors.UploadError;

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

		// we may upload more photos only if there is less then 20 or the list is still null
		boolean isActive =  userProfile.getPhotos() == null || userProfile.getPhotos().size() < MAX_NUMBER_OF_PHOTOS;
		renderArgs.put("uploadPhotoLinkActive", isActive);

		render();
	}

	public static void doUploadPhoto(File theFile) {

		try {
			BeanProvider.getProfilePhotoService().addOnePhoto(getSessionWrapper().getLoggedInUserProfileId(), theFile);
			profilePhotos();
		} catch (DabUploadFailedException e) {

			flash.put(SESSION_ATTR_SUGGESTED_NAVIGATION, Router.reverse("profile.ProfilePhotos.profilePhotos"));

			if (e.getReason() == null) {
				// this should never happen, defaulting to a generic error message
				logger.log(Level.SEVERE, "Could not process uploaded request, but the exception for the failed upload does not contain a reason, this is weird...", e);
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

		UserProfile userProfile = BeanProvider.getUserProfileService().loadUserProfile(getSessionWrapper().getLoggedInUserProfileId(), true);

		if (userProfile == null) {
			logger.log(Level.WARNING, "Could delete photo: no user found for  " + getSessionWrapper().getLoggedInUserProfileId() + "This is very weird! => redirecting to home page");
			controllers.Application.index();
		}

		BeanProvider.getProfilePhotoService().removeProfilePhoto(userProfile, deletedPhotoIdx);
		profilePhotos();
	}

	
	
	
	public static void doUpdatePhotoCaption(int profilePhotoIndex, String profilePhotoCaption) {

		UserProfile userProfile = BeanProvider.getUserProfileService().loadUserProfile(getSessionWrapper().getLoggedInUserProfileId(), true);
		
		if (userProfile == null) {
			logger.log(Level.WARNING, "Could edit caption: no user found for  " + getSessionWrapper().getLoggedInUserProfileId() + "This is very weird! => redirecting to home page");
			controllers.Application.index();
		}
		
		BeanProvider.getProfilePhotoService().updatePhotoCaption(userProfile, profilePhotoIndex, profilePhotoCaption);

		logger.log(Level.INFO, "rendeing profie");
		renderJSON(new MappedValue("response", "ok"));
	}
	
	public static void doSetAsMainPhoto(int photoIndex) {
		// TODO
		
	}


}
