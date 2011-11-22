/**
 * 
 */
package com.svend.dab.eda.events.profile;

import com.svend.dab.core.beans.profile.ProfileRef;
import com.svend.dab.eda.Event;
import com.svend.dab.eda.IEventPropagator;
import com.svend.dab.eda.IEventPropagatorsContainer;

/**
 * @author Svend
 *
 */
public class ProfileRefUpdated extends Event {

	private ProfileRef profileRef;
	
	public ProfileRefUpdated(ProfileRef profileRef) {
		super();
		this.profileRef = profileRef;
	}


	public ProfileRefUpdated() {
		super();
	}


	/* (non-Javadoc)
	 * @see com.svend.dab.eda.Event#selectEventProcessor(com.svend.dab.eda.IEventPropagatorsContainer)
	 */
	@Override
	public IEventPropagator<ProfileRefUpdated> selectEventProcessor(IEventPropagatorsContainer container) {
		return container.getProfileRefUpdatedPropagator();
	}


	public ProfileRef getProfileRef() {
		return profileRef;
	}


	public void setProfileRef(ProfileRef profileRef) {
		this.profileRef = profileRef;
	}

}
