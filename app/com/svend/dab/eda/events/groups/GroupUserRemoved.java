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
public class GroupUserRemoved extends Event {

	private String userId;
	private String groupId;

	public GroupUserRemoved() {
		super();
	}

	public GroupUserRemoved( String groupId, String userId) {
		super();
		this.userId = userId;
		this.groupId = groupId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.svend.dab.eda.Event#selectEventProcessor(com.svend.dab.eda.IEventPropagatorsContainer)
	 */
	@Override
	public IEventPropagator selectEventProcessor(IEventPropagatorsContainer container) {
		return container.getPropagatorByName("groupUserRemovedPropagator");
	}

	public String getUserId() {
		return userId;
	}

	public String getGroupId() {
		return groupId;
	}

}
