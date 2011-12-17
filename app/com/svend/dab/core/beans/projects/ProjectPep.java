package com.svend.dab.core.beans.projects;

import com.google.common.base.Strings;
import com.svend.dab.core.beans.projects.Participant.ROLE;

/**
 * @author svend
 * 
 * Project policy enformcement point
 *
 */
public class ProjectPep {
	
	private final Project project;
	

	public ProjectPep(Project project) {
		super();
		this.project = project;
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
		if (user == null) {
			return false;
		}
		
		ROLE role = project.findRoleOfUser(user);
		return role == ROLE.initiator;
	}


	public boolean isAllowedToCancel(String user) {
		if (user == null) {
			return false;
		}
		ROLE role = project.findRoleOfUser(user);
		return role == ROLE.initiator;
	}


	public boolean isAllowedToTerminate(String user) {
		if (user == null) {
			return false;
		}
		ROLE role = project.findRoleOfUser(user);
		return role == ROLE.initiator;
	}
	
	// ------------------------------------------
	// photo gallery


	public boolean isAllowedToEditPhotoGallery(String user) {
		if (user == null) {
			return false;
		}
		ROLE role = project.findRoleOfUser(user);
		return role == ROLE.initiator || role == ROLE.admin;
	}

	
	// applications

	/**
	 * @param user
	 * @return
	 */
	public boolean isAllowedToSeeApplications(String user) {
		if (user == null) {
			return false;
		}
		ROLE role = project.findRoleOfUser(user);
		return role == ROLE.initiator || role == ROLE.admin || role == ROLE.member;
	}
	
	/**
	 * @param user
	 * @return
	 */
	public boolean isAllowedToAcceptOrRejectApplications(String user) {
		if (user == null) {
			return false;
		}
		ROLE role = project.findRoleOfUser(user);
		return role == ROLE.initiator || role == ROLE.admin;
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
		
		ROLE roleOfRejected =project.findRoleOfUser(rejectedUserId);
		
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
		
		ROLE roleOfUpgraded =project.findRoleOfUser(upgradedUser);
		
		if (roleOfUpgraded != ROLE.member) {
			return false;
		}

		// cannot make an inactive member admin
		Participant participation = project.getParticipant(upgradedUser);
		if (participation == null || ! participation.getUser().isProfileActive()) {
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
		
		Participant participation = project.getParticipant(downgradedUser);
		if (participation == null || ! participation.getUser().isProfileActive()) {
			return false;
		}

		// cannot simply "make member" an owner ship proposal, the owner has to cancel the transfer of ownership 
		if (participation.isOwnershipProposed()) {
			return false;
		}
		
		ROLE roleOfDowngraded =project.findRoleOfUser(downgradedUser);
		
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
		
		// may not give ownership to himself
		if (upgradedUser .equals(userId)) {
			return false;
		}
		
		// may not give ownership to an inactive user
		Participant participation = project.getParticipant(upgradedUser);
		if (participation == null || ! participation.getUser().isProfileActive()) {
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
		
		// may not remove ownership to himself
		if (upgradedUser .equals(userId)) {
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
		if (Strings.isNullOrEmpty(userId) ) {
			return false;
		}
		
		
		// may not accept ownership if he currently is inactive user (he should not be able to deactivate his profile anyway, so this should be impossible, but let's be paranoid)
		Participant participation = project.getParticipant(userId);
		if (participation == null || ! participation.getUser().isProfileActive()) {
			return false;
		}

		return participation.isOwnershipProposed() ;
	}
	
	

	/**
	 * @param userId
	 * @return
	 */
	public boolean isAllowedToLeave(String userId) {
		// the initiator must forward ownership, he cannot just leave
		return project.findRoleOfUser(userId) != ROLE.initiator;
	}

}
