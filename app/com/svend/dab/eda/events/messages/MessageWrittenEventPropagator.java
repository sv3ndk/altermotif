package com.svend.dab.eda.events.messages;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.svend.dab.core.beans.DabException;
import com.svend.dab.core.beans.DabPreConditionViolationException;
import com.svend.dab.core.beans.message.UserMessage;
import com.svend.dab.core.beans.profile.ProfileRef;
import com.svend.dab.core.beans.profile.UserProfile;
import com.svend.dab.core.dao.IUserMessageDao;
import com.svend.dab.dao.mongo.IUserProfileDao;
import com.svend.dab.eda.IEventPropagator;

/**
 * @author svend
 *
 */

@Service
public class MessageWrittenEventPropagator implements IEventPropagator<MessageWrittenEvent>{

	@Autowired
	private IUserMessageDao userMessageDao;
	
	
	@Autowired
	private IUserProfileDao userProfileRepo;
	
	@Override
	public void propagate(MessageWrittenEvent event) throws DabException {
		
		UserProfile fromUser = userProfileRepo.findOne(event.getFromUserName());
		UserProfile toUser = userProfileRepo.findOne(event.getToUserName());

		if (fromUser == null) {
			throw new DabPreConditionViolationException("cannot propagate a message emission event: fromUser does not (or no longer) exist: " + event.getFromUserName());
		}

		if (toUser == null) {
			throw new DabPreConditionViolationException("cannot propagate a message emission event: toUser does not (or no longer) exist: " + event.getToUserName());
		}
		
		userMessageDao.save(new UserMessage(new ProfileRef(fromUser), new ProfileRef(toUser), event.getSubject(), event.getContent(), event.getCreationDate()));
	}

}
