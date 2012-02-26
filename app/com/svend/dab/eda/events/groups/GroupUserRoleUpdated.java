/**
 * 
 */
package com.svend.dab.eda.events.groups;

import com.svend.dab.core.beans.groups.GroupParticipant.ROLE;
import com.svend.dab.eda.Event;
import com.svend.dab.eda.IEventPropagator;
import com.svend.dab.eda.IEventPropagatorsContainer;

/**
 * @author svend
 * 
 */
public class GroupUserRoleUpdated extends Event {

	private String groupId;
	private String userId;
	private ROLE role;

	public GroupUserRoleUpdated(String groupId, String userId, ROLE role) {
		super();
		this.groupId = groupId;
		this.userId = userId;
		this.role = role;
	}

	public GroupUserRoleUpdated() {
		super();
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.svend.dab.eda.Event#selectEventProcessor(com.svend.dab.eda.IEventPropagatorsContainer)
	 */
	@Override
	public IEventPropagator selectEventProcessor(IEventPropagatorsContainer container) {
		return container.getPropagatorByName("groupUserRoleUpdatedPropagator");
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

	public ROLE getRole() {
		return role;
	}

	public void setRole(ROLE role) {
		this.role = role;
	}

}
