/**
 * 
 */
package com.svend.dab.eda.events.profile;

import com.svend.dab.core.beans.profile.UserSummary;
import com.svend.dab.eda.Event;
import com.svend.dab.eda.IEventPropagator;
import com.svend.dab.eda.IEventPropagatorsContainer;

/**
 * @author Svend
 *
 */
public class UserSummaryUpdated extends Event {
	
	
	private  UserSummary updatedSummary;
	
	public UserSummaryUpdated() {
		super();
	}


	public UserSummaryUpdated(UserSummary updatedSummary) {
		super();
		this.updatedSummary = updatedSummary;
	}


	/* (non-Javadoc)
	 * @see com.svend.dab.eda.Event#selectEventProcessor(com.svend.dab.eda.IEventPropagatorsContainer)
	 */
	@Override
	public IEventPropagator<UserSummaryUpdated> selectEventProcessor(IEventPropagatorsContainer container) {
		return container.getPropagatorByName("userSummaryUpdatedPropagator");
	}


	public UserSummary getUpdatedSummary() {
		return updatedSummary;
	}


	public void setUpdatedSummary(UserSummary updatedSummary) {
		this.updatedSummary = updatedSummary;
	}


}
