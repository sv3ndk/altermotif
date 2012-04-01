package com.svend.dab.eda.events.groups;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.svend.dab.core.beans.DabException;
import com.svend.dab.core.dao.IProjectDao;
import com.svend.dab.core.dao.IUserProfileDao;
import com.svend.dab.core.groups.IGroupFtsService;
import com.svend.dab.eda.IEventPropagator;

/**
 * @author svend
 * 
 */

@Service
public class GroupSummaryUpdatedPropagator implements IEventPropagator<GroupSummaryUpdated> {

	@Autowired
	private IUserProfileDao userProfileRepo;

	@Autowired
	private IProjectDao projectDao;

	@Autowired
	private IGroupFtsService groupFtsService;

	public void propagate(GroupSummaryUpdated event) throws DabException {

		if (event != null && event.getUpdatedSummary() != null) {

			userProfileRepo.updateGroupSummaryOfAllUsersPartOf(event.getUpdatedSummary());
			projectDao.updateGroupSummaryOfAllProjectsPartOf(event.getUpdatedSummary());
			groupFtsService.updateGroupIndex(event.getUpdatedSummary().getGroupId(), false);

		}
	}
}
