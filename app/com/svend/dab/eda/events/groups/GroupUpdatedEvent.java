package com.svend.dab.eda.events.groups;

import com.svend.dab.core.beans.groups.ProjectGroup;
import com.svend.dab.eda.Event;
import com.svend.dab.eda.IEventPropagator;
import com.svend.dab.eda.IEventPropagatorsContainer;

/**
 * @author svend
 * 
 */
public class GroupUpdatedEvent extends Event {

	private ProjectGroup updated;

	@Override
	public IEventPropagator selectEventProcessor(IEventPropagatorsContainer container) {
		return container.getPropagatorByName("groupUpdatedPropagator");
	}

	public GroupUpdatedEvent(ProjectGroup updated) {
		super();
		this.updated = updated;
	}

	public GroupUpdatedEvent() {
		super();
	}

	public ProjectGroup getUpdated() {
		return updated;
	}

	public void setUpdated(ProjectGroup updated) {
		this.updated = updated;
	}

}
