package com.svend.dab.core.beans.groups;

import com.svend.dab.core.beans.projects.ProjectSummary;

/**
 * 
 * Representation of a project which is participant to a group (as opposed to a user which is a participant to a group)
 * 
 * @author svend
 * 
 */
public class GroupProjectParticipant {

	private ProjectSummary projet;
	private String applicationText;
	private boolean accepted;

	public GroupProjectParticipant() {
		super();
	}

	public GroupProjectParticipant(ProjectSummary projet,  String applicationText, boolean accepted) {
		super();
		this.projet = projet;
		this.accepted = accepted;
		this.applicationText = applicationText;
	}


	public ProjectSummary getProjet() {
		return projet;
	}

	public void setProjet(ProjectSummary projet) {
		this.projet = projet;
	}

	public boolean isAccepted() {
		return accepted;
	}

	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}

	public String getApplicationText() {
		return applicationText;
	}

	public void setApplicationText(String applicationText) {
		this.applicationText = applicationText;
	}

}
