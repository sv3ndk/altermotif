package com.svend.dab.core.beans.projects;

import java.util.Date;

import com.svend.dab.core.beans.projects.Participant.ROLE;

import controllers.BeanProvider;

/**
 * @author Svend
 * 
 */
public class Participation {

	private ProjectSummary projectSummary;
	private ROLE role;
	private boolean accepted = true;

	public Participation() {
		super();
	}

	public Participation(Project project, ROLE role, boolean accepted) {
		super();
		this.projectSummary = new ProjectSummary(project);
		this.role = role;
		this.accepted = accepted;
	}

	public boolean isIn(ProjectSummary pSummary) {
		return pSummary != null && projectSummary.getProjectId().equals(pSummary.getProjectId());
	}

	public void generatePhotoLink(Date expirationdate) {
		projectSummary.generatePhotoLink(expirationdate);
	}
	
	public void generatePhotoLink() {
		Date expirationdate = new Date();
		expirationdate.setTime(expirationdate.getTime() + BeanProvider.getConfig().getPhotoExpirationDelayInMillis());
		generatePhotoLink(expirationdate);
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

	public boolean isAccepted() {
		return accepted;
	}

	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}


}
