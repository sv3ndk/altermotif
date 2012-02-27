package controllers.groups;

import java.util.LinkedList;
import java.util.List;

import models.altemotif.groups.GroupViewVisibility;
import models.altermotif.BinaryResponse;
import models.altermotif.GroupsViewParticipantActionOutcome;
import web.utils.Utils;

import com.svend.dab.core.beans.groups.GroupParticipant;
import com.svend.dab.core.beans.groups.GroupParticipant.ROLE;
import com.svend.dab.core.beans.groups.GroupPep;
import com.svend.dab.core.beans.groups.ProjectGroup;
import com.svend.dab.core.beans.profile.UserProfile;
import com.svend.dab.core.beans.profile.UserSummary;
import com.svend.dab.core.beans.projects.Participation;

import controllers.Application;
import controllers.BeanProvider;
import controllers.DabController;
import controllers.profile.ProfileHome;

public class GroupsView extends DabController {

	public static void groupsView(String groupid) {

		ProjectGroup group = BeanProvider.getGroupService().loadGroupById(groupid, true);

		if (group == null || !group.isActive()) {
			Application.index();
		} else {

			GroupViewVisibility visibility = new GroupViewVisibility(new GroupPep(group), getSessionWrapper().getLoggedInUserProfileId());
			renderArgs.put("groupViewVisibility", visibility);
			renderArgs.put("visitedGroup", group);
			renderArgs.put("loggedInUserId", getSessionWrapper().getLoggedInUserProfileId());

			if (getSessionWrapper().isLoggedIn()) {
				UserProfile user = BeanProvider.getUserProfileService().loadUserProfile(getSessionWrapper().getLoggedInUserProfileId(), false);
				if (user != null) {
					List<Participation> projects = user.getAllProjectsWhereUserIsAdmin(getSessionWrapper().getLoggedInUserProfileId());
					
					List<Participation> filteredProject = new LinkedList<Participation>();
					for (Participation participation : projects) {
						if (!group.isProjectMemberOfGroupOrHasAlreadyApplied(participation.getProjectSummary().getProjectId())) {
							filteredProject.add(participation);
						}
					}

					visibility.addProjectsWhereUserIsAdmin(filteredProject);
					renderArgs.put("projectsWhereUserIsAdmin", filteredProject);
				}
			}

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
		if (group == null || !group.hasAppliedForGroupMembership(getSessionWrapper().getLoggedInUserProfileId())) {
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

				// building response with updated user rights
				group.addParticipant(new GroupParticipant(ROLE.member, new UserSummary(applicantId, null, null, true)));
				renderOutcome(group, applicantId);

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

	public static void leaveGroup(String groupId) {
		ProjectGroup group = BeanProvider.getGroupService().loadGroupById(groupId, true);

		if (group == null) {
			renderJSON(new BinaryResponse(false));
		} else {
			GroupPep pep = new GroupPep(group);
			if (pep.isUserAllowedToLeaveGroup(getSessionWrapper().getLoggedInUserProfileId())) {
				BeanProvider.getGroupService().removeUserFromGroup(groupId, getSessionWrapper().getLoggedInUserProfileId());
			} else {
				renderJSON(new BinaryResponse(false));
			}
		}
	}

	public static void makeAdmin(String groupId, String upgradedUser) {
		ProjectGroup group = BeanProvider.getGroupService().loadGroupById(groupId, true);

		if (group == null) {
			renderJSON(new GroupsViewParticipantActionOutcome(false));
		} else {
			GroupPep pep = new GroupPep(group);
			if (pep.isUserAllowedToMakeAdmin(getSessionWrapper().getLoggedInUserProfileId(), upgradedUser)) {
				BeanProvider.getGroupService().updateUserParticipantRole(groupId, upgradedUser, ROLE.admin);

				// building response with updated user rights
				group.updateUserParticipantRole(upgradedUser, ROLE.admin);
				renderOutcome(group, upgradedUser);

			} else {
				renderJSON(new GroupsViewParticipantActionOutcome(false));
			}
		}
	}

	public static void makeMember(String groupId, String downgradedUser) {
		ProjectGroup group = BeanProvider.getGroupService().loadGroupById(groupId, true);

		if (group == null) {
			renderJSON(new GroupsViewParticipantActionOutcome(false));
		} else {
			GroupPep pep = new GroupPep(group);
			if (pep.isUserAllowedToMakeMember(getSessionWrapper().getLoggedInUserProfileId(), downgradedUser)) {
				BeanProvider.getGroupService().updateUserParticipantRole(groupId, downgradedUser, ROLE.member);

				// building response with updated user rights
				group.updateUserParticipantRole(downgradedUser, ROLE.member);
				renderOutcome(group, downgradedUser);

			} else {
				renderJSON(new GroupsViewParticipantActionOutcome(false));
			}
		}
	}

	public static void removeMember(String groupId, String removedUser) {
		ProjectGroup group = BeanProvider.getGroupService().loadGroupById(groupId, true);

		if (group == null) {
			renderJSON(new GroupsViewParticipantActionOutcome(false));
		} else {
			GroupPep pep = new GroupPep(group);
			if (pep.isUserAllowedToRemoveUser(getSessionWrapper().getLoggedInUserProfileId(), removedUser)) {
				BeanProvider.getGroupService().removeUserFromGroup(groupId, removedUser);

				// building response with updated user rights
				group.removeParticipant(removedUser);
				renderOutcome(group, removedUser);

			} else {
				renderJSON(new GroupsViewParticipantActionOutcome(false));
			}
		}
	}

	public static void applyToGroupWithProject(String groupId, String projectId, String applicationText) {

		if (getSessionWrapper().isLoggedIn()) {
			BeanProvider.getGroupService().applyToGroupWithProject(getSessionWrapper().getLoggedInUserProfileId(), groupId, projectId, applicationText);
			
			renderJSON(new GroupsViewParticipantActionOutcome(true));
		} else {
			renderJSON(new GroupsViewParticipantActionOutcome(false));
		}
	}
	
	
	public static void rejectProjectApplicationToGroup(String groupId, String projectId) {
		
		ProjectGroup group = BeanProvider.getGroupService().loadGroupById(groupId, true);

		if (group == null) {
			renderJSON(new GroupsViewParticipantActionOutcome(false));
		} else {
			GroupPep pep = new GroupPep(group);
			if (pep.isUserAllowedToAcceptAndRejectProjectApplications(getSessionWrapper().getLoggedInUserProfileId())) {
				BeanProvider.getGroupService().rejectProjectApplication(groupId, projectId);
				renderJSON(new GroupsViewParticipantActionOutcome(true));
				
			} else {
				renderJSON(new GroupsViewParticipantActionOutcome(false));
			}
		}
		
	}

	// ////////////////////////////
	// /

	private static void renderOutcome(ProjectGroup group, String otherUserId) {
		GroupsViewParticipantActionOutcome outcome = new GroupsViewParticipantActionOutcome(true);
		GroupViewVisibility visibility = new GroupViewVisibility(new GroupPep(group), getSessionWrapper().getLoggedInUserProfileId());

		outcome.setLoggedInUser_makeMemberLinkVisible(visibility.isMakeMemberLinkVisible(getSessionWrapper().getLoggedInUserProfileId()));
		outcome.setLoggedInUser_makeAdminLinkVisible(visibility.isMakeAdminLinkVisible(getSessionWrapper().getLoggedInUserProfileId()));
		outcome.setLoggedInUser_leaveLinkVisible(visibility.isLeaveLinkVisible(getSessionWrapper().getLoggedInUserProfileId()));

		outcome.setOtherUser_makeMemberLinkVisible(visibility.isMakeMemberLinkVisible(otherUserId));
		outcome.setOtherUser_makeAdminLinkVisible(visibility.isMakeAdminLinkVisible(otherUserId));
		outcome.setOtherUser_leaveLinkVisible(visibility.isLeaveLinkVisible(otherUserId));
		outcome.setOtherUser_removeUserLinkVisible(visibility.isRemoveMemberLinkVisible(otherUserId));

		outcome.setApplyToGroupWithProjectLinkVisisble(visibility.isApplyWithProjetLinkVisible());
		
		renderJSON(outcome);
	}

}
