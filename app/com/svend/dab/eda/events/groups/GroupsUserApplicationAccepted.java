package com.svend.dab.eda.events.groups;

import com.svend.dab.eda.Event;
import com.svend.dab.eda.IEventPropagator;
import com.svend.dab.eda.IEventPropagatorsContainer;

/**
 * @author svend
 *
 */
public class GroupsUserApplicationAccepted extends Event{

	private String groupId;
	private String userId;
	
	public GroupsUserApplicationAccepted() {
		super();
		// TODO Auto-generated constructor stub
	}

	public GroupsUserApplicationAccepted(String groupId, String userId) {
		super();
		this.groupId = groupId;
		this.userId = userId;
	}

	@Override
	public IEventPropagator selectEventProcessor(IEventPropagatorsContainer container) {
		return container.getPropagatorByName("groupsUserApplicationAcceptedPropagator");
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
