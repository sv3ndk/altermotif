package controllers.groups;

import static controllers.errors.UploadError.SESSION_ATTR_ERROR_MESSAGE_KEY;
import static controllers.errors.UploadError.SESSION_ATTR_SUGGESTED_NAVIGATION;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import play.mvc.Router;

import com.svend.dab.core.beans.DabUploadFailedException;
import com.svend.dab.core.beans.groups.GroupPep;
import com.svend.dab.core.beans.groups.ProjectGroup;

import controllers.Application;
import controllers.BeanProvider;
import controllers.DabLoggedController;
import controllers.errors.UploadError;

public class GroupsEditPhotos extends DabLoggedController {

	
	private final static String FLASH_EDITED_GROUP_ID ="gid";
	
	private static Logger logger = Logger.getLogger(GroupsEditPhotos.class.getName());

	
	public static void groupsEditPhotos(String groupId) {
		
		ProjectGroup group = BeanProvider.getGroupService().loadGroupById(groupId, true);
		
		if (group != null) {
			
			if (new GroupPep(group).isAllowedToEditPhotoGallery(getSessionWrapper().getLoggedInUserProfileId())) {
				
				flash.put(FLASH_EDITED_GROUP_ID, groupId);

				renderArgs.put("editedGroup", group);
				renderArgs.put("uploadPhotoLinkActive", !group.getPhotoAlbum().isFull());

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
			
			BeanProvider.getGroupPhotoService().addOnePhoto(flash.get(FLASH_EDITED_GROUP_ID), theFile);
			groupsEditPhotos(flash.get(FLASH_EDITED_GROUP_ID));
			
		} catch (DabUploadFailedException e) {
			flash.put(SESSION_ATTR_SUGGESTED_NAVIGATION, Router.reverse("groups.GroupsEditPhotos.groupsEditPhotos(" + flash.get(FLASH_EDITED_GROUP_ID)) + ")");

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
			flash.put(SESSION_ATTR_SUGGESTED_NAVIGATION, Router.reverse("groups.GroupsEditPhotos.groupsEditPhotos(" + flash.get(FLASH_EDITED_GROUP_ID)) + ")");
			flash.put(SESSION_ATTR_ERROR_MESSAGE_KEY, DabUploadFailedException.failureReason.technicalError.getErrorMessageKey());
			UploadError.uploadError();
		}
		

	}
	
	/**
	 * @param deletedPhotoIdx
	 */
	public static void doDeletePhoto(int deletedPhotoIdx) {
		
		// TODO: security check for this user here
		ProjectGroup group = BeanProvider.getGroupService().loadGroupById(flash.get(FLASH_EDITED_GROUP_ID), false);
		
		if (group == null) {
			logger.log(Level.WARNING, "Could delete photo: no group  found for  project" + flash.get(FLASH_EDITED_GROUP_ID) + "This is very weird! => redirecting to home page");
			controllers.Application.index();
		}

		BeanProvider.getGroupPhotoService().removePhoto(group, deletedPhotoIdx);
		groupsEditPhotos(flash.get(FLASH_EDITED_GROUP_ID));

	}
	
	/**
	 * @param photoIndex
	 * @param photoCaption
	 */
	public static void doUpdatePhotoCaption(int photoIndex, String photoCaption) {

		// TODO: security check for this user here
		ProjectGroup group = BeanProvider.getGroupService().loadGroupById(flash.get(FLASH_EDITED_GROUP_ID), false);
		
		if (group == null) {
			logger.log(Level.WARNING, "Could update photo caption: no group  found for  project" + flash.get(FLASH_EDITED_GROUP_ID) + "This is very weird! => redirecting to home page");
			controllers.Application.index();
		}
		
		BeanProvider.getGroupPhotoService().replacePhotoCaption(group, photoIndex, photoCaption);
		groupsEditPhotos(flash.get(FLASH_EDITED_GROUP_ID));
		
	}
	
	public static void doSetAsMainPhoto(int photoIndex) {
		
		// TODO: security check for this user here
		ProjectGroup group = BeanProvider.getGroupService().loadGroupById(flash.get(FLASH_EDITED_GROUP_ID), false);
		
		if (group == null) {
			logger.log(Level.WARNING, "Could set photo in first position: no group  found for  project" + flash.get(FLASH_EDITED_GROUP_ID) + "This is very weird! => redirecting to home page");
			controllers.Application.index();
		}
		
		BeanProvider.getGroupPhotoService().putPhotoInFirstPositio(group, photoIndex);;
		groupsEditPhotos(flash.get(FLASH_EDITED_GROUP_ID));
	}

}
