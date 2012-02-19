package controllers.groups;

import web.utils.Utils;
import models.altemotif.groups.GroupViewVisibility;
import models.altermotif.BinaryResponse;

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
	
	
	public static void applyToGroup(String groupId, String applicationText) {
		
		ProjectGroup group = BeanProvider.getGroupService().loadGroupById(groupId, true);
		
		if (group == null) {
			renderJSON(new BinaryResponse(false));
		} else {
			GroupPep pep = new GroupPep(group);
			if (pep.isUserAllowedToApplyToGroup((getSessionWrapper().getLoggedInUserProfileId()))) {
				BeanProvider.getGroupService().applyToGroup(groupId, getSessionWrapper().getLoggedInUserProfileId());
				renderJSON(new BinaryResponse(true));
			} else {
				renderJSON(new BinaryResponse(false));
			}
		}
	}
	
	public static void cancelApplyToGroup(String groupId) {
		ProjectGroup group = BeanProvider.getGroupService().loadGroupById(groupId, true);
		if (group == null || ! group.hasAppliedForGroupMembership(getSessionWrapper().getLoggedInUserProfileId())) {
			renderJSON(new BinaryResponse(false));
		} else {
			BeanProvider.getGroupService().cancelUserApplicationToGroup(groupId, getSessionWrapper().getLoggedInUserProfileId());
			renderJSON(new BinaryResponse(true));
		}
	}
	
	
	public static void acceptUserApplicationToGroup(String groupId, String applicantId) {
		ProjectGroup group = BeanProvider.getGroupService().loadGroupById(groupId, true);
		
		if (group == null) {
			renderJSON(new BinaryResponse(false));
		} else {
			GroupPep pep = new GroupPep(group);
			if (pep.isUserAllowedToAcceptAndRejectUserApplications(getSessionWrapper().getLoggedInUserProfileId())) {
				BeanProvider.getGroupService().acceptUserApplicationToGroup(groupId, applicantId);
			} else {
				renderJSON(new BinaryResponse(false));
			}
		}
		
	}
	
	public static void rejectUserApplicationToGroup(String groupId, String applicantId) {
		ProjectGroup group = BeanProvider.getGroupService().loadGroupById(groupId, true);
		
		if (group == null) {
			renderJSON(new BinaryResponse(false));
		} else {
			GroupPep pep = new GroupPep(group);
			if (pep.isUserAllowedToAcceptAndRejectUserApplications(getSessionWrapper().getLoggedInUserProfileId())) {
				BeanProvider.getGroupService().cancelUserApplicationToGroup(groupId, applicantId);
			} else {
				renderJSON(new BinaryResponse(false));
			}
		}
		
	}
	
	

}
