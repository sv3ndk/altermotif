package com.svend.dab.eda.events.groups;

import com.svend.dab.core.beans.groups.GroupSummary;
import com.svend.dab.eda.Event;
import com.svend.dab.eda.IEventPropagator;
import com.svend.dab.eda.IEventPropagatorsContainer;

public class GroupSummaryUpdated extends Event {

	private GroupSummary updatedSummary;

	public GroupSummaryUpdated(GroupSummary updatedSummary) {
		super();
		this.updatedSummary = updatedSummary;
	}

	public GroupSummaryUpdated() {
		super();
	}

	@Override
	public IEventPropagator selectEventProcessor(IEventPropagatorsContainer container) {
		return container.getPropagatorByName("groupSummaryUpdatedPropagator");
	}

	public GroupSummary getUpdatedSummary() {
		return updatedSummary;
	}

	public void setUpdatedSummary(GroupSummary updatedSummary) {
		this.updatedSummary = updatedSummary;
	}

}
