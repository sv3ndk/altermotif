package controllers.groups;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import models.altemotif.groups.GroupViewVisibility;
import models.altermotif.BinaryResponse;
import models.altermotif.GroupsViewParticipantActionOutcome;
import models.altermotif.MappedValue;
import play.mvc.Router;
import web.utils.Utils;

import com.svend.dab.core.beans.ForumThread;
import com.svend.dab.core.beans.groups.GroupParticipant;
import com.svend.dab.core.beans.groups.GroupParticipant.ROLE;
import com.svend.dab.core.beans.groups.GroupPep;
import com.svend.dab.core.beans.groups.GroupProjectParticipant;
import com.svend.dab.core.beans.groups.ProjectGroup;
import com.svend.dab.core.beans.profile.UserProfile;
import com.svend.dab.core.beans.profile.UserSummary;
import com.svend.dab.core.beans.projects.Participation;

import controllers.Application;
import controllers.BeanProvider;
import controllers.DabController;
import controllers.profile.ProfileHome;

public class GroupsView extends DabController {

	private static Logger logger = Logger.getLogger(GroupsView.class.getName());

	public static void groupsView(String groupid) {

		ProjectGroup group = BeanProvider.getGroupService().loadGroupById(groupid, true);

		if (group == null || !group.isActive()) {
			Application.index();
		} else {

			GroupViewVisibility visibility = new GroupViewVisibility(new GroupPep(group), getSessionWrapper().getLoggedInUserProfileId());
			renderArgs.put("groupViewVisibility", visibility);
			renderArgs.put("visitedGroup", group);
			renderArgs.put("loggedInUserId", getSessionWrapper().getLoggedInUserProfileId());
			renderArgs.put("allThreads", BeanProvider.getForumThreadDao().loadGroupForumThreads(groupid));

			List<Participation> allProjectWhereUserIsAdmin = computeFilteredListOfProjectWhereUserIsAdmin();
			visibility.addProjectsWhereUserIsAdmin(allProjectWhereUserIsAdmin);
			renderArgs.put("projectsWhereUserIsAdmin", filterProjectAltreadyInGroup(group, allProjectWhereUserIsAdmin));

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
				renderJSON(buildOutcome(group, applicantId, null));
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
				renderJSON(buildOutcome(group, upgradedUser, null));
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
				renderJSON(buildOutcome(group, downgradedUser, null));
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
				renderJSON(buildOutcome(group, removedUser, null));
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
		if (group == null || projectId == null) {
			renderJSON(new GroupsViewParticipantActionOutcome(false));
		} else {
			GroupPep pep = new GroupPep(group);
			if (pep.isUserAllowedToAcceptAndRejectProjectApplications(getSessionWrapper().getLoggedInUserProfileId())) {
				BeanProvider.getGroupService().rejectProjectApplication(groupId, projectId);

				Utils.waitABit();
				ProjectGroup updatedGroup = BeanProvider.getGroupService().loadGroupById(groupId, true);
				renderJSON(buildOutcome(updatedGroup, null, projectId));

			} else {
				renderJSON(new GroupsViewParticipantActionOutcome(false));
			}
		}
	}

	public static void removeProjectFromGroup(String groupId, String projectId) {

		ProjectGroup group = BeanProvider.getGroupService().loadGroupById(groupId, true);
		if (group == null) {
			renderJSON(new GroupsViewParticipantActionOutcome(false));
		} else {
			GroupProjectParticipant removeProjectParticipant = group.findProjectParticipant(projectId);
			UserProfile user = BeanProvider.getUserProfileService().loadUserProfile(getSessionWrapper().getLoggedInUserProfileId(), false);
			if (removeProjectParticipant != null && user != null) {
				List<Participation> projectsWhereThisUserIsAdmin = user.getAllProjectsWhereUserIsAdmin(getSessionWrapper().getLoggedInUserProfileId());
				GroupPep pep = new GroupPep(group);
				if (pep.isUserAllowedToRemoveProjectFromGroup(getSessionWrapper().getLoggedInUserProfileId(), removeProjectParticipant.getProjet(),
						projectsWhereThisUserIsAdmin)) {
					BeanProvider.getGroupService().removeProjectFromGroup(groupId, projectId);

					Utils.waitABit();
					ProjectGroup updatedGroup = BeanProvider.getGroupService().loadGroupById(groupId, true);
					renderJSON(buildOutcome(updatedGroup, null, projectId));
				} else {
					renderJSON(new GroupsViewParticipantActionOutcome(false));
				}
			} else {
				renderJSON(new GroupsViewParticipantActionOutcome(false));
			}
		}
	}

	public static void acceptProjectApplicationToGroup(String groupId, String projectId) {

		ProjectGroup group = BeanProvider.getGroupService().loadGroupById(groupId, true);
		if (group == null) {
			renderJSON(new GroupsViewParticipantActionOutcome(false));
		} else {
			GroupPep pep = new GroupPep(group);
			if (pep.isUserAllowedToAcceptAndRejectProjectApplications(getSessionWrapper().getLoggedInUserProfileId())) {
				BeanProvider.getGroupService().acceptProjectApplication(groupId, projectId);
				renderJSON(new GroupsViewParticipantActionOutcome(true));

			} else {
				renderJSON(new GroupsViewParticipantActionOutcome(false));
			}
		}
	}

	// ///////////////////////////////////////
	// forum and thread

	/**
	 * @param ownerId
	 * @param threadTitle
	 */
	public static void doAddThread(String ownerId, String threadTitle, boolean isThreadPublic) {
		ProjectGroup group = BeanProvider.getGroupService().loadGroupById(ownerId, true);
		if (group != null) {
			GroupPep pep = new GroupPep(group);
			if (pep.isUserAllowedToAddThreadToForum(getSessionWrapper().getLoggedInUserProfileId())) {

				ForumThread createdThread = BeanProvider.getForumThreadDao().createNewThread(
						new ForumThread(null, ownerId, threadTitle, new Date(), 0, isThreadPublic));

				createdThread.setMayUserDeleteThisThread(pep.isAllowedToDeleteThread(getSessionWrapper().getLoggedInUserProfileId()));
				createdThread.setMayUserUpdateVisibility(pep.isAllowedToUpdateVisibilityThread(getSessionWrapper().getLoggedInUserProfileId()));
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("t", createdThread.getId());

				createdThread.setThreadUrl(Router.reverse("forum.ForumThreadView.groupForumThread", params).url);

				renderJSON(createdThread);
			} else {
				logger.log(Level.WARNING, "user trying to add a thread to a groupId but is not allowed to: projectId:" + ownerId + "userid:"
						+ getSessionWrapper().getLoggedInUserProfileId());
			}
		} else {
			logger.log(Level.WARNING, "user trying to add a thread to a non existant group : " + ownerId + " this should be impossible!");
		}
	}

	public static void changeThreadVisibility(String ownerId, String threadId, boolean isThreadPublic) {
		ProjectGroup group = BeanProvider.getGroupService().loadGroupById(ownerId, true);
		if (group != null) {
			if (new GroupPep(group).isAllowedToUpdateVisibilityThread(getSessionWrapper().getLoggedInUserProfileId())) {
				BeanProvider.getForumThreadDao().updateThreadVisibility(threadId, isThreadPublic);
				renderJSON(BeanProvider.getForumThreadDao().getThreadById(threadId));
			} else {
				logger.log(Level.WARNING, "user trying to change visibility of a group thread but is not allowed to: groupId:" + ownerId + ", threadId:"
						+ threadId + "userid:" + getSessionWrapper().getLoggedInUserProfileId());
			}
		} else {
			logger.log(Level.WARNING, "user trying to change visibility of a thread of a non existant group : " + ownerId + " this should be impossible!");
		}
	}

	public static void deleteThread(String ownerId, String threadId) {
		ProjectGroup group = BeanProvider.getGroupService().loadGroupById(ownerId, true);
		if (group != null) {
			if (new GroupPep(group).isAllowedToDeleteThread(getSessionWrapper().getLoggedInUserProfileId())) {

				// non transactional, but ok, we might just lose a little space in that case
				BeanProvider.getForumThreadDao().deleteThread(threadId);
				BeanProvider.getForumPostDao().deletePostsOfThread(threadId);

				renderJSON(new MappedValue("removeThreadId", threadId));
			} else {
				logger.log(Level.WARNING, "user trying to delete a group thread but is not allowed to: projectId:" + ownerId + ", threadId:" + threadId
						+ "userid:" + getSessionWrapper().getLoggedInUserProfileId());
			}
		} else {
			logger.log(Level.WARNING, "user trying to delete a thread of a non existant group : " + ownerId + " this should be impossible!");
		}
	}

	// ////////////////////////////
	// ////////////////////////////
	// /

	private static GroupsViewParticipantActionOutcome buildOutcome(ProjectGroup group, String otherUserId, String projectId) {
		GroupsViewParticipantActionOutcome outcome = new GroupsViewParticipantActionOutcome(true);
		GroupViewVisibility visibility = new GroupViewVisibility(new GroupPep(group), getSessionWrapper().getLoggedInUserProfileId());

		List<Participation> allProjectWhereUserIsAdmin = computeFilteredListOfProjectWhereUserIsAdmin();
		visibility.addProjectsWhereUserIsAdmin(allProjectWhereUserIsAdmin);

		if (projectId != null) {
			// null is ok: not all group actions involve a projectId
			List<Participation> updatedProjectParticipationList = filterProjectAltreadyInGroup(group, computeFilteredListOfProjectWhereUserIsAdmin());
			for (Participation participation : updatedProjectParticipationList) {
				if (projectId.equals(participation.getProjectSummary().getProjectId())) {
					participation.getProjectSummary().initProjectLink();
					participation.generatePhotoLink();
					outcome.setAddedProjectIamAdminOf(participation);
				}
			}
		}

		outcome.setLoggedInUser_makeMemberLinkVisible(visibility.isMakeMemberLinkVisible(getSessionWrapper().getLoggedInUserProfileId()));
		outcome.setLoggedInUser_makeAdminLinkVisible(visibility.isMakeAdminLinkVisible(getSessionWrapper().getLoggedInUserProfileId()));
		outcome.setLoggedInUser_leaveLinkVisible(visibility.isLeaveLinkVisible(getSessionWrapper().getLoggedInUserProfileId()));

		if (otherUserId != null) {
			// null is ok: not all group actions involve an "other user"
			outcome.setOtherUser_makeMemberLinkVisible(visibility.isMakeMemberLinkVisible(otherUserId));
			outcome.setOtherUser_makeAdminLinkVisible(visibility.isMakeAdminLinkVisible(otherUserId));
			outcome.setOtherUser_leaveLinkVisible(visibility.isLeaveLinkVisible(otherUserId));
			outcome.setOtherUser_removeUserLinkVisible(visibility.isRemoveMemberLinkVisible(otherUserId));
		}

		outcome.setApplyToGroupWithProjectLinkVisisble(visibility.isApplyWithProjetLinkVisible());

		return outcome;
	}

	private static List<Participation> filterProjectAltreadyInGroup(ProjectGroup group, List<Participation> allProjectWhereUserIsAdmin) {

		if (allProjectWhereUserIsAdmin == null || allProjectWhereUserIsAdmin.isEmpty()) {
			return allProjectWhereUserIsAdmin;
		}

		List<Participation> filteredProject = new LinkedList<Participation>();
		for (Participation participation : allProjectWhereUserIsAdmin) {
			if (!group.isProjectMemberOfGroupOrHasAlreadyApplied(participation.getProjectSummary().getProjectId())) {
				filteredProject.add(participation);
			}
		}
		return filteredProject;
	}

	private static List<Participation> computeFilteredListOfProjectWhereUserIsAdmin() {
		if (getSessionWrapper().isLoggedIn()) {
			UserProfile user = BeanProvider.getUserProfileService().loadUserProfile(getSessionWrapper().getLoggedInUserProfileId(), false);
			if (user != null) {
				return user.getAllProjectsWhereUserIsAdmin(getSessionWrapper().getLoggedInUserProfileId());
			}
		}
		return new LinkedList<Participation>();
	}
}