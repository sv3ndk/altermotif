/**
 * 
 */
package com.svend.dab.eda.events.profile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.svend.dab.core.beans.DabException;
import com.svend.dab.core.beans.DabPreConditionViolationException;
import com.svend.dab.core.beans.profile.ProfileRef;
import com.svend.dab.core.beans.profile.UserProfile;
import com.svend.dab.core.beans.profile.UserSummary;
import com.svend.dab.core.dao.IUserProfileDao;
import com.svend.dab.eda.EventEmitter;
import com.svend.dab.eda.IEventPropagator;

/**
 * @author Svend
 *
 */
@Component
public class UserPrivacySettingsUpdatedPropagator implements IEventPropagator<UserPrivacySettingsUpdatedEvent> {


	@Autowired
	IUserProfileDao userProfileRepo;
	
	@Autowired
	private EventEmitter emitter;
	
	public void propagate(UserPrivacySettingsUpdatedEvent event) throws DabException {
		
		UserProfile profile = userProfileRepo.retrieveUserProfileById(event.getUserId());
		
		if (profile == null) {
			throw new DabPreConditionViolationException("cannot propagate privacy settinsg: unexisting profile for " + event.getUserId());
		}
		
		userProfileRepo.replacePrivacySettings(event.getUserId(), event.getNewPrivacySettings());
		
		if (profile.getPrivacySettings().isProfileActive() != event.getNewPrivacySettings().isProfileActive()) {
			emitter.emit(new UserSummaryUpdated(new UserSummary(event.getUserId(), profile.getPdata(), profile.getMainPhoto(), event.getNewPrivacySettings().isProfileActive())));
			emitter.emit(new ProfileRefUpdated(new ProfileRef(event.getUserId(), event.getNewPrivacySettings().isProfileActive())));
		}
	}

}
