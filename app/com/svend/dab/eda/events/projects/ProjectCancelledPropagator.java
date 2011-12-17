package com.svend.dab.eda.events.projects;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.svend.dab.core.beans.DabException;
import com.svend.dab.core.beans.projects.Participant;
import com.svend.dab.core.beans.projects.Project;
import com.svend.dab.core.beans.projects.Project.STATUS;
import com.svend.dab.dao.mongo.IProjectDao;
import com.svend.dab.eda.EventEmitter;
import com.svend.dab.eda.IEventPropagator;


/**
 * @author svend
 *
 */
@Service("projectCancelledPropagator")
public class ProjectCancelledPropagator implements IEventPropagator<ProjectCancelled> {

	@Autowired
	private IProjectDao projetRepo;
	
	@Autowired
	private EventEmitter eventEmitter;
	
	private static Logger logger = Logger.getLogger(ProjectCancelledPropagator.class.getName());
	
	@Override
	public void propagate(ProjectCancelled event) throws DabException {
		
		if (event == null ) {
			logger.log(Level.WARNING, "Cannot propagate a project participant removed event: event is null ");
			return;
		}
		
		Project project = projetRepo.findOne(event.getProjectId());
		
		if (project ==null ) {
			logger.log(Level.WARNING, "Cannot propagate a project canceled event: no project found for id ==" + event.getProjectId());
			return;
		}
		
		// this should be useless: the GUI should prevent to cancel a project which still has participants, but let's be paranoid 
		for (Participant participant : project.getParticipants()) {
			eventEmitter.emit(new ProjectParticipantRemoved(participant.getUser().getUserName(), event.getProjectId()));
		}

		projetRepo.updateProjectStatus(project.getId(), STATUS.cancelled);
		
	}

}
