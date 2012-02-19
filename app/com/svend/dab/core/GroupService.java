/**
 * 
 */
package com.svend.dab.core;

import java.util.Date;
import java.util.UUID;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.svend.dab.core.beans.Config;
import com.svend.dab.core.beans.groups.ProjectGroup;
import com.svend.dab.core.dao.IGroupDao;
import com.svend.dab.eda.EventEmitter;
import com.svend.dab.eda.events.groups.GroupClosed;
import com.svend.dab.eda.events.groups.GroupCreated;
import com.svend.dab.eda.events.groups.GroupUpdatedEvent;

/**
 * @author svend
 *
 */
@Service
public class GroupService implements IGroupService {

	private static Logger logger = Logger.getLogger(GroupService.class.getName());

	@Autowired
	private EventEmitter eventEmitter;
	
	@Autowired
	private IGroupDao groupDao;

	@Autowired
	private Config config;

	
	/* (non-Javadoc)
	 * @see com.svend.dab.core.IGroupService#createNewGroup(com.svend.dab.core.beans.groups.Group, java.lang.String)
	 */
	public void createNewGroup(ProjectGroup createdGroup, String creatorId) {
		if (createdGroup != null && ! Strings.isNullOrEmpty(creatorId)) {
			createdGroup.setId(UUID.randomUUID().toString().replace("-", ""));
			createdGroup.setCreationDate(new Date());
			eventEmitter.emit(new GroupCreated(createdGroup, creatorId));
		}
	}


	public ProjectGroup loadGroupById(String groupId, boolean preparePresignedLinks) {
		
		ProjectGroup group = groupDao.retrieveGroupById(groupId);
		
		if (group != null && preparePresignedLinks) {

			Date expirationdate = new Date();
			expirationdate.setTime(expirationdate.getTime() + config.getPhotoExpirationDelayInMillis());
			group.generatePhotoLinks(expirationdate);

			
			// TODO: prepare presigned URL here (when photos will be there...)
		}
		
		return group;
	}


	public void updateGroupData(ProjectGroup editedGroup) {
		if (editedGroup != null) {
			eventEmitter.emit(new GroupUpdatedEvent(editedGroup));
		}
	}


	public void closeGroup(String groupId) {
		if (!Strings.isNullOrEmpty(groupId)) {
			eventEmitter.emit(new GroupClosed(groupId));
		}
	}
	

}
