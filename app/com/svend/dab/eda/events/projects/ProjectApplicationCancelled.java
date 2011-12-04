package com.svend.dab.eda.events.projects;

import com.svend.dab.eda.Event;
import com.svend.dab.eda.IEventPropagator;
import com.svend.dab.eda.IEventPropagatorsContainer;

public class ProjectApplicationCancelled extends Event {

	private String userId;
	private String projectId;

	public ProjectApplicationCancelled() {
		super();
	}

	public ProjectApplicationCancelled(String userId, String projectId) {
		super();
		this.userId = userId;
		this.projectId = projectId;
	}

	@Override
	public IEventPropagator selectEventProcessor(IEventPropagatorsContainer container) {
		return container.getPropagatorByName("projectApplicationCancelledPropagator");
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

}
