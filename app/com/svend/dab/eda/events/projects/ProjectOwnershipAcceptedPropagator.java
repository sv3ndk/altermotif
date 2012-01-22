package com.svend.dab.eda.events.projects;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.svend.dab.core.beans.DabException;
import com.svend.dab.core.beans.profile.UserProfile;
import com.svend.dab.core.beans.projects.Participant;
import com.svend.dab.core.beans.projects.Participant.ROLE;
import com.svend.dab.core.beans.projects.Project;
import com.svend.dab.dao.mongo.IProjectDao;
import com.svend.dab.dao.mongo.IUserProfileDao;
import com.svend.dab.eda.IEventPropagator;

@Component("projectOwnershipAcceptedPropagator")
public class ProjectOwnershipAcceptedPropagator implements IEventPropagator<ProjectOwnershipAccepted> {

	@Autowired
	private IProjectDao projetRepo;

	@Autowired
	private IUserProfileDao userProfileDao;

	private static Logger logger = Logger.getLogger(ProjectOwnershipAcceptedPropagator.class.getName());

	public void propagate(ProjectOwnershipAccepted event) throws DabException {
		if (event == null ) {
			logger.log(Level.WARNING, "Cannot propagate a project ownership acceptation event: event is null ");
			return;
		}
		
		Project project = projetRepo.findOne(event.getProjectId());
		UserProfile profile = userProfileDao.retrieveUserProfileById(event.getUsername());
		
		if (project ==null || profile == null) {
			logger.log(Level.WARNING, "Cannot propagate a project ownership proposal event: no project found for id ==" + event.getProjectId() + " or no profile for username=" + event.getUsername());
			return;
		}
		
		for (Participant participant : project.getParticipants()) {
			projetRepo.updateOwnerShipProposed(event.getProjectId(), participant.getUser().getUserName(), false);
		}

		userProfileDao.updateProjectRole(event.getUsername(), event.getProjectId(), ROLE.initiator);
		projetRepo.updateParticipantRole(event.getProjectId(), event.getUsername(), ROLE.initiator);
		
		userProfileDao.updateProjectRole(event.getPreviousOwner(), event.getProjectId(), ROLE.admin);
		projetRepo.updateParticipantRole(event.getProjectId(), event.getPreviousOwner(), ROLE.admin);

	}

}
