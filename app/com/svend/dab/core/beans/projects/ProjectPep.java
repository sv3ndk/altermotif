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

	public boolean isAllowedToSeeApplications(String user) {
		if (user == null) {
			return false;
		}
		ROLE role = project.findRoleOfUser(user);
		return role == ROLE.initiator || role == ROLE.admin || role == ROLE.member;
	}
	
	public boolean isAllowedToAcceptOrRejectApplications(String user) {
		if (user == null) {
			return false;
		}
		ROLE role = project.findRoleOfUser(user);
		return role == ROLE.initiator || role == ROLE.admin;
	}
	
	public boolean isAllowedToEjectParticipant(String userId, String rejectedUserId) {
		if (Strings.isNullOrEmpty(userId) || Strings.isNullOrEmpty(rejectedUserId)) {
			return false;
		}
		
		ROLE roleOfRejected =project.findRoleOfUser(rejectedUserId);
		
		if (roleOfRejected == ROLE.initiator) {
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
		
		Participant participation = project.getParticipation(upgradedUser);
		if (participation == null || ! participation.getUser().isProfileActive()) {
			return false;
		}
		
		
		ROLE roleOfUser = project.findRoleOfUser(userId);
		return roleOfUser == ROLE.initiator || roleOfUser == ROLE.admin;
	}


	public boolean isAllowedToMakeMember(String userId, String downgradedUser) {
		if (Strings.isNullOrEmpty(userId) || Strings.isNullOrEmpty(downgradedUser)) {
			return false;
		}
		
		Participant participation = project.getParticipation(downgradedUser);
		if (participation == null || ! participation.getUser().isProfileActive()) {
			return false;
		}

		
		ROLE roleOfDowngraded =project.findRoleOfUser(downgradedUser);
		
		if (roleOfDowngraded != ROLE.admin) {
			return false;
		}
		
		
		ROLE roleOfUser = project.findRoleOfUser(userId);
		return roleOfUser == ROLE.initiator || roleOfUser == ROLE.admin;
	}


	public boolean isAllowedGiveOwnership(String userId, String upgradedUser) {
		if (Strings.isNullOrEmpty(userId) || Strings.isNullOrEmpty(upgradedUser)) {
			return false;
		}
		
		if (upgradedUser .equals(userId)) {
			return false;
		}
		
		Participant participation = project.getParticipation(upgradedUser);
		if (participation == null || ! participation.getUser().isProfileActive()) {
			return false;
		}
		
		
		return project.findRoleOfUser(userId) == ROLE.initiator;
	}


	public boolean isAllowedToLeave(String userId) {
		// the initiator must forward ownership, he cannot just leave
		return project.findRoleOfUser(userId) != ROLE.initiator;
	}
	

}
