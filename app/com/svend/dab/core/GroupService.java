/**
 * 
 */
package com.svend.dab.core;

import java.util.UUID;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.svend.dab.core.beans.groups.ProjectGroup;
import com.svend.dab.eda.EventEmitter;
import com.svend.dab.eda.groups.GroupCreated;

/**
 * @author svend
 *
 */
@Service
public class GroupService implements IGroupService {

	private static Logger logger = Logger.getLogger(GroupService.class.getName());

	@Autowired
	private EventEmitter eventEmitter;

	
	/* (non-Javadoc)
	 * @see com.svend.dab.core.IGroupService#createNewGroup(com.svend.dab.core.beans.groups.Group, java.lang.String)
	 */
	public void createNewGroup(ProjectGroup createdGroup, String creatorId) {
		if (createdGroup != null && ! Strings.isNullOrEmpty(creatorId)) {
			createdGroup.setId(UUID.randomUUID().toString().replace("-", ""));
			eventEmitter.emit(new GroupCreated(createdGroup, creatorId));
		}
	}

}
