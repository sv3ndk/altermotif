package com.svend.dab.eda.events.groups;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.svend.dab.core.beans.DabException;
import com.svend.dab.core.beans.groups.GroupSummary;
import com.svend.dab.core.dao.IGroupDao;
import com.svend.dab.core.groups.IGroupFtsService;
import com.svend.dab.eda.EventEmitter;
import com.svend.dab.eda.IEventPropagator;

@Service
public class GroupUpdatedPropagator implements IEventPropagator<GroupUpdatedEvent> {

	@Autowired
	private EventEmitter eventEmitter;

	@Autowired
	private IGroupDao groupDao;

	@Autowired
	private IGroupFtsService groupFtsService;

	public void propagate(GroupUpdatedEvent event) throws DabException {
		groupDao.updateGroupData(event.getUpdated());
		groupFtsService.updateGroupIndex(event.getUpdated().getId(), false);

		// TODO: optimization: we could emit this only if the summary has changed (i.e the group name has changed)
		eventEmitter.emit(new GroupSummaryUpdated(new GroupSummary(event.getUpdated())));
	}

}
