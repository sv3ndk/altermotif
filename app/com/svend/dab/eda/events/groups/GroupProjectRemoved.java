/**
 * 
 */
package com.svend.dab.eda.events.groups;

import com.svend.dab.eda.Event;
import com.svend.dab.eda.IEventPropagator;
import com.svend.dab.eda.IEventPropagatorsContainer;

/**
 * @author svend
 * 
 */
public class GroupProjectRemoved extends Event {

	private String groupId;
	private String projectId;

	public GroupProjectRemoved() {
		super();
	}

	public GroupProjectRemoved(String groupId, String projectId) {
		super();
		this.groupId = groupId;
		this.projectId = projectId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.svend.dab.eda.Event#selectEventProcessor(com.svend.dab.eda.IEventPropagatorsContainer)
	 */
	@Override
	public IEventPropagator selectEventProcessor(IEventPropagatorsContainer container) {
		return container.getPropagatorByName("groupProjectRemovedPropagator");
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

}
