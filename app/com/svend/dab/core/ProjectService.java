package com.svend.dab.core;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.svend.dab.core.beans.Config;
import com.svend.dab.core.beans.projects.Project;
import com.svend.dab.core.beans.projects.Project.STATUS;
import com.svend.dab.dao.mongo.IProjectDao;
import com.svend.dab.eda.EventEmitter;
import com.svend.dab.eda.events.projects.ProjectCreated;

@Component("projectService")
public class ProjectService implements IProjectService {

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
	public Project loadProject(String projectId, boolean generatePhotoLinks) {

		Project prj = projectDao.findOne(projectId);

		if (prj != null && generatePhotoLinks) {
			Date expirationdate = new Date();
			expirationdate.setTime(expirationdate.getTime() + config.getCvExpirationDelayInMillis());
			prj.generatePhotoLinks(expirationdate);
		}

		return prj;
	}

}
