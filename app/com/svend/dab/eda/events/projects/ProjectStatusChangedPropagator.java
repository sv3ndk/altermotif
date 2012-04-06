package com.svend.dab.eda.events.projects;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.svend.dab.core.beans.DabException;
import com.svend.dab.core.beans.groups.GroupSummary;
import com.svend.dab.core.beans.projects.Participant;
import com.svend.dab.core.beans.projects.Project;
import com.svend.dab.core.dao.IGroupDao;
import com.svend.dab.core.dao.IProjectDao;
import com.svend.dab.core.dao.IUserProfileDao;
import com.svend.dab.eda.IEventPropagator;

/**
 * @author svend
 *
 */
@Service("projectStatusChangedPropagator")
public class ProjectStatusChangedPropagator implements IEventPropagator<ProjectStatusChanged>{

	@Autowired
	private IProjectDao projetRepo;

	@Autowired
	private IUserProfileDao userProfileDao;

	@Autowired
	private IGroupDao groupDao;
	
	private static Logger logger = Logger.getLogger(ProjectStatusChangedPropagator.class.getName());
	
	public void propagate(ProjectStatusChanged event) throws DabException {
		
		if (event == null ) {
			logger.log(Level.WARNING, "Cannot propagate a project status update event: event is null or does not contain any summary");
			return;
		}
		
		Project updatedProject = projetRepo.findOne(event.getProjectId());
		
		if (updatedProject ==null) {
			logger.log(Level.WARNING, "Cannot propagate a project status update event: no project found for id ==" + event.getProjectId());
			return;
		}
		
		projetRepo.updateProjectStatus(updatedProject.getId(), event.getNewStatus());
		
		if (updatedProject.getParticipants() != null) {
			for (Participant participant : updatedProject.getParticipants()) {
				userProfileDao.updateProjectStatus(participant.getUser().getUserName(), event.getProjectId(), event.getNewStatus());
			}
		}
		
		
		if (updatedProject.getGroups() != null) {
			for (GroupSummary summary : updatedProject.getGroups()) {
				groupDao.updateProjectStatus(summary.getGroupId(), event.getProjectId(), event.getNewStatus());
			}
		}
		
	}

}
