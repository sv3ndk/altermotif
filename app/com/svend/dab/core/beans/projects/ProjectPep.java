package com.svend.dab.core.beans.projects;

import com.google.common.base.Strings;
import com.svend.dab.core.beans.profile.UserProfile;
import com.svend.dab.core.beans.projects.Participant.ROLE;
import com.svend.dab.core.beans.projects.Project.PROJECT_VISIBILITY;
import com.svend.dab.core.beans.projects.Project.STATUS;

import controllers.BeanProvider;

/**
 * @author svend
 * 
 *         Project policy enformcement point
 * 
 *         TODO: optimization here: cache the user role resolution (or inside the proejct itself, rather...)
 * 
 */
public class ProjectPep {

	private final Project project;

	public ProjectPep(Project project) {
		super();
		this.project = project;
	}

	// //////////////////////////////////////////
	// project applications

	public boolean isAllowedToApplyToProject(String visitingUserId) {
		if (visitingUserId == null) {
			return false;
		}

		if (project.isUserAlreadyMember(visitingUserId) || project.isUserApplying(visitingUserId)) {
			return false;
		}

		// TODO: optimization: cache the profile here instead of loading it each time...
		// + do not load the whole profile (just the boolean is enough...)
		UserProfile profile = BeanProvider.getUserProfileService().loadUserProfile(visitingUserId, false);

		if (profile == null || !profile.getPrivacySettings().isProfileActive()) {
			return false;
		}

		return true;
	}

	public boolean isAllowedToCancelApplication(String visitingUserId) {
		if (visitingUserId == null) {
			return false;
		}

		if (!project.isUserApplying(visitingUserId)) {
			return false;
		}

		// TODO: optimization: cache the profile here instead of loading it each time...
		// + do not load the whole profile (just the boolean is enough...)
		UserProfile profile = BeanProvider.getUserProfileService().loadUserProfile(visitingUserId, false);

		if (profile == null || !profile.getPrivacySettings().isProfileActive()) {
			return false;
		}

		return true;

	}

	// -----------------------------------------------
	// project edition

	/**
	 * Only the project owner or the project admin may edit
	 * 
	 * @param user
	 * @return
	 */
	public boolean isAllowedToEditAtLeastPartially(String user) {
		// admin may update some parts of the project
		// owner may update any part
		return isAdminOrOwnerOfStartedProject(user);
	}

	public boolean isAllowedToEditProjectOffer(String user) {
		return isOwnerOfStartedProject(user);
	}

	public boolean isAllowedToEditProjectReason(String user) {
		return isOwnerOfStartedProject(user);
	}

	public boolean isAllowedToEditProjectTags(String user) {
		return isOwnerOfStartedProject(user);
	}

	public boolean isAllowedToEditProjectThemes(String user) {
		return isOwnerOfStartedProject(user);
	}

	// /////////////////////////////////////
	// projects tasks and assets

	public boolean isAllowedToEditProjectTasks(String user) {
		return isAdminOrOwnerOfStartedProject(user);
	}

	public boolean isAllowedToEditProjectAssets(String user) {
		return isAdminOrOwnerOfStartedProject(user);
	}

	// //////////////////////////////
	// project life cycle

	public boolean isAllowedToCancelProject(String user) {
		if (!isOwnerOfStartedProject(user)) {
			return false;
		} else {
			return project.hasNoOtherActiveParticipantThanTheOwner();
		}
	}

	public boolean isAllowedToTerminate(String user) {
		return isOwnerOfStartedProject(user);
	}

	public boolean isAllowedToRestartProject(String user) {
		if (user == null) {
			return false;
		}

		if (project.getStatus() != STATUS.done) {
			return false;
		}

		ROLE role = project.findRoleOfUser(user);
		return role == ROLE.initiator;
	}

	// ------------------------------------------
	// photo gallery

	public boolean isAllowedToEditPhotoGallery(String user) {
		return isAdminOrOwnerOfStartedProject(user);
	}

	// ////////////////////////////////////////
	// project description (description, strategy, offer)

	public boolean isAllowedToSeeDecription(String visitingUserId) {
		return allowsReadAccessTo(project.getPdata().getDescriptionVisibility(), visitingUserId);
	}

	public boolean isAllowedToSeeStrategy(String visitingUserId) {
		return allowsReadAccessTo(project.getPdata().getStrategyVisibility(), visitingUserId);
	}

	public boolean isAllowedToSeeOffer(String visitingUserId) {
		return allowsReadAccessTo(project.getPdata().getOfferVisibility(), visitingUserId);
	}

	// ////////////////////////////////////////
	// membership applications

	/**
	 * @param user
	 * @return
	 */
	public boolean isAllowedToSeeApplications(String user) {
		return isPartOfStartedProject(user);
	}

	/**
	 * @param user
	 * @return
	 */
	public boolean isAllowedToAcceptOrRejectApplications(String user) {
		return isAdminOrOwnerOfStartedProject(user);
	}

	/**
	 * @param userId
	 * @param rejectedUserId
	 * @return
	 */
	public boolean isAllowedToEjectParticipant(String userId, String rejectedUserId) {
		if (Strings.isNullOrEmpty(userId) || Strings.isNullOrEmpty(rejectedUserId)) {
			return false;
		}

		if (project.getStatus() != STATUS.started) {
			return false;
		}

		ROLE roleOfRejected = project.findRoleOfUser(rejectedUserId);
		if (roleOfRejected == ROLE.initiator) {
			return false;
		}

		// cannot simply "remove" an owner ship proposal, the owner has to cancel the transfer of ownership
		Participant rejectedParticipant = project.getParticipant(rejectedUserId);
		if (rejectedParticipant.isOwnershipProposed()) {
			return false;
		}

		ROLE roleOfUser = project.findRoleOfUser(userId);
		return roleOfUser == ROLE.initiator || roleOfUser == ROLE.admin;
	}

	public boolean isAllowedToMakeAdmin(String userId, String upgradedUser) {
		if (Strings.isNullOrEmpty(userId) || Strings.isNullOrEmpty(upgradedUser)) {
			return false;
		}

		if (project.getStatus() != STATUS.started) {
			return false;
		}

		ROLE roleOfUpgraded = project.findRoleOfUser(upgradedUser);
		if (roleOfUpgraded != ROLE.member) {
			return false;
		}

		// cannot make an inactive member admin
		Participant participation = project.getParticipant(upgradedUser);
		if (participation == null || !participation.getUser().isProfileActive()) {
			return false;
		}

		// cannot simply "make admin" an owner ship proposal, the owner has to cancel the transfer of ownership
		if (participation.isOwnershipProposed()) {
			return false;
		}

		ROLE roleOfUser = project.findRoleOfUser(userId);
		return roleOfUser == ROLE.initiator || roleOfUser == ROLE.admin;
	}

	public boolean isAllowedToMakeMember(String userId, String downgradedUser) {
		if (Strings.isNullOrEmpty(userId) || Strings.isNullOrEmpty(downgradedUser)) {
			return false;
		}

		if (project.getStatus() != STATUS.started) {
			return false;
		}

		Participant participation = project.getParticipant(downgradedUser);
		if (participation == null || !participation.getUser().isProfileActive()) {
			return false;
		}

		// cannot simply "make member" an owner ship proposal, the owner has to cancel the transfer of ownership
		if (participation.isOwnershipProposed()) {
			return false;
		}

		ROLE roleOfDowngraded = project.findRoleOfUser(downgradedUser);

		if (roleOfDowngraded != ROLE.admin) {
			return false;
		}

		ROLE roleOfUser = project.findRoleOfUser(userId);
		return roleOfUser == ROLE.initiator || roleOfUser == ROLE.admin;
	}

	public boolean isAllowedToGiveOwnership(String userId, String upgradedUser) {
		if (Strings.isNullOrEmpty(userId) || Strings.isNullOrEmpty(upgradedUser)) {
			return false;
		}

		if (project.getStatus() != STATUS.started) {
			return false;
		}

		// may not give ownership to himself
		if (upgradedUser.equals(userId)) {
			return false;
		}

		// may not give ownership to an inactive user
		Participant participation = project.getParticipant(upgradedUser);
		if (participation == null || !participation.getUser().isProfileActive()) {
			return false;
		}

		// may not give ownership to a user if we already proposed the owner ship in the past
		if (participation.isOwnershipProposed()) {
			return false;
		}

		return project.findRoleOfUser(userId) == ROLE.initiator;
	}

	public boolean isAllowedToCancelOwnershipTransfer(String userId, String upgradedUser) {
		if (Strings.isNullOrEmpty(userId) || Strings.isNullOrEmpty(upgradedUser)) {
			return false;
		}

		if (project.getStatus() != STATUS.started) {
			return false;
		}

		// may not remove ownership to himself
		if (upgradedUser.equals(userId)) {
			return false;
		}

		// may only remove it if it has been proposed previously
		Participant participation = project.getParticipant(upgradedUser);
		if (!participation.isOwnershipProposed()) {
			return false;
		}

		return project.findRoleOfUser(userId) == ROLE.initiator;
	}

	/**
	 * @param userId
	 * @return
	 */
	public boolean isAllowedToAcceptOrRefuseOwnershipTransfer(String userId) {
		if (Strings.isNullOrEmpty(userId)) {
			return false;
		}

		if (project.getStatus() != STATUS.started) {
			return false;
		}

		// may not accept ownership if he currently is inactive user (he should not be able to deactivate his profile anyway, so this should be impossible, but let's be paranoid)
		Participant participation = project.getParticipant(userId);
		if (participation == null || !participation.getUser().isProfileActive()) {
			return false;
		}

		return participation.isOwnershipProposed();
	}

	/**
	 * @param userId
	 * @return
	 */
	public boolean isAllowedToLeave(String userId) {

		if (project.getStatus() != STATUS.started) {
			return false;
		}

		// the initiator must forward ownership, he cannot just leave
		return project.findRoleOfUser(userId) != ROLE.initiator;
	}

	// /////////////////////////////////////////////
	//

	protected boolean allowsReadAccessTo(PROJECT_VISIBILITY visibility, String visitingUserId) {

		ROLE role = project.findRoleOfUser(visitingUserId);

		switch (visibility) {
		case everybody:
			return true;

		case loggedin:
			return visitingUserId != null;

		case members:
			return role == ROLE.member || role == ROLE.admin || role == ROLE.initiator;

		case admins:
			return role == ROLE.admin || role == ROLE.initiator;

		case owner:
			return role == ROLE.initiator;
		}

		return false;
	}

	// ////////////////////////////////
	// forum

	public boolean isAllowedAddForumThread(String user) {
		return isPartOfStartedProject(user);
	}

	public boolean isAllowedSeeThisForumThread(String user, boolean isThreadPublic) {
		// only project members (or higher roles) may see non public threads
		return isPartOfStartedProject(user) || isThreadPublic;
	}

	public boolean isAllowedToDeleteThread(String user) {
		return isAdminOrOwnerOfStartedProject(user);
	}

	public boolean isAllowedToUpdateVisibilityThread(String user) {
		return isPartOfStartedProject(user);
	}

	public boolean isAllowedToPostNewMessage(String user, ForumThread thread) {
		if (thread.isThreadPublic()) {
			return user != null;
		} else {
			return isPartOfStartedProject(user);
		}
	}

	public boolean isAllowedToDeleteForumPosts(String user, ForumThread thread) {
		return isAdminOrOwnerOfStartedProject(user);
	}
	
	public boolean isAllowedToMoveForumPosts(String user, ForumThread thread) {
		return isAdminOrOwnerOfStartedProject(user);
	}


	// ////////////////////////////////
	// some generic project related roles

	// member, admin or initiator
	protected boolean isPartOfStartedProject(String user) {
		if (user == null) {
			return false;
		}

		if (project.getStatus() != STATUS.started) {
			return false;
		}

		ROLE role = project.findRoleOfUser(user);
		return role == ROLE.initiator || role == ROLE.admin || role == ROLE.member;
	}

	// admin initiator only
	protected boolean isAdminOrOwnerOfStartedProject(String user) {
		if (user == null) {
			return false;
		}

		if (project.getStatus() != STATUS.started) {
			return false;
		}

		ROLE role = project.findRoleOfUser(user);
		return role == ROLE.initiator || role == ROLE.admin;
	}

	// initiator only
	protected boolean isOwnerOfStartedProject(String user) {
		if (user == null) {
			return false;
		}

		if (project.getStatus() != STATUS.started) {
			return false;
		}

		ROLE role = project.findRoleOfUser(user);
		return role == ROLE.initiator;
	}

}