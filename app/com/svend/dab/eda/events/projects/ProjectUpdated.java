package com.svend.dab.eda.events.projects;

import java.util.Set;

import com.svend.dab.core.beans.projects.Project;
import com.svend.dab.core.beans.projects.Task;
import com.svend.dab.eda.Event;
import com.svend.dab.eda.IEventPropagator;
import com.svend.dab.eda.IEventPropagatorsContainer;

public class ProjectUpdated extends Event {

	private Project updatedProject;
	
	private Set<Task> updatedTasks;

	@Override
	public IEventPropagator selectEventProcessor(IEventPropagatorsContainer container) {
		return container.getPropagatorByName("projectUpdatedPropagator");
	}

	public ProjectUpdated(Project updated, Set<Task> updatedTasks) {
		super();
		this.updatedProject = updated;
		this.updatedTasks = updatedTasks;
	}

	public Project getUpdatedProject() {
		return updatedProject;
	}

	public void setUpdatedProject(Project createdProject) {
		this.updatedProject = createdProject;
	}

}
