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

	private Set<String> removedTasksIds;

	@Override
	public IEventPropagator selectEventProcessor(IEventPropagatorsContainer container) {
		return container.getPropagatorByName("projectUpdatedPropagator");
	}

	public ProjectUpdated(Project updated, Set<Task> updatedTasks, Set<String> removedTasksIds) {
		super();
		this.updatedProject = updated;
		this.updatedTasks = updatedTasks;
		this.removedTasksIds = removedTasksIds;
	}

	public Project getUpdatedProject() {
		return updatedProject;
	}

	public void setUpdatedProject(Project createdProject) {
		this.updatedProject = createdProject;
	}

	public Set<Task> getUpdatedTasks() {
		return updatedTasks;
	}

	public void setUpdatedTasks(Set<Task> updatedTasks) {
		this.updatedTasks = updatedTasks;
	}

	public Set<String> getRemovedTasksIds() {
		return removedTasksIds;
	}

	public void setRemovedTasksIds(Set<String> removedTasksIds) {
		this.removedTasksIds = removedTasksIds;
	}

}
