package com.svend.dab.eda.events.projects;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.svend.dab.core.beans.DabException;
import com.svend.dab.core.beans.profile.UserProfile;
import com.svend.dab.core.beans.projects.Participant;
import com.svend.dab.core.beans.projects.Participant.ROLE;
import com.svend.dab.core.beans.projects.Participation;
import com.svend.dab.core.beans.projects.Project;
import com.svend.dab.dao.mongo.IProjectDao;
import com.svend.dab.dao.mongo.IUserProfileDao;
import com.svend.dab.eda.IEventPropagator;

@Component("projectApplicationEventPropagator")
public class ProjectApplicationEventPropagator implements IEventPropagator<ProjectApplicationEvent>{
	
	@Autowired
	private IProjectDao projetRepo;

	@Autowired
	private IUserProfileDao userProfileDao;
	
	private static Logger logger = Logger.getLogger(ProjectMainPhotoUpdatedPropagator.class.getName());

	
	@Override
	public void propagate(ProjectApplicationEvent event) throws DabException {

		if (event == null ) {
			logger.log(Level.WARNING, "Cannot propagate a project application event: event is null ");
			return;
		}
		
		Project project = projetRepo.findOne(event.getProjectId());
		UserProfile profile = userProfileDao.findOne(event.getApplyingUserId());
		
		if (project ==null || profile == null) {
			logger.log(Level.WARNING, "Cannot propagate a project application event: no project found for id ==" + event.getProjectId() + " or no profile for username=" + event.getApplyingUserId());
			return;
		}

		// in case of event retries, the user could already have been inserted => just doing nothing in that case
		if (! project.isUserAlreadyApplying(event.getApplyingUserId()) && ! project.isUserAlreadyMember(event.getApplyingUserId())) {
			Participant createdParticipant = new Participant(ROLE.member, profile, event.getApplicationText());
			createdParticipant.setAccepted(false);
			projetRepo.addOneParticipant(event.getProjectId(), createdParticipant);
		}
		
		// same idea as above
		if (! profile.isMemberOfOrHasAppliedTo(project.getId())) {
			userProfileDao.addProjectParticipation(profile, new Participation(project, ROLE.member, false));
		}
		
	}

}
