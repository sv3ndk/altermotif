package com.svend.dab.eda.events.contacts;

import java.util.Date;

import com.sun.istack.NotNull;
import com.svend.dab.eda.Event;
import com.svend.dab.eda.IEventPropagator;
import com.svend.dab.eda.IEventPropagatorsContainer;

/**
 * 
 * This event can only happen if there is not yet any relationship between those two user profiles
 * 
 * @author svend
 * 
 */
public class ContactRelationshipRequested extends Event {

	@NotNull
	private String fromUser;
	
	@NotNull
	private String toUser;
	private String introductionText;
	
	@NotNull
	private Date requestDate;

	@Override
	public IEventPropagator<ContactRelationshipRequested> selectEventProcessor(IEventPropagatorsContainer container) {
		return container.getPropagatorByName("contactRelationshipRequestedPropagator");
	}

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

	public Date getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}

	public String getIntroductionText() {
		return introductionText;
	}

	public void setIntroductionText(String introductionText) {
		this.introductionText = introductionText;
	}

}
