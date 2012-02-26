package com.svend.dab.eda.events.groups;

import com.svend.dab.core.beans.groups.ProjectGroup;
import com.svend.dab.eda.Event;
import com.svend.dab.eda.IEventPropagator;
import com.svend.dab.eda.IEventPropagatorsContainer;

/**
 * @author svend
 * 
 */
public class GroupCreated extends Event {

	private ProjectGroup createdGroup;
	private String creatorUserId;

	public GroupCreated() {
		super();
	}

	public GroupCreated(ProjectGroup createdGroup, String creatorUserId) {
		super();
		this.createdGroup = createdGroup;
		this.creatorUserId = creatorUserId;
	}

	@Override
	public IEventPropagator selectEventProcessor(IEventPropagatorsContainer container) {
		return container.getPropagatorByName("groupCreatedPropagator");
	}

	public ProjectGroup getCreatedGroup() {
		return createdGroup;
	}

	public void setCreatedGroup(ProjectGroup createdGroup) {
		this.createdGroup = createdGroup;
	}

	public String getCreatorUserId() {
		return creatorUserId;
	}

	public void setCreatorUserId(String creatorUserId) {
		this.creatorUserId = creatorUserId;
	}

}
