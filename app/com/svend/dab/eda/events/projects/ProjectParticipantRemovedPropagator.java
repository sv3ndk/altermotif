package com.svend.dab.eda.events.projects;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.svend.dab.core.beans.DabException;
import com.svend.dab.core.beans.profile.UserProfile;
import com.svend.dab.core.beans.projects.Participant;
import com.svend.dab.core.beans.projects.Participation;
import com.svend.dab.core.beans.projects.Project;
import com.svend.dab.core.dao.IProjectDao;
import com.svend.dab.core.dao.IUserProfileDao;
import com.svend.dab.eda.IEventPropagator;

@Component("participantRemovedPropagator")
public class ProjectParticipantRemovedPropagator implements IEventPropagator<ProjectParticipantRemoved> {

	private static Logger logger = Logger.getLogger(ProjectParticipantRemovedPropagator.class.getName());
	
	@Autowired
	private IProjectDao projetRepo;

	@Autowired
	private IUserProfileDao userProfileDao;

	
	public void propagate(ProjectParticipantRemoved event) throws DabException {
		
		if (event == null ) {
			logger.log(Level.WARNING, "Cannot propagate a project participant removed event: event is null ");
			return;
		}
		
		Project project = projetRepo.findOne(event.getProjectId());
		UserProfile profile = userProfileDao.retrieveUserProfileById(event.getUserId());
		
		if (project ==null || profile == null) {
			logger.log(Level.WARNING, "Cannot propagate a project participant removed event: no project found for id ==" + event.getProjectId() + " or no profile for username=" + event.getUserId());
			return;
		}

		// in case of event retries, the user could already have been moved from participation to projects => just skipping that step in that case
		Participant existingParticipant = project.getParticipant(event.getUserId());
		if (existingParticipant != null) {
			projetRepo.removeParticipant(event.getProjectId(), event.getUserId());
		}
		
		Participation existingConfirmedParticipation = profile.getProject(event.getProjectId());
		if (existingConfirmedParticipation != null) {
			userProfileDao.removeParticipation(profile.getUsername(), event.getProjectId());
		}
	}
}
