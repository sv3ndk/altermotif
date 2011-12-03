package com.svend.dab.eda.events.contacts;

import java.util.Date;

import com.sun.istack.NotNull;
import com.svend.dab.eda.Event;
import com.svend.dab.eda.IEventPropagator;
import com.svend.dab.eda.IEventPropagatorsContainer;

/**
 * 
 * This event can only happen when there is a pending relationship between those two profiles
 * 
 * @author svend
 *
 */
public class ContactRelationshipResponse extends Event{
	

	public enum RESPONSE {
		approvedByRecipient,
		rejectedByRecipient,
		cancelledByRequestor
	}
	
	@NotNull
	private String fromUser;
	
	@NotNull
	private String toUser;
	
	@NotNull
	private Date reactionDate;
	
	@NotNull
	private RESPONSE response;
	
	// -----------------------------------------------
	// -----------------------------------------------
	

	/**
	 * @param fromUser
	 * @param toUser
	 * @param response
	 */
	public ContactRelationshipResponse(String fromUser, String toUser, RESPONSE response) {
		super();
		this.fromUser = fromUser;
		this.toUser = toUser;
		this.response = response;
		reactionDate = new Date();
	}

	
	public ContactRelationshipResponse() {
		super();
	}

	// -----------------------------------------------
	// -----------------------------------------------
	
	
	@Override
	public IEventPropagator<ContactRelationshipResponse> selectEventProcessor(IEventPropagatorsContainer container) {
		return container.getPropagatorByName("contactRelationshipResponsePropagator");
	}

	// -----------------------------------------------
	// -----------------------------------------------

	public String getFromUser() {
		return fromUser;
	}

	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
	}

	public String getToUser() {
		return toUser;
	}

	public void setToUser(String toUser) {
		this.toUser = toUser;
	}

	public Date getReactionDate() {
		return reactionDate;
	}

	public void setReactionDate(Date reactionDate) {
		this.reactionDate = reactionDate;
	}

	public RESPONSE getResponse() {
		return response;
	}

	public void setResponse(RESPONSE response) {
		this.response = response;
	}
	

}
