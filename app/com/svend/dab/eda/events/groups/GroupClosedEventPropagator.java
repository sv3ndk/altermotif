/**
 * 
 */
package com.svend.dab.eda.events.groups;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.svend.dab.core.beans.DabException;
import com.svend.dab.core.beans.groups.GroupParticipant;
import com.svend.dab.core.beans.groups.ProjectGroup;
import com.svend.dab.core.dao.IGroupDao;
import com.svend.dab.eda.EventEmitter;
import com.svend.dab.eda.IEventPropagator;

/**
 * @author svend
 *
 */
@Service
public class GroupClosedEventPropagator implements IEventPropagator<GroupClosed> {

	
	@Autowired
	private IGroupDao groupDao;
	
	@Autowired
	private EventEmitter eventEmitter;

	
	private static Logger logger = Logger.getLogger(GroupClosedEventPropagator.class.getName());

	
	
	public void propagate(GroupClosed event) throws DabException {

		if (event != null && event.getGroupId() != null) {
			
			ProjectGroup updateGroup = groupDao.retrieveGroupById(event.getGroupId());
			if (updateGroup == null) {
				logger.log(Level.WARNING, "refusing to propagate a GroupClosedEventPropagator: no group found for id " + event.getGroupId());
				
			} else {
				
				// removing all particpant: normally only the last admin should still be there
				if (updateGroup.getParticipants() != null) {
					for (GroupParticipant participant : updateGroup.getParticipants()) {
						eventEmitter.emit(new GroupUserParticipantRemoved(event.getGroupId(), participant.getUser().getUserName()));
					}
				}
				
				
				// TODO: emit "remove project from group" event here (should be useless though...)
				
				groupDao.updateGroupActiveStatus(event.getGroupId(), false);
			}
			
			
		} else {
			logger.log(Level.WARNING, "refusing to propagate a null GroupClosedEventPropagator or event with a null group id");
		}
		
		
		
	}

}
