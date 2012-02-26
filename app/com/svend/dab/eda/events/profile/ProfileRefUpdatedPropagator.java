package com.svend.dab.eda.events.profile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.svend.dab.core.beans.DabException;
import com.svend.dab.core.dao.IUserMessageDao;
import com.svend.dab.eda.IEventPropagator;

@Component
public class ProfileRefUpdatedPropagator implements IEventPropagator<ProfileRefUpdated> {

	@Autowired
	private IUserMessageDao userMessageRepo;
	
	public void propagate(ProfileRefUpdated event) throws DabException {
		
		userMessageRepo.updateFromUserProfileRef(event.getProfileRef());
		userMessageRepo.updateTOUserProfileRef(event.getProfileRef());
		
		
	}


}
