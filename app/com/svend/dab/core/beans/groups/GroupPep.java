package com.svend.dab.core.beans.groups;

import com.svend.dab.core.beans.groups.GroupParticipant.ROLE;

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
		return isUserAdmin(userId);
	}
	
	
	///////////////////////////////
	
	protected boolean isUserAdmin(String userId) {
		ROLE userRole = group.findRoleOfUser(userId);
		return userRole == ROLE.admin;
	}
	
	protected boolean isUserMemberOrAdmin(String userId) {
		ROLE userRole = group.findRoleOfUser(userId);
		return userRole == ROLE.admin || userRole == ROLE.member;
	}

}
