package com.svend.dab.eda.events.projects;

import java.util.Set;

import com.svend.dab.core.beans.projects.Asset;
import com.svend.dab.core.beans.projects.Project;
import com.svend.dab.core.beans.projects.Task;
import com.svend.dab.eda.Event;
import com.svend.dab.eda.IEventPropagator;
import com.svend.dab.eda.IEventPropagatorsContainer;

public class ProjectUpdated extends Event {

	private Project updatedProject;

	private Set<Task> updatedTasks;
	private Set<String> removedTasksIds;
	
	private Set<Asset> updatedAssets;
	private Set<String> removedAssetsIds;

	@Override
	public IEventPropagator selectEventProcessor(IEventPropagatorsContainer container) {
		return container.getPropagatorByName("projectUpdatedPropagator");
	}

	public ProjectUpdated(Project updated, Set<Task> updatedTasks, Set<String> removedTasksIds, Set<Asset> updatedAssets, Set<String> removedAssetsIds) {
		super();
		this.updatedProject = updated;
		this.updatedTasks = updatedTasks;
		this.removedTasksIds = removedTasksIds;
		this.updatedAssets = updatedAssets;
		this.removedAssetsIds = removedAssetsIds;
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

	public Set<Asset> getUpdatedAssets() {
		return updatedAssets;
	}

	public void setUpdatedAssets(Set<Asset> updatedAssets) {
		this.updatedAssets = updatedAssets;
	}

	public Set<String> getRemovedAssetsIds() {
		return removedAssetsIds;
	}

	public void setRemovedAssetsIds(Set<String> removedAssetsIds) {
		this.removedAssetsIds = removedAssetsIds;
	}

}
