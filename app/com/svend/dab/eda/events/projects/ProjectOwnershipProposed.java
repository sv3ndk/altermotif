/**
 * 
 */
package com.svend.dab.eda.events.projects;

import com.svend.dab.eda.Event;
import com.svend.dab.eda.IEventPropagator;
import com.svend.dab.eda.IEventPropagatorsContainer;

/**
 * @author svend
 * 
 */
public class ProjectOwnershipProposed extends Event {

	private String username;
	private String projectId;

	public ProjectOwnershipProposed() {
		super();
	}

	public ProjectOwnershipProposed(String username, String projectId) {
		super();
		this.username = username;
		this.projectId = projectId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.svend.dab.eda.Event#selectEventProcessor(com.svend.dab.eda.IEventPropagatorsContainer)
	 */
	@Override
	public IEventPropagator selectEventProcessor(IEventPropagatorsContainer container) {
		return container.getPropagatorByName("projectOwnershipProposedPropagator");
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

}
