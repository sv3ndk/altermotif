package com.svend.dab.eda.events.projects;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.svend.dab.core.beans.DabException;
import com.svend.dab.core.beans.profile.UserProfile;
import com.svend.dab.core.beans.projects.Participant;
import com.svend.dab.core.beans.projects.Project;
import com.svend.dab.dao.mongo.IProjectDao;
import com.svend.dab.dao.mongo.IUserProfileDao;
import com.svend.dab.eda.IEventPropagator;

/**
 * @author svend
 *
 */
@Service("projectOwnershipProposedPropagator")
public class ProjectOwnershipProposedPropagator implements IEventPropagator<ProjectOwnershipProposed>{

	@Autowired
	private IProjectDao projetRepo;

	@Autowired
	private IUserProfileDao userProfileDao;

	private static Logger logger = Logger.getLogger(ProjectOwnershipProposedPropagator.class.getName());
	
	@Override
	public void propagate(ProjectOwnershipProposed event) throws DabException {
		
		if (event == null ) {
			logger.log(Level.WARNING, "Cannot propagate a project ownership proposal event: event is null ");
			return;
		}
		
		Project project = projetRepo.findOne(event.getProjectId());
		UserProfile profile = userProfileDao.retrieveUserProfileById(event.getUsername());
		
		if (project ==null || profile == null) {
			logger.log(Level.WARNING, "Cannot propagate a project ownership proposal event: no project found for id ==" + event.getProjectId() + " or no profile for username=" + event.getUsername());
			return;
		}
		
		
		for (Participant participant : project.getParticipants()) {
			if (event.getUsername().equals(participant.getUser().getUserName())) {
				projetRepo.updateOwnerShipProposed(event.getProjectId(), participant.getUser().getUserName(), true);
			} else {
				projetRepo.updateOwnerShipProposed(event.getProjectId(), participant.getUser().getUserName(), false);
			}
		}
		
		
	}

}
