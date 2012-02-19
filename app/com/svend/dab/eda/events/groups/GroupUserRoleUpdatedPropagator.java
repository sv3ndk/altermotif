/**
 * 
 */
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
import com.svend.dab.core.dao.IUserProfileDao;
import com.svend.dab.eda.IEventPropagator;

/**
 * @author svend
 * 
 */
@Service
public class GroupUserRoleUpdatedPropagator implements IEventPropagator<GroupUserRoleUpdated> {

	@Autowired
	private IGroupDao groupDao;

	@Autowired
	private IUserProfileDao userProfileRepo;
	
	private static Logger logger = Logger.getLogger(GroupUserRoleUpdatedPropagator.class.getName());

	public void propagate(GroupUserRoleUpdated event) throws DabException {
		
		if (event == null || Strings.isNullOrEmpty(event.getGroupId() ) || Strings.isNullOrEmpty(event.getGroupId())) {
			logger.log(Level.WARNING, "refusing to propagate a GroupUserRoleUpdatedPropagator: null event or null group id or null profile id");
		} else {
			
			
			ProjectGroup group = groupDao.retrieveGroupById(event.getGroupId());
			UserProfile user = userProfileRepo.retrieveUserProfileById(event.getUserId());

			if (group == null || !group.isActive() || user == null || !user.getPrivacySettings().isProfileActive()) {
				logger.log(Level.WARNING, "refusing to propagate a GroupUserRoleUpdatedPropagator: group and/user not found or not active");
			} else {
				groupDao.updateParticipantRole(event.getGroupId(), event.getUserId(), event.getRole());
				userProfileRepo.updateGroupParticipationRole(event.getUserId(), event.getGroupId(), event.getRole());
			}
			
		}

	}

}
