package com.svend.dab.eda.events.profile;

import java.util.UUID;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.svend.dab.core.beans.DabException;
import com.svend.dab.core.beans.DabIllegalFormatException;
import com.svend.dab.core.dao.IUserProfileDao;
import com.svend.dab.eda.IEventPropagator;

@Component
public class UserLoggedInEventPropagator implements IEventPropagator<UserLoggedInEvent> {

	@Autowired
	private IUserProfileDao userProfileRepo;
	
	private static Logger logger = Logger.getLogger(UserLoggedInEventPropagator.class.getName());

	public void propagate(UserLoggedInEvent event) throws DabException {

		if (event == null || event.getDateOfLoggin() == null || event.getUsername() == null) {
			throw new DabIllegalFormatException("Cannot record user loged in event: null event or null date or null username");
		}
		
		userProfileRepo.replaceLatestLoginAndPermKey(event.getUsername(), UUID.randomUUID().toString(), event.getDateOfLoggin());
	}
	

	

}
