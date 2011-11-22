package com.svend.dab.core.beans.projects;

import java.util.Date;

import com.svend.dab.core.beans.projects.Participant.ROLE;

/**
 * @author Svend
 * 
 */
public class Participation {

	private ProjectSummary projectSummary;
	private ROLE role;


	
	
	public Participation() {
		super();
	}

	public Participation(Project project, ROLE role) {
		super();
		this.projectSummary = new ProjectSummary(project);
		this.role = role;
	}
	
	
	public boolean isIn(ProjectSummary pSummary) {
		return pSummary != null && projectSummary.getProjectId().equals(pSummary.getProjectId());
	}
	
	public void generatePhotoLink(Date expirationdate) {
		projectSummary.generatePhotoLink(expirationdate);
	}

	

	public ProjectSummary getProjectSummary() {
		return projectSummary;
	}

	public void setProjectSummary(ProjectSummary projectSummary) {
		this.projectSummary = projectSummary;
	}

	public ROLE getRole() {
		return role;
	}

	public void setRole(ROLE role) {
		this.role = role;
	}




}
