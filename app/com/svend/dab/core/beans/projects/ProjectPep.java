package com.svend.dab.core.beans.projects;

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
		return user.equals(project.getInitiator().getUser().getUserName());
	}


	public boolean isAllowedToCancel(String user) {
		if (user == null) {
			return false;
		}
		return user.equals(project.getInitiator().getUser().getUserName());
	}


	public boolean isAllowedToTerminate(String user) {
		if (user == null) {
			return false;
		}
		return user.equals(project.getInitiator().getUser().getUserName());
	}
	
	
	

}
