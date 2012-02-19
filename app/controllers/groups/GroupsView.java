package controllers.groups;

import web.utils.Utils;
import models.altemotif.groups.GroupViewVisibility;

import com.svend.dab.core.beans.groups.GroupPep;
import com.svend.dab.core.beans.groups.ProjectGroup;

import controllers.Application;
import controllers.BeanProvider;
import controllers.DabLoggedController;
import controllers.profile.ProfileHome;

public class GroupsView extends DabLoggedController {

	public static void groupsView(String groupid) {

		ProjectGroup group = BeanProvider.getGroupService().loadGroupById(groupid, true);

		if (group == null || ! group.isActive()) {
			Application.index();
		} else {

			renderArgs.put("groupViewVisibility", new GroupViewVisibility(new GroupPep(group), getSessionWrapper().getLoggedInUserProfileId()));
			renderArgs.put("visitedGroup", group);

			render();
		}
	}

	public static void closeGroup(String groupId) {

		ProjectGroup group = BeanProvider.getGroupService().loadGroupById(groupId, true);

		if (group == null) {
			Application.index();
		} else {
			GroupPep pep = new GroupPep(group);
			if (pep.isUserAllowedToCloseGroup(getSessionWrapper().getLoggedInUserProfileId())) {
				BeanProvider.getGroupService().closeGroup(groupId);
				Utils.waitABit();
				ProfileHome.profileHome();
			} else {
				Application.index();
			}
		}
	}

}
