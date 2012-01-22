package com.svend.dab.eda.events.projects;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.svend.dab.core.beans.DabException;
import com.svend.dab.core.beans.profile.UserProfile;
import com.svend.dab.core.beans.projects.Project;
import com.svend.dab.dao.mongo.IProjectDao;
import com.svend.dab.dao.mongo.IUserProfileDao;
import com.svend.dab.eda.IEventPropagator;

@Service("userProjectRoleUpdatedPropagator")
public class UserProjectRoleUpdatedPropagator implements IEventPropagator<UserProjectRoleUpdated>{

	@Autowired
	private IProjectDao projetRepo;

	@Autowired
	private IUserProfileDao userProfileDao;
	
	private static Logger logger = Logger.getLogger(UserProjectRoleUpdatedPropagator.class.getName());
	
	
	public void propagate(UserProjectRoleUpdated event) throws DabException {

		if (event == null ) {
			logger.log(Level.WARNING, "Cannot propagate a project application cancellation event: event is null ");
			return;
		}
		
		Project project = projetRepo.findOne(event.getProjectId());
		UserProfile profile = userProfileDao.retrieveUserProfileById(event.getUserId());
		
		if (project ==null || profile == null) {
			logger.log(Level.WARNING, "Cannot propagate a project role update event: no project found for id ==" + event.getProjectId() + " or no profile for username=" + event.getUserId());
			return;
		}
		
		projetRepo.updateParticipantRole(event.getProjectId(), event.getUserId(), event.getRole());
		
		userProfileDao.updateProjectRole(event.getUserId(), event.getProjectId(), event.getRole());
		
	}

}
