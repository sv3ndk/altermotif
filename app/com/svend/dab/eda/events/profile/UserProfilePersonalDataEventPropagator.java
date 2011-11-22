package com.svend.dab.eda.events.profile;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.svend.dab.core.beans.DabException;
import com.svend.dab.core.beans.DabPreConditionViolationException;
import com.svend.dab.core.beans.profile.UserProfile;
import com.svend.dab.core.beans.profile.UserSummary;
import com.svend.dab.dao.mongo.IUserProfileDao;
import com.svend.dab.eda.EventEmitter;
import com.svend.dab.eda.IEventPropagator;

/**
 * @author svend
 * 
 */
@Service
public class UserProfilePersonalDataEventPropagator implements IEventPropagator<UserProfilePersonalDataUpdatedEvent> {

	@Autowired
	IUserProfileDao userProfileRepo;
	
	@Autowired
	private EventEmitter emitter;

	private static Logger logger = Logger.getLogger(UserProfilePersonalDataEventPropagator.class.getName());

	@Override
	public void propagate(UserProfilePersonalDataUpdatedEvent event) throws DabException {

		if (event.getPersonalData() == null || event.getUsername() == null) {
			logger.log(Level.WARNING, "Cannot update a null profile => not doing anything");
		} else {

			UserProfile profile = userProfileRepo.findOne(event.getUsername());

			if (profile == null) {
				throw new DabPreConditionViolationException("cannot update an unexisting profile");
			}

			userProfileRepo.replacePersonalData(profile, event.getPersonalData());
			if (profile.getPdata().isLocationDifferent(event.getPersonalData())) {
				UserSummary updatedUserSummary = new UserSummary(event.getUsername(), event.getPersonalData(),profile.getMainPhoto(), profile.getPrivacySettings().isProfileActive());
				emitter.emit(new UserSummaryUpdated(updatedUserSummary));
			}

		}
	}

}
