package com.svend.dab.eda.events.profile;

import java.util.Date;

import com.svend.dab.core.beans.profile.UserProfile;
import com.svend.dab.eda.Event;
import com.svend.dab.eda.IEventPropagator;
import com.svend.dab.eda.IEventPropagatorsContainer;

/**
 * @author svend
 * 
 */
public class UserLoggedInEvent extends Event {

	private String username;
	private Date dateOfLoggin;

	public UserLoggedInEvent(UserProfile userProfile) {
		this.username = userProfile.getUsername();
		this.dateOfLoggin = new Date();
	}

	public UserLoggedInEvent() {
		super();
	}

	@Override
	public IEventPropagator<UserLoggedInEvent> selectEventProcessor(IEventPropagatorsContainer container) {
		return container.getUserLoggedInEventPropagator();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Date getDateOfLoggin() {
		return dateOfLoggin;
	}

	public void setDateOfLoggin(Date dateOfLoggin) {
		this.dateOfLoggin = dateOfLoggin;
	}

}
