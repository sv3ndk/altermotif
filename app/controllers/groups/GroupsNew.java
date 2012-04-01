/**
 * 
 */
package controllers.groups;

import com.svend.dab.core.beans.groups.ProjectGroup;

import play.data.validation.Validation;
import models.altemotif.groups.EditedGroup;
import web.utils.Utils;
import controllers.BeanProvider;
import controllers.DabLoggedController;
import controllers.profile.ProfileHome;
import controllers.validators.DabValidators;

/**
 * @author svend
 *
 */
public class GroupsNew extends DabLoggedController {

	public static void groupsNew() {
		Utils.addThemesToRenderArgs(getSessionWrapper(), renderArgs);
		Utils.addJsonThemesToRenderArgs(getSessionWrapper(), renderArgs, getSessionWrapper().getSelectedLg());
		render();
	}
	
	
	public static void doCreateGroup(EditedGroup editedGroup) {
		
		DabValidators.validateEditedGroup(editedGroup, validation, flash);
		
		if (Validation.hasErrors()) {
			params.flash();
			Validation.keep();
			groupsNew();
		} else {
			ProjectGroup createdGroup = new ProjectGroup();
			editedGroup.applyToGroup(createdGroup);
			BeanProvider.getGroupService().createNewGroup(createdGroup, getSessionWrapper().getLoggedInUserProfileId());
			Utils.waitABit();
			ProfileHome.profileHome();
		}
	}
	
	
}
