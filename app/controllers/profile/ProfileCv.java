package controllers.profile;


import static controllers.errors.UploadError.SESSION_ATTR_ERROR_MESSAGE_KEY;
import static controllers.errors.UploadError.SESSION_ATTR_SUGGESTED_NAVIGATION;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import play.mvc.Router;

import models.altermotif.profile.ProfileManageCvVisibility;

import com.svend.dab.core.UserProfileService;
import com.svend.dab.core.beans.DabUploadFailedException;
import com.svend.dab.core.beans.profile.UserProfile;
import com.svend.dab.web.upload.IUploadProcessor;

import controllers.BeanProvider;
import controllers.DabLoggedController;
import controllers.errors.UploadError;

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
	
	
	

	public static void uploadCv(File theFile) {


		try {

			IUploadProcessor uploadProcessor = BeanProvider.getUploadProcessor();
			uploadProcessor.processUploadCvRequest(theFile, getSessionWrapper().getLoggedInUserProfileId());
			profileCv();
		} catch (DabUploadFailedException e) {

			flash.put(SESSION_ATTR_SUGGESTED_NAVIGATION, Router.reverse("profile.ProfileCv.profileCv"));

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
			flash.put(SESSION_ATTR_SUGGESTED_NAVIGATION, Router.reverse("profile.ProfileCv.profileCv()"));
			flash.put(SESSION_ATTR_ERROR_MESSAGE_KEY, DabUploadFailedException.failureReason.technicalError.getErrorMessageKey());
			UploadError.uploadError();
		}


	}
	
	
	public static void deleteCv() {
	
		UserProfile userProfile = BeanProvider.getUserProfileService().loadUserProfile(getSessionWrapper().getLoggedInUserProfileId(), true);
		if (userProfile == null) {
			logger.log(Level.WARNING, "Could not load profile for supposedly logged in user " + getSessionWrapper().getLoggedInUserProfileId() + " => cannot remove cv => redirecting to home page");
			controllers.Application.index();
		}
		
		BeanProvider.getUserProfileService().removeCv(userProfile);

		profileCv();
		
	}
	

}
