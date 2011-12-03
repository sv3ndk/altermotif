package com.svend.dab.eda.events.profile;

import java.util.Date;

import com.svend.dab.eda.Event;
import com.svend.dab.eda.IEventPropagator;
import com.svend.dab.eda.IEventPropagatorsContainer;

public class UserReferenceWritten extends Event {

	// the id must be generated in the event, in order to be able to detect replays of reception of this event (and avoid duplicating the references in the profile)
	private String referenceId;
	
	private String fromUserName;

	private String toUserName;

	private Date creationDate;

	private String text;

	@Override
	public IEventPropagator<UserReferenceWritten> selectEventProcessor(IEventPropagatorsContainer container) {
		return container.getPropagatorByName("userReferenceWrittenEventPropagator");
	}

	public String getFromUserName() {
		return fromUserName;
	}

	public void setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
	}

	public String getToUserName() {
		return toUserName;
	}

	public void setToUserName(String toUserName) {
		this.toUserName = toUserName;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

}
