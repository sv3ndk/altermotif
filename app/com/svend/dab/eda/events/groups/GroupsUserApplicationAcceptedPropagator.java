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
import com.svend.dab.core.beans.groups.GroupParticipation;
import com.svend.dab.core.beans.groups.GroupSummary;
import com.svend.dab.core.beans.groups.ProjectGroup;
import com.svend.dab.core.beans.groups.GroupParticipant.ROLE;
import com.svend.dab.core.beans.profile.UserProfile;
import com.svend.dab.core.dao.IGroupDao;
import com.svend.dab.core.dao.IUserProfileDao;
import com.svend.dab.eda.IEventPropagator;

/**
 * @author svend
 * 
 */
@Service
public class GroupsUserApplicationAcceptedPropagator implements IEventPropagator<GroupsUserApplicationAccepted> {

	private static Logger logger = Logger.getLogger(GroupsUserApplicationAcceptedPropagator.class.getName());

	@Autowired
	private IGroupDao groupDao;

	@Autowired
	private IUserProfileDao userProfileRepo;

	public void propagate(GroupsUserApplicationAccepted event) throws DabException {

		if (event != null && !Strings.isNullOrEmpty(event.getGroupId()) && !Strings.isNullOrEmpty(event.getUserId())) {

			ProjectGroup group = groupDao.retrieveGroupById(event.getGroupId());
			UserProfile user = userProfileRepo.retrieveUserProfileById(event.getUserId());

			if (group == null || !group.isActive() || user == null || !user.getPrivacySettings().isProfileActive()) {
				logger.log(Level.WARNING, "refusing to propagate a GroupsUserApplicationAcceptedPropagator: group and/user not found or not active");
			} else {

				if (group.hasAppliedForGroupMembership(event.getUserId())) {
					userProfileRepo.addParticipationInGroup(event.getUserId(), new GroupParticipation(ROLE.member, new GroupSummary(group)));
					groupDao.setUserApplicationAcceptedStatus(event.getGroupId(), event.getUserId(), true);
				} else {
					logger.log(Level.WARNING, "refusing to propagate: no application found");
				}
			}

		} else {
			logger.log(Level.WARNING, "refusing to propagate a null GroupsUserApplicationAcceptedPropagator or event with null userid or group id");
		}

	}

}
