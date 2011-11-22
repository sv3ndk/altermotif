package com.svend.dab.eda.events.profile;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.svend.dab.core.beans.DabException;
import com.svend.dab.core.beans.DabPreConditionViolationException;
import com.svend.dab.core.beans.profile.UserProfile;
import com.svend.dab.core.beans.profile.UserReference;
import com.svend.dab.core.beans.profile.UserSummary;
import com.svend.dab.dao.mongo.IUserProfileDao;
import com.svend.dab.eda.IEventPropagator;

/**
 * @author svend
 * 
 */
@Service
public class UserReferenceWrittenEventPropagator implements IEventPropagator<UserReferenceWritten> {

	@Autowired
	private IUserProfileDao userProfileRepo;
	
	private static Logger logger = Logger.getLogger(UserReferenceWrittenEventPropagator.class.getName());

	@Override
	public void propagate(UserReferenceWritten event) throws DabException {
		
		logger.log(Level.INFO, "leaving a reference: from " + event.getFromUserName() + " to " + event.getToUserName() + " test is " + event.getText()) ;

		if (event != null && event.getFromUserName() != null && event.getToUserName() != null) {

			UserProfile fromProfile = userProfileRepo.findOne(event.getFromUserName());
			UserProfile toProfile = userProfileRepo.findOne(event.getToUserName());

			if (fromProfile == null || toProfile == null) {
				throw new DabPreConditionViolationException("Cannot propagate an reference left event: from or to user name does not exist");
			}

			UserReference createdReference = new UserReference();
			createdReference.setId(event.getReferenceId());
			createdReference.setCreationDate(event.getCreationDate());
			createdReference.setText(event.getText());
			createdReference.setFromUser(new UserSummary(fromProfile));
			createdReference.setToUser(new UserSummary(toProfile));

			userProfileRepo.addReceivedReference(toProfile, createdReference);
			userProfileRepo.addWrittenReference(fromProfile, createdReference);

		} else {
			throw new DabPreConditionViolationException("Cannot propagate an reference left event: null or null userReference or null from user name");
		}

	}

}
