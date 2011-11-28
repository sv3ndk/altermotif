package com.svend.dab.eda.events.projects;

import com.svend.dab.core.beans.projects.Project;
import com.svend.dab.eda.Event;
import com.svend.dab.eda.IEventPropagator;
import com.svend.dab.eda.IEventPropagatorsContainer;

public class ProjectUpdated extends Event {

	private Project updatedProject;

	@Override
	public IEventPropagator selectEventProcessor(IEventPropagatorsContainer container) {
		return container.getProjectUpdatedPropagator();
	}

	public ProjectUpdated() {
		super();
	}

	public ProjectUpdated(Project updatedProject) {
		super();
		this.updatedProject = updatedProject;
	}

	public Project getUpdatedProject() {
		return updatedProject;
	}

	public void setUpdatedProject(Project createdProject) {
		this.updatedProject = createdProject;
	}

}
