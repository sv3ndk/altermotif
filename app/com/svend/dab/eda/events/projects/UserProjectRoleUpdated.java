package com.svend.dab.eda.events.projects;

import com.svend.dab.core.beans.projects.Participant.ROLE;
import com.svend.dab.eda.Event;
import com.svend.dab.eda.IEventPropagator;
import com.svend.dab.eda.IEventPropagatorsContainer;

public class UserProjectRoleUpdated extends Event {

	private String userId;
	private ROLE role;
	private String projectId;

	public UserProjectRoleUpdated() {
		super();
	}

	public UserProjectRoleUpdated(String userId, ROLE role, String projectId) {
		super();
		this.userId = userId;
		this.role = role;
		this.projectId = projectId;
	}

	@Override
	public IEventPropagator selectEventProcessor(IEventPropagatorsContainer container) {
		return container.getPropagatorByName("userProjectRoleUpdatedPropagator");
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public ROLE getRole() {
		return role;
	}

	public void setRole(ROLE role) {
		this.role = role;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

}
