package com.svend.dab.eda.events.profile;

import com.svend.dab.eda.Event;
import com.svend.dab.eda.IEventPropagator;
import com.svend.dab.eda.IEventPropagatorsContainer;

/**
 * @author svend
 * 
 */
public class UserReferenceRemovedEvent extends Event {

	private String referenceId;
	private String fromProfileId;
	private String toProfileId;
	

	@Override
	public IEventPropagator<UserReferenceRemovedEvent> selectEventProcessor(IEventPropagatorsContainer container) {
		return container.getUserReferenceRemovedEventPropagator();
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public String getFromProfileId() {
		return fromProfileId;
	}

	public void setFromProfileId(String fromProfileId) {
		this.fromProfileId = fromProfileId;
	}

	public String getToProfileId() {
		return toProfileId;
	}

	public void setToProfileId(String toProfileId) {
		this.toProfileId = toProfileId;
	}

}
