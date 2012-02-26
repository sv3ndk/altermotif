package com.svend.dab.eda.events.groups;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.svend.dab.core.beans.DabException;
import com.svend.dab.core.dao.IGroupDao;
import com.svend.dab.core.dao.IUserProfileDao;
import com.svend.dab.eda.IEventPropagator;

/**
 * @author svend
 *
 */
@Service
public class GroupUserParticipantRemovedPropagator implements IEventPropagator<GroupUserParticipantRemoved> {

	private static Logger logger = Logger.getLogger(GroupUserParticipantRemovedPropagator.class.getName());
	
	@Autowired
	private IGroupDao groupDao;

	@Autowired
	private IUserProfileDao userProfileDao;

	
	public void propagate(GroupUserParticipantRemoved event) throws DabException {
		
		if (event != null && ! Strings.isNullOrEmpty(event.getGroupId()) && ! Strings.isNullOrEmpty(event.getParticipantId())) {
			
			groupDao.removeUserParticipant(event.getGroupId(), event.getParticipantId());
			userProfileDao.removeParticipationInGroup(event.getParticipantId(), event.getGroupId());
			
		} else {
			logger.log(Level.WARNING, "refusing to propagate a null GroupUserParticipantRemoved or event with a null group id or user id");
		}
		
	}

}
