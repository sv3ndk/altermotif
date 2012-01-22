package com.svend.dab.eda.events.projects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.svend.dab.core.beans.DabException;
import com.svend.dab.core.beans.projects.Asset;
import com.svend.dab.core.beans.projects.Task;
import com.svend.dab.core.projects.IProjectFTSService;
import com.svend.dab.dao.mongo.IProjectDao;
import com.svend.dab.eda.IEventPropagator;

@Component("projectUpdatedPropagator")
public class ProjectUpdatedPropagator implements IEventPropagator<ProjectUpdated> {

	@Autowired
	private IProjectDao projetRepo;
	
	@Autowired
	private IProjectFTSService projectFTSService;

	public void propagate(ProjectUpdated event) throws DabException {
		projetRepo.updateProjectPDataAndLinksAndTagsAndThemes(event.getUpdatedProject().getId(), event.getUpdatedProject());
		
		if (event.getUpdatedTasks() != null) {
			for (Task newOrUpdatedTask : event.getUpdatedTasks()) {
				projetRepo.addOrUpdateProjectTasks(event.getUpdatedProject().getId(), newOrUpdatedTask);
			}
		}
		
		if (event.getRemovedTasksIds() != null) {
			for (String removedTasksId : event.getRemovedTasksIds()) {
				projetRepo.removeTaskFromProject(event.getUpdatedProject().getId(), removedTasksId);
			}
		}

		if (event.getUpdatedAssets() != null) {
			for (Asset newOrUpdatedAsset : event.getUpdatedAssets()) {
				projetRepo.addOrUpdateProjectAsset(event.getUpdatedProject().getId(), newOrUpdatedAsset);
			}
		}
		
		if (event.getRemovedAssetsIds() != null) {
			for (String removedAssetId : event.getRemovedAssetsIds()) {
				projetRepo.removeAssetFromProject(event.getUpdatedProject().getId(), removedAssetId);
			}
		}
		
		projectFTSService.updateProjetIndex(event.getUpdatedProject().getId(), false);
	}

	public void setProjetRepo(IProjectDao projetRepo) {
		this.projetRepo = projetRepo;
	}

}
