package com.svend.dab.eda.events.groups;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.svend.dab.core.beans.DabException;
import com.svend.dab.core.dao.IUserProfileDao;
import com.svend.dab.eda.IEventPropagator;

/**
 * @author svend
 *
 */

@Service
public class GroupSummaryUpdatedPropagator implements IEventPropagator<GroupSummaryUpdated> {

	
	@Autowired
	private IUserProfileDao userProfileRepo;

	
	public void propagate(GroupSummaryUpdated event) throws DabException {
		
		if (event != null && event.getUpdatedSummary() != null) {
			
			userProfileRepo.updateGroupSummaryOfAllUsersPartOf(event.getUpdatedSummary());
			
			// TODO: update projects as well here (as soon as projets can apply to groups)
			
			
		}
		
		
	}

}
