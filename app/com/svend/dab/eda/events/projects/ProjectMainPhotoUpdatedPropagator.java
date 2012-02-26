package com.svend.dab.eda.events.projects;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.svend.dab.core.beans.DabException;
import com.svend.dab.core.beans.projects.Participant;
import com.svend.dab.core.beans.projects.Project;
import com.svend.dab.core.dao.IProjectDao;
import com.svend.dab.core.dao.IUserProfileDao;
import com.svend.dab.eda.IEventPropagator;

@Component("projectMainPhotoUpdatedPropagator")
public class ProjectMainPhotoUpdatedPropagator implements IEventPropagator<ProjectMainPhotoUpdated>{

	@Autowired
	private IProjectDao projetRepo;

	@Autowired
	private IUserProfileDao userProfileDao;
	
	private static Logger logger = Logger.getLogger(ProjectMainPhotoUpdatedPropagator.class.getName());
	
	
	public void propagate(ProjectMainPhotoUpdated event) throws DabException {
		
		if (event == null ) {
			logger.log(Level.WARNING, "Cannot propagate a project photo updated event: event is null or does not contain any summary");
			return;
		}
		
		Project updatedProject = projetRepo.findOne(event.getProjectId());
		
		if (updatedProject ==null) {
			logger.log(Level.WARNING, "Cannot propagate a project photo updated event: no project found for id ==" + event.getProjectId());
			return;
		}
		
		if (updatedProject.getParticipants() != null) {
			for (Participant participant : updatedProject.getParticipants()) {
				userProfileDao.updateProjectMainPhoto(participant.getUser().getUserName(), event.getProjectId(), event.getMainPhoto());
			}
		}
		
	}

}
