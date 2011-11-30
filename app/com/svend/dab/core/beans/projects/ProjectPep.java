package com.svend.dab.core.beans.projects;

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
	
	
	

}
