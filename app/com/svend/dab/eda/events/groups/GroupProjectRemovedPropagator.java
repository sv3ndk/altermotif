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
import com.svend.dab.core.beans.projects.Project;
import com.svend.dab.core.dao.IGroupDao;
import com.svend.dab.core.dao.IProjectDao;
import com.svend.dab.eda.IEventPropagator;

/**
 * @author svend
 *
 */
@Service
public class GroupProjectRemovedPropagator implements IEventPropagator<GroupProjectRemoved> {

	private static Logger logger = Logger.getLogger(GroupProjectRemovedPropagator.class.getName());

	@Autowired
	private IGroupDao groupDao;
	
	@Autowired
	private IProjectDao projectDao;

	
	public void propagate(GroupProjectRemoved event) throws DabException {
		
		if (event != null && !Strings.isNullOrEmpty(event.getGroupId()) && !Strings.isNullOrEmpty(event.getProjectId())) {
			
			ProjectGroup group = groupDao.retrieveGroupById(event.getGroupId());
			Project project = projectDao.findOne(event.getProjectId());
			
			if (group == null || project == null) {
				logger.log(Level.WARNING, "refusing to propagate a GroupProjectRemovedPropagator: no group and/or no project found for project id = " + event.getProjectId() + " and groupid == " + event.getGroupId());
			} else {
				
				groupDao.removeProjectParticipant(event.getGroupId(), event.getProjectId());
				
				projectDao.removeParticipationInGroup(event.getProjectId(), event.getGroupId());
				
			}
		}

		
	}

}
