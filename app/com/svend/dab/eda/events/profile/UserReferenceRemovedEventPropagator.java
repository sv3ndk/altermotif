package com.svend.dab.eda.events.profile;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.svend.dab.core.beans.DabException;
import com.svend.dab.core.beans.DabPreConditionViolationException;
import com.svend.dab.core.beans.profile.UserProfile;
import com.svend.dab.dao.mongo.IUserProfileDao;
import com.svend.dab.eda.IEventPropagator;

/**
 * @author svend
 *
 */
@Service
public class UserReferenceRemovedEventPropagator implements IEventPropagator<UserReferenceRemovedEvent>{

	@Autowired
	private IUserProfileDao userProfileDao; 
	
	
	private static Logger logger = Logger.getLogger(UserReferenceRemovedEventPropagator.class.getName());
	
	@Override
	public void propagate(UserReferenceRemovedEvent event) throws DabException {
		
		if (event == null || event.getReferenceId() == null || "".equals(event.getReferenceId())) {
			logger.log(Level.WARNING, "Cannot remove a reference: null reference or reference with null or empty id");
		} else {
			UserProfile fromProfile = userProfileDao.findOne(event.getFromProfileId());
			UserProfile toProfile = userProfileDao.findOne(event.getToProfileId());
			if (fromProfile == null ) {
				throw new DabPreConditionViolationException("Cannot propagate an reference removed event: from user does not exist: " + event.getFromProfileId());
			} else if (toProfile == null) {
				throw new DabPreConditionViolationException("Cannot propagate an reference removed event: to user does not exist: " + event.getToProfileId());
			} else {
				userProfileDao.removeWrittenReference(fromProfile, event.getReferenceId());
				userProfileDao.removeReceivedReference(toProfile, event.getReferenceId());
			}
		}
	}
}
