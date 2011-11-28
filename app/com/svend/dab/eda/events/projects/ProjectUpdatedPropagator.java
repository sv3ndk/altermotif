package com.svend.dab.eda.events.projects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.svend.dab.core.beans.DabException;
import com.svend.dab.dao.mongo.IProjectDao;
import com.svend.dab.eda.IEventPropagator;

@Component("projectUpdatedPropagator")
public class ProjectUpdatedPropagator implements IEventPropagator<ProjectUpdated> {

	@Autowired
	private IProjectDao projetRepo;

	@Override
	public void propagate(ProjectUpdated event) throws DabException {
		projetRepo.updateProjectPDataLinksAndTags(event.getUpdatedProject().getId(), event.getUpdatedProject());
	}

	public void setProjetRepo(IProjectDao projetRepo) {
		this.projetRepo = projetRepo;
	}

}
