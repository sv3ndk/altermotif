package com.svend.dab.eda.events.profile;

import com.svend.dab.core.beans.profile.PrivacySettings;
import com.svend.dab.eda.Event;
import com.svend.dab.eda.IEventPropagator;
import com.svend.dab.eda.IEventPropagatorsContainer;

/**
 * @author Svend
 *
 */
public class UserPrivacySettingsUpdatedEvent extends Event {

	

	private String userId;
	private PrivacySettings newPrivacySettings;
	
	
	/**
	 * 
	 */
	public UserPrivacySettingsUpdatedEvent() {
	}

	public UserPrivacySettingsUpdatedEvent(String userId, PrivacySettings newPrivacySettings) {
		super();
		this.userId = userId;
		this.newPrivacySettings = newPrivacySettings;
	}
	/* (non-Javadoc)
	 * @see com.svend.dab.eda.Event#selectEventProcessor(com.svend.dab.eda.IEventPropagatorsContainer)
	 */
	@Override
	public IEventPropagator<UserPrivacySettingsUpdatedEvent> selectEventProcessor(IEventPropagatorsContainer container) {
		return container.getUserPrivacySettingsUpdatedPropagator();
	}
	
	// --------------------------------------
	// --------------------------------------

	public String getUserId() {
		return userId;
	}

	public void setUserId(String updatedUserId) {
		this.userId = updatedUserId;
	}

	public PrivacySettings getNewPrivacySettings() {
		return newPrivacySettings;
	}

	public void setNewPrivacySettings(PrivacySettings newPrivacySettings) {
		this.newPrivacySettings = newPrivacySettings;
	}

}
