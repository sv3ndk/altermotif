package com.svend.dab.eda.events.groups;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.svend.dab.core.beans.DabException;
import com.svend.dab.core.beans.groups.GroupSummary;
import com.svend.dab.core.beans.groups.ProjectGroup;
import com.svend.dab.core.beans.projects.Project;
import com.svend.dab.core.dao.IGroupDao;
import com.svend.dab.core.dao.IGroupIndexDao;
import com.svend.dab.core.dao.IProjectDao;
import com.svend.dab.eda.IEventPropagator;

/**
 * @author svend
 * 
 */
@Service
public class GroupProjectApplicationAcceptedPropagator implements IEventPropagator<GroupProjectApplicationAccepted> {

	private static Logger logger = Logger.getLogger(GroupProjectApplicationAcceptedPropagator.class.getName());

	@Autowired
	private IGroupDao groupDao;

	@Autowired
	private IProjectDao projectDao;

	@Autowired
	private IGroupIndexDao groupIndexDao;

	public void propagate(GroupProjectApplicationAccepted event) throws DabException {

		if (event != null && !Strings.isNullOrEmpty(event.getGroupId()) && !Strings.isNullOrEmpty(event.getProjectId())) {

			ProjectGroup group = groupDao.retrieveGroupById(event.getGroupId());
			Project project = projectDao.findOne(event.getProjectId());

			if (group == null || project == null) {
				logger.log(Level.WARNING, "refusing to propagate a GroupProjectApplicationAcceptedPropagator: no group and/or no project found for project id = " + event.getProjectId()
						+ " and groupid == " + event.getGroupId());
			} else {
				groupDao.setProjectApplicationAcceptedStatus(event.getGroupId(), event.getProjectId(), true);

				if (!project.isPartOfGroup(event.getGroupId())) {
					projectDao.addOneGroup(event.getProjectId(), new GroupSummary(group));
				}

				groupIndexDao.updateIndex(event.getGroupId(), false);

			}
		} else {
			logger.log(Level.WARNING, "refusing to propagate a null GroupProjectApplicationAcceptedPropagator or event with null projectId or group id");
		}

	}

}
