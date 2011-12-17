package com.svend.dab.eda.events.projects;

import com.svend.dab.core.beans.projects.Project.STATUS;
import com.svend.dab.eda.Event;
import com.svend.dab.eda.IEventPropagator;
import com.svend.dab.eda.IEventPropagatorsContainer;

/**
 * 
 * This is sent when the project changes to "terminated" or back to "started" <br />
 * 
 * A separate event is necessary for the status "cancelled", since this one is not undoable and corresponds to a logical delete
 * 
 * @author svend
 * 
 */
public class ProjectStatusChanged extends Event {

	private String projectId;
	private STATUS newStatus;

	public ProjectStatusChanged() {
		super();
	}

	public ProjectStatusChanged(String projectId, STATUS newStatus) {
		super();
		this.projectId = projectId;
		this.newStatus = newStatus;
	}

	@Override
	public IEventPropagator selectEventProcessor(IEventPropagatorsContainer container) {
		return container.getPropagatorByName("projectStatusChangedPropagator");
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public STATUS getNewStatus() {
		return newStatus;
	}

	public void setNewStatus(STATUS newStatus) {
		this.newStatus = newStatus;
	}

}
