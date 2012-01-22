package com.svend.dab.eda.events.profile;

import com.svend.dab.core.beans.profile.PersonalData;
import com.svend.dab.core.beans.profile.UserProfile;
import com.svend.dab.eda.Event;
import com.svend.dab.eda.IEventPropagator;
import com.svend.dab.eda.IEventPropagatorsContainer;

/**
 * @author svend
 * 
 */
public class UserProfilePersonalDataUpdatedEvent extends Event {

	private PersonalData personalData;
	private String username;


	public UserProfilePersonalDataUpdatedEvent() {
		super();
	}


	public UserProfilePersonalDataUpdatedEvent(UserProfile userProfile) {
		this.username = userProfile.getUsername();
		this.personalData = userProfile.getPdata();
	}

	@Override
	public String toString() {
		return "user profile update event " + username;
	}

	// --------------------------------------------------------
	// --------------------------------------------------------

	public IEventPropagator<UserProfilePersonalDataUpdatedEvent> selectEventProcessor(IEventPropagatorsContainer container) {
		return container.getPropagatorByName("userProfilePersonalDataEventPropagator");
	}

	public PersonalData getPersonalData() {
		return personalData;
	}

	public void setPersonalData(PersonalData personalData) {
		this.personalData = personalData;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
