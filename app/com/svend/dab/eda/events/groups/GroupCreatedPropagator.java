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
import com.svend.dab.core.beans.groups.GroupParticipant;
import com.svend.dab.core.beans.groups.GroupParticipant.ROLE;
import com.svend.dab.core.beans.groups.GroupParticipation;
import com.svend.dab.core.beans.groups.GroupSummary;
import com.svend.dab.core.beans.profile.UserProfile;
import com.svend.dab.core.beans.profile.UserSummary;
import com.svend.dab.core.dao.IGroupDao;
import com.svend.dab.core.dao.IUserProfileDao;
import com.svend.dab.core.groups.IGroupFtsService;
import com.svend.dab.eda.IEventPropagator;

/**
 * @author svend
 *
 */
@Service
public class GroupCreatedPropagator implements IEventPropagator<GroupCreated> {

	private static Logger logger = Logger.getLogger(GroupCreatedPropagator.class.getName());
	
	@Autowired
	private IUserProfileDao userProfileRepo;
	
	@Autowired
	private IGroupDao groupDao;
	
	@Autowired
	private IGroupFtsService groupFtsService;
	
	public void propagate(GroupCreated event) throws DabException {
		
		ProjectGroup createGroup = event.getCreatedGroup();
		
		if (createGroup != null && !Strings.isNullOrEmpty(createGroup.getId()) && ! Strings.isNullOrEmpty(event.getCreatorUserId())) {
			
			UserProfile creator = userProfileRepo.retrieveUserProfileById(event.getCreatorUserId());
			
			if (creator != null) {
				createGroup.addParticipant(new GroupParticipant(ROLE.admin, new UserSummary(creator)));
				
				if (groupDao.retrieveGroupById(createGroup.getId()) == null) {
					// in case of retry, this makes sure the group is not created several times...
					groupDao.save(createGroup);
				}
				
				if (creator.retrieveParticipationInGroup(createGroup.getId()) == null) {
					userProfileRepo.addParticipationInGroup(creator.getUsername(), new GroupParticipation(ROLE.admin, new GroupSummary(createGroup)));
				}
				
				groupFtsService.updateGroupIndex(event.getCreatedGroup().getId(), false);
				
			} else {
				logger.log(Level.WARNING, "Not creating a new project group: no user found for this userid: " + event.getCreatorUserId());
			}
			
		} else {
			logger.log(Level.WARNING, "Not creating a new project group: null group or groupid or creator userid");
		}
	}

}
