package com.svend.dab.eda.events.groups;

import com.svend.dab.eda.Event;
import com.svend.dab.eda.IEventPropagator;
import com.svend.dab.eda.IEventPropagatorsContainer;

/**
 * @author svend
 * 
 */
public class GroupUserParticipantRemoved extends Event {

	private String groupId;
	private String participantId;

	public GroupUserParticipantRemoved() {
		super();
	}

	public GroupUserParticipantRemoved(String groupId, String participantId) {
		super();
		this.groupId = groupId;
		this.participantId = participantId;
	}

	@Override
	public IEventPropagator selectEventProcessor(IEventPropagatorsContainer container) {
		return container.getPropagatorByName("groupUserParticipantRemovedPropagator");
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getParticipantId() {
		return participantId;
	}

	public void setParticipantId(String participantId) {
		this.participantId = participantId;
	}

}
