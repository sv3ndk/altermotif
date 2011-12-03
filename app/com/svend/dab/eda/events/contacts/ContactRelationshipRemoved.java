package com.svend.dab.eda.events.contacts;

import com.sun.istack.NotNull;
import com.svend.dab.eda.Event;
import com.svend.dab.eda.IEventPropagator;
import com.svend.dab.eda.IEventPropagatorsContainer;

/**
 * This event can only happen if there is a confirmed relationship between the parties
 * 
 * @author svend
 *
 */
public class ContactRelationshipRemoved extends Event {
	
	@NotNull
	private String cancellingUser;
	@NotNull
	private String otherUser;

	public ContactRelationshipRemoved(String cancellingUser, String otherUser) {
		super();
		this.cancellingUser = cancellingUser;
		this.otherUser = otherUser;
	}

	
	public ContactRelationshipRemoved() {
		super();
	}


	@Override
	public IEventPropagator<ContactRelationshipRemoved> selectEventProcessor(IEventPropagatorsContainer container) {
		return container.getPropagatorByName("contactRelationshipRemovedPropagator");
	}

	public String getCancellingUser() {
		return cancellingUser;
	}

	public void setCancellingUser(String cancellingUser) {
		this.cancellingUser = cancellingUser;
	}


	public String getOtherUser() {
		return otherUser;
	}

	public void setOtherUser(String otherUser) {
		this.otherUser = otherUser;
	}

}
