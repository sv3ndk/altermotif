package com.svend.dab.eda.events.projects;

import com.svend.dab.eda.Event;
import com.svend.dab.eda.IEventPropagator;
import com.svend.dab.eda.IEventPropagatorsContainer;

/**
 * @author svend
 * 
 */
public class ProjectOwnershipAccepted extends Event {

	private String username;
	private String previousOwner;
	

	private String projectId;

	public ProjectOwnershipAccepted() {
		super();
	}

	public ProjectOwnershipAccepted(String username, String previousOwner, String projectId) {
		super();
		this.username = username;
		this.previousOwner = previousOwner;
		this.projectId = projectId;
	}

	@Override
	public IEventPropagator selectEventProcessor(IEventPropagatorsContainer container) {
		return container.getPropagatorByName("projectOwnershipAcceptedPropagator");
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

	public String getPreviousOwner() {
		return previousOwner;
	}

	public void setPreviousOwner(String previousOwner) {
		this.previousOwner = previousOwner;
	}

}
