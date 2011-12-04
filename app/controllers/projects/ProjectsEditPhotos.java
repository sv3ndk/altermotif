package controllers.projects;

import static controllers.errors.UploadError.SESSION_ATTR_ERROR_MESSAGE_KEY;
import static controllers.errors.UploadError.SESSION_ATTR_SUGGESTED_NAVIGATION;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import play.mvc.Router;

import com.svend.dab.core.beans.DabUploadFailedException;
import com.svend.dab.core.beans.profile.UserProfile;
import com.svend.dab.core.beans.projects.Project;
import com.svend.dab.core.beans.projects.ProjectPep;

import controllers.Application;
import controllers.BeanProvider;
import controllers.DabLoggedController;
import controllers.errors.UploadError;

/**
 * @author svend
 * 
 */
public class ProjectsEditPhotos extends DabLoggedController {

	private static Logger logger = Logger.getLogger(ProjectsEditPhotos.class.getName());
	
	private final static String FLASH_EDITED_PROJECT_ID ="prjid";
	
	
	/**
	 * @param p
	 */
	public static void projectsEditPhotos(String p) {

		Project project = BeanProvider.getProjectService().loadProject(p, true);

		if (project != null) {
			ProjectPep pep = new ProjectPep(project);
			if (pep.isAllowedToEditPhotoGallery(getSessionWrapper().getLoggedInUserProfileId())) {

				flash.put(FLASH_EDITED_PROJECT_ID, p);
				renderArgs.put("editedProject", project);
				renderArgs.put("uploadPhotoLinkActive", !project.isPhotoPackFull());

				render();

			} else {
				Application.index();
			}

		} else {
			Application.index();
		}
	}

	
	
	/**
	 * @param theFile
	 */
	public static void doUploadPhoto(File theFile) {
		
		try {
			// TODO: security check for this user here
			
			BeanProvider.getProjectPhotoService().addOnePhoto(flash.get(FLASH_EDITED_PROJECT_ID), theFile);
			projectsEditPhotos(flash.get(FLASH_EDITED_PROJECT_ID));
			
		} catch (DabUploadFailedException e) {
			flash.put(SESSION_ATTR_SUGGESTED_NAVIGATION, Router.reverse("profile.ProjectsEditPhotos.projectsEditPhotos(" + flash.get(FLASH_EDITED_PROJECT_ID)) + ")");

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
			flash.put(SESSION_ATTR_SUGGESTED_NAVIGATION, Router.reverse("profile.ProjectsEditPhotos.projectsEditPhotos(" + flash.get(FLASH_EDITED_PROJECT_ID)) + ")");
			flash.put(SESSION_ATTR_ERROR_MESSAGE_KEY, DabUploadFailedException.failureReason.technicalError.getErrorMessageKey());
			UploadError.uploadError();
		}


		
	}
	
	/**
	 * @param deletedPhotoIdx
	 */
	public static void doDeletePhoto(int deletedPhotoIdx) {

		UserProfile userProfile = BeanProvider.getUserProfileService().loadUserProfile(getSessionWrapper().getLoggedInUserProfileId(), true);
		if (userProfile == null) {
			logger.log(Level.WARNING, "Could delete photo: no user found for  " + getSessionWrapper().getLoggedInUserProfileId() + "This is very weird! => redirecting to home page");
			controllers.Application.index();
		}
		// TODO: security check for this user here
		
		Project project = BeanProvider.getProjectService().loadProject(flash.get(FLASH_EDITED_PROJECT_ID), false);
		
		if (project == null) {
			logger.log(Level.WARNING, "Could delete photo: no project  found for  project" + flash.get(FLASH_EDITED_PROJECT_ID) + "This is very weird! => redirecting to home page");
			controllers.Application.index();
		}

		BeanProvider.getProjectPhotoService().removeProjectPhoto(project, deletedPhotoIdx);
		projectsEditPhotos(flash.get(FLASH_EDITED_PROJECT_ID));
		
	}

	
	
	/**
	 * @param photoIndex
	 * @param photoCaption
	 */
	public static void doUpdatePhotoCaption(int photoIndex, String photoCaption) {

		UserProfile userProfile = BeanProvider.getUserProfileService().loadUserProfile(getSessionWrapper().getLoggedInUserProfileId(), true);
		if (userProfile == null) {
			logger.log(Level.WARNING, "Could update caption for photo: no user found for  " + getSessionWrapper().getLoggedInUserProfileId() + "This is very weird! => redirecting to home page");
			controllers.Application.index();
		}
		// TODO: security check for this user here
		
		Project project = BeanProvider.getProjectService().loadProject(flash.get(FLASH_EDITED_PROJECT_ID), false);
		
		if (project == null) {
			logger.log(Level.WARNING, "Could update caption for: no project  found for  " + flash.get(FLASH_EDITED_PROJECT_ID) + "This is very weird! => redirecting to home page");
			controllers.Application.index();
		}
		
		BeanProvider.getProjectPhotoService().replacePhotoCaption(project, photoIndex, photoCaption);
		flash.keep(FLASH_EDITED_PROJECT_ID);
		
	}
	
	
	
	
	public static void doSetAsMainPhoto(int photoIndex) {
		
		UserProfile userProfile = BeanProvider.getUserProfileService().loadUserProfile(getSessionWrapper().getLoggedInUserProfileId(), true);
		if (userProfile == null) {
			logger.log(Level.WARNING, "Could set as main photo: no user found for  " + getSessionWrapper().getLoggedInUserProfileId() + "This is very weird! => redirecting to home page");
			controllers.Application.index();
		}
		// TODO: security check for this user here
		
		Project project = BeanProvider.getProjectService().loadProject(flash.get(FLASH_EDITED_PROJECT_ID), false);
		
		if (project == null) {
			logger.log(Level.WARNING, "Could set as main photo: no project  found for  " + flash.get(FLASH_EDITED_PROJECT_ID) + "This is very weird! => redirecting to home page");
			controllers.Application.index();
		}
		
		BeanProvider.getProjectPhotoService().putPhotoInFirstPositio(project, photoIndex);
		projectsEditPhotos(flash.get(FLASH_EDITED_PROJECT_ID));
	}

}
