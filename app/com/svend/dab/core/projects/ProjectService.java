package com.svend.dab.core.projects;

import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;
import com.svend.dab.core.beans.Config;
import com.svend.dab.core.beans.projects.Project;
import com.svend.dab.core.beans.projects.Project.STATUS;
import com.svend.dab.dao.mongo.IProjectDao;
import com.svend.dab.eda.EventEmitter;
import com.svend.dab.eda.events.projects.ProjectApplicationCancelled;
import com.svend.dab.eda.events.projects.ProjectApplicationEvent;
import com.svend.dab.eda.events.projects.ProjectCreated;
import com.svend.dab.eda.events.projects.ProjectUpdated;

@Component("projectService")
public class ProjectService implements IProjectService {

	private static Logger logger = Logger.getLogger(ProjectService.class.getName());
	
	@Autowired
	private EventEmitter eventEmitter;

	@Autowired
	private IProjectDao projectDao;

	@Autowired
	private Config config;

	@Override
	public void createProject(Project createdProject, String creatorId) {
		createdProject.setId(UUID.randomUUID().toString().replace("-", ""));
		createdProject.getPdata().setCreationDate(new Date());
		createdProject.setStatus(STATUS.started);
		eventEmitter.emit(new ProjectCreated(createdProject, creatorId));
	}

	@Override
	public void updateProject(Project updated) {
		eventEmitter.emit(new ProjectUpdated(updated));
	}

	
	
	@Override
	public Project loadProject(String projectId, boolean generatePhotoLinks) {

		if (Strings.isNullOrEmpty(projectId)) {
			return null;
		}
		
		Project prj = projectDao.findOne(projectId);

		if (prj != null && generatePhotoLinks) {
			Date expirationdate = new Date();
			expirationdate.setTime(expirationdate.getTime() + config.getCvExpirationDelayInMillis());
			prj.generatePhotoLinks(expirationdate);
		}

		return prj;
	}


	@Override
	public void applyToProject(String userId, String applicationText, Project project) {
		if (Strings.isNullOrEmpty(userId) || project == null) {
			logger.log(Level.WARNING, "not letting a null user applying to a project or a user applying to a null project");
			return;
		}
		eventEmitter.emit(new ProjectApplicationEvent(userId, project.getId(), applicationText));
	}

	@Override
	public void cancelApplication(String userId, Project project) {
		if (Strings.isNullOrEmpty(userId) || project == null) {
			logger.log(Level.WARNING, "not letting a null user cancel a proejct application or a user cancelling for a null project");
			return;
		}
		eventEmitter.emit(new ProjectApplicationCancelled(userId, project.getId()));
	}

	

}
