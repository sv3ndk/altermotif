package controllers.groups;

import play.data.validation.Validation;
import models.altemotif.groups.EditedGroup;

import com.svend.dab.core.beans.groups.GroupPep;
import com.svend.dab.core.beans.groups.ProjectGroup;

import web.utils.Utils;
import controllers.Application;
import controllers.BeanProvider;
import controllers.DabLoggedController;
import controllers.profile.ProfileHome;
import controllers.validators.DabValidators;

/**
 * @author svend
 *
 */
public class GroupsEdit extends DabLoggedController{
	
	
	// we normally load the group data when we navigate to the page, unless we are navigating due to error in a previous edition submission
	public final static String FLASH_SKIP_LOADING_GROUP ="skipLd";
	public final static String FLASH_GROUP_ID ="gid";
	
	public static void groupsEdit(String gid) {
		
		// even if we should skip load for edition reason, we still need to load the real group for the security check
		ProjectGroup group = BeanProvider.getGroupService().loadGroupById(gid, false);
		
		if (group == null  || ! group.isActive()) {
			// group does not exist!
			Application.index();
		} else {
			GroupPep pep = new GroupPep(group);
			if (!pep.isUserAllowedToEditGroup(getSessionWrapper().getLoggedInUserProfileId())) {
				// user not allowed to edit this group!
				Application.index();
			} else {
				
				if (!flash.contains(FLASH_SKIP_LOADING_GROUP)) {
					renderArgs.put("editedGroup", new EditedGroup(group));
				}
				
				
				Utils.addProjectThemesToRenderArgs(getSessionWrapper(), renderArgs);
				Utils.addProjectJsonThemesToRenderArgs(getSessionWrapper(), renderArgs, getSessionWrapper().getSelectedLg());
				flash.put(FLASH_GROUP_ID, gid);
				render();
			}
		}
	}
	
	
	
	public static void doEditGroup(EditedGroup editedGroup) {
		
		DabValidators.validateEditedGroup(editedGroup, validation, flash);
		
		if (Validation.hasErrors()) {
			params.flash();
			Validation.keep();
			
			flash.put(FLASH_SKIP_LOADING_GROUP, true);
			groupsEdit(flash.get(FLASH_GROUP_ID));
		} else {
			
			ProjectGroup updated = BeanProvider.getGroupService().loadGroupById(flash.get(FLASH_GROUP_ID), false);
			if (updated != null) {
				
				GroupPep pep = new GroupPep(updated);
				if (pep.isUserAllowedToEditGroup(getSessionWrapper().getLoggedInUserProfileId())) {
					editedGroup.applyToGroup(updated);
					BeanProvider.getGroupService().updateGroupData(updated);
					Utils.waitABit();
				}
			}
				
			ProfileHome.profileHome();
		}

		
	}
	
}
