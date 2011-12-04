package com.svend.dab.eda.events.projects;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.svend.dab.core.beans.DabException;
import com.svend.dab.core.beans.profile.UserProfile;
import com.svend.dab.core.beans.profile.UserSummary;
import com.svend.dab.core.beans.projects.Participant;
import com.svend.dab.core.beans.projects.Participation;
import com.svend.dab.core.beans.projects.Project;
import com.svend.dab.core.beans.projects.Participant.ROLE;
import com.svend.dab.dao.mongo.IProjectDao;
import com.svend.dab.dao.mongo.IUserProfileDao;
import com.svend.dab.eda.IEventPropagator;

@Component("projectApplicationCancelledPropagator")
public class ProjectApplicationCancelledPropagator implements IEventPropagator<ProjectApplicationCancelled>{

	@Autowired
	private IProjectDao projetRepo;

	@Autowired
	private IUserProfileDao userProfileDao;
	
	private static Logger logger = Logger.getLogger(ProjectApplicationCancelledPropagator.class.getName());
	
	
	@Override
	public void propagate(ProjectApplicationCancelled event) throws DabException {
		
		if (event == null ) {
			logger.log(Level.WARNING, "Cannot propagate a project application cancellation event: event is null ");
			return;
		}
		
		Project project = projetRepo.findOne(event.getProjectId());
		UserProfile profile = userProfileDao.findOne(event.getUserId());
		
		if (project ==null || profile == null) {
			logger.log(Level.WARNING, "Cannot propagate a project application cancellation event: no project found for id ==" + event.getProjectId() + " or no profile for username=" + event.getUserId());
			return;
		}

		// in case of event retries, the user could already have been removed => just skipping that step in that case
		Participant existingParticipant = project.getParticipation(event.getUserId());
		if (existingParticipant != null) {
			
			// no idea why a simple "remove" does not work in that case...
			List<Participant> newPList = new LinkedList<Participant>();
			for (Participant p : project.getParticipants()) {
				if (!p.getUser().getUserName().equals(existingParticipant.getUser().getUserName())) {
					newPList.add(p);
				}
			}
			
			projetRepo.updateParticipantList(event.getProjectId(), newPList);
		}
		
		// same idea as above
		Participation existingParticipation = profile.getApplication(event.getProjectId());
		if (existingParticipation != null) {
			userProfileDao.removeParticipation(profile.getUsername(), existingParticipation);
			
		}

		
	}

}
