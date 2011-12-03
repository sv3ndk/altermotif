/**
 * 
 */
package com.svend.dab.eda.events.projects;

import com.svend.dab.core.beans.projects.Project;
import com.svend.dab.eda.Event;
import com.svend.dab.eda.IEventPropagator;
import com.svend.dab.eda.IEventPropagatorsContainer;

/**
 * @author Svend
 * 
 */
public class ProjectCreated extends Event {

	private Project createdProject;
	private String creatorId; 

	
	
	public ProjectCreated() {
		super();
	}

	public ProjectCreated(Project createdProject, String creatorId) {
		super();
		this.createdProject = createdProject;
		this.creatorId = creatorId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.svend.dab.eda.Event#selectEventProcessor(com.svend.dab.eda.IEventPropagatorsContainer)
	 */
	@Override
	public IEventPropagator<ProjectCreated> selectEventProcessor(IEventPropagatorsContainer container) {
		return container.getPropagatorByName("projectCreatedPropagator");
	}

	public Project getCreatedProject() {
		return createdProject;
	}

	public void setCreatedProject(Project createdProject) {
		this.createdProject = createdProject;
	}

	public String getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(String creatorId) {
		this.creatorId = creatorId;
	}

}
