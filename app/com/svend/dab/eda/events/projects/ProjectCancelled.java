package com.svend.dab.eda.events.projects;

import com.svend.dab.eda.Event;
import com.svend.dab.eda.IEventPropagator;
import com.svend.dab.eda.IEventPropagatorsContainer;

public class ProjectCancelled extends Event {

	private String projectId;

	public ProjectCancelled() {
		super();
	}

	public ProjectCancelled(String projectId) {
		super();
		this.projectId = projectId;
	}

	@Override
	public IEventPropagator selectEventProcessor(IEventPropagatorsContainer container) {
		return container.getPropagatorByName("projectCancelledPropagator");
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

}
