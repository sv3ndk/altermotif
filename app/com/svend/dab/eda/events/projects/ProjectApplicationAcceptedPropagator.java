package com.svend.dab.eda.events.projects;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.svend.dab.core.beans.DabException;
import com.svend.dab.core.beans.profile.UserProfile;
import com.svend.dab.core.beans.projects.Participant;
import com.svend.dab.core.beans.projects.Participation;
import com.svend.dab.core.beans.projects.Project;
import com.svend.dab.dao.mongo.IProjectDao;
import com.svend.dab.dao.mongo.IUserProfileDao;
import com.svend.dab.eda.IEventPropagator;

/**
 * @author svend
 *
 */
@Service("projectApplicationAcceptedPropagator")
public class ProjectApplicationAcceptedPropagator implements IEventPropagator<ProjectApplicationAccepted>{

	@Autowired
	private IProjectDao projetRepo;

	@Autowired
	private IUserProfileDao userProfileDao;

	private static Logger logger = Logger.getLogger(ProjectApplicationAcceptedPropagator.class.getName());
	
	
	@Override
	public void propagate(ProjectApplicationAccepted event) throws DabException {
		if (event == null ) {
			logger.log(Level.WARNING, "Cannot propagate a project application acceptation event: event is null ");
			return;
		}
		
		Project project = projetRepo.findOne(event.getProjectId());
		UserProfile profile = userProfileDao.retrieveUserProfileById(event.getUserId());
		
		if (project ==null || profile == null) {
			logger.log(Level.WARNING, "Cannot propagate a project application acceptation event: no project found for id ==" + event.getProjectId() + " or no profile for username=" + event.getUserId());
			return;
		}

		// in case of event retries, the user could already have been moved from participation to projects => just skipping that step in that case
		Participant existingParticipant = project.getParticipation(event.getUserId());
		if (existingParticipant != null) {
			projetRepo.markParticipantAsAccepted(event.getProjectId(), event.getUserId());
		}
		
		Participation participation = profile.getApplication(event.getProjectId());
		if (participation != null) {
			userProfileDao.markParticipationHasAccepted(event.getUserId(), event.getProjectId());
		}
	}

}
