package com.svend.dab.eda.events.groups;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.svend.dab.core.beans.DabException;
import com.svend.dab.core.beans.groups.ProjectGroup;
import com.svend.dab.core.beans.profile.UserProfile;
import com.svend.dab.core.dao.IGroupDao;
import com.svend.dab.core.dao.IGroupIndexDao;
import com.svend.dab.core.dao.IUserProfileDao;
import com.svend.dab.eda.IEventPropagator;

/**
 * @author svend
 * 
 */
@Service
public class GroupUserRemovedPropagator implements IEventPropagator<GroupUserRemoved> {

	private static Logger logger = Logger.getLogger(GroupUserRemovedPropagator.class.getName());
	
	@Autowired
	private IGroupDao groupDao;

	@Autowired
	private IUserProfileDao userProfileRepo;

	@Autowired
	private IGroupIndexDao groupIndexDao;

	public void propagate(GroupUserRemoved event) throws DabException {
		if (event != null && ! Strings.isNullOrEmpty(event.getGroupId()) && ! Strings.isNullOrEmpty(event.getUserId())) {
			
			ProjectGroup group = groupDao.retrieveGroupById(event.getGroupId());
			UserProfile user = userProfileRepo.retrieveUserProfileById(event.getUserId());
			groupIndexDao.updateIndex(event.getGroupId(), false);

			if (group == null || ! group.isActive() || user == null || ! user.getPrivacySettings().isProfileActive()) {
				logger.log(Level.WARNING, "refusing to propagate a GroupUserRemovedPropagator: group and/user not found or not active");
			} else {
				userProfileRepo.removeParticipationInGroup(event.getUserId(), event.getGroupId());
				groupDao.removeUserParticipant(event.getGroupId(), event.getUserId());
			}
			
		} else {
			logger.log(Level.WARNING, "refusing to propagate a null GroupsUserApplicationAcceptedPropagator or event with null userid or group id");
		}
	}

}
