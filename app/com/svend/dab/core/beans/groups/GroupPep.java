package com.svend.dab.core.beans.groups;

import com.svend.dab.core.beans.groups.GroupParticipant.ROLE;
import com.svend.dab.core.beans.profile.UserProfile;

import controllers.BeanProvider;

/**
 * 
 * Policy enforcement point for groups: this contains all the role-related security authorization methods 
 * 
 * @author svend
 *
 */
public class GroupPep {
	
	
	private final ProjectGroup group;
	
	public GroupPep(ProjectGroup group) {
		super();
		this.group = group;
	}
	
	/////////////////////////////////////
	//

	public boolean isUserAllowedToEditGroup(String userId) {
		return isUserAdmin(userId);
	}

	public boolean isUserAllowedToCloseGroup(String userId) {
		return isUserAdmin(userId)  && group.getNumberOfParticipants() == 1;
	}
	
	///////////////////////////////
	
	public boolean isUserAllowedToApplyToGroup(String userId) {
		if (userId == null) {
			return false;
		}

		if (isUserMemberOrAdmin(userId)) {
			return false;
		}

		if (group.hasAppliedForGroupMembership(userId)) {
			return false;
		}
		
		// TODO: optimization: cache the profile here instead of loading it each time...
		// + do not load the whole profile (just the boolean is enough...)
		UserProfile profile = BeanProvider.getUserProfileService().loadUserProfile(userId, false);

		if (profile == null || !profile.getPrivacySettings().isProfileActive()) {
			return false;
		}

		return true;
	}
	
	public boolean isUserAllowedToAcceptAndRejectUserApplications(String userId) {
		return isUserAdmin(userId);
	}

	public boolean isUserAllowedToLeaveGroup(String userId) {
		ROLE userRole = group.findRoleOfUser(userId);
		
		if (userRole == null) {
			return false;
		} else if (userRole == ROLE.member) {
			return true;
		} else if (userRole == ROLE.admin) {
			return group.getNumberOfAdmins() > 1;
		}
		
		return false;
	}
	
	public boolean isUserAllowedToMakeAdmin(String userId, String upgradedUser) {
		ROLE userRole = group.findRoleOfUser(userId);
		if (userRole ==ROLE.admin) {
			ROLE roleOfUpgraded = group.findRoleOfUser(upgradedUser);
			return roleOfUpgraded == ROLE.member;
			
		} else {
			return false;
		}
	}
	
	public boolean isUserAllowedToMakeMember(String userId, String downGradedUser) {
		
		if (userId == null || downGradedUser == null || !downGradedUser.equals(userId)) {
			return false;
		}
		
		ROLE userRole = group.findRoleOfUser(userId);
		if (userRole == ROLE.admin) {
			return group.getNumberOfAdmins() > 1;
		} else {
			return false;
		}
	}
	
	
	public boolean isUserAllowedToRemoveUser(String userId, String removeUserId) {
		
		if (userId == null || removeUserId == null ) {
			return false;
		}
		
		if (userId.equals(removeUserId)) {
			return isUserAllowedToLeaveGroup(userId);
		} else {
			
			return isUserAdmin(userId) && ! isUserAdmin(removeUserId);
		}
	}
	
	
	public boolean isUserAllowedToAcceptAndRejectProjectApplications(String userId) {
		return isUserAdmin(userId);
	}


	
	///////////////////////////////
	
	public boolean isUserAdmin(String userId) {
		ROLE userRole = group.findRoleOfUser(userId);
		return userRole == ROLE.admin;
	}
	
	public boolean isUserMemberOrAdmin(String userId) {
		ROLE userRole = group.findRoleOfUser(userId);
		return userRole == ROLE.admin || userRole == ROLE.member;
	}

	public ProjectGroup getGroup() {
		return group;
	}

	

}
