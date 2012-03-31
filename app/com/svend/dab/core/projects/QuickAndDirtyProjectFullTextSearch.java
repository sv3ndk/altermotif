package com.svend.dab.core.projects;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import web.utils.Utils;

import com.google.common.base.Strings;
import com.svend.dab.core.beans.Config;
import com.svend.dab.core.beans.projects.IndexedProject;
import com.svend.dab.core.beans.projects.Project;
import com.svend.dab.core.beans.projects.ProjectOverview;
import com.svend.dab.core.beans.projects.ProjectSearchQuery;
import com.svend.dab.core.dao.IIndexedProjectDao;
import com.svend.dab.core.dao.IProjectDao;

/**
 * Quick and dirty (and I'm drunk...) version of a full text search index for projects.
 * 
 * TODO: to be replaced by a Solr index some time...
 * 
 * @author svend
 * 
 */
@Service
public class QuickAndDirtyProjectFullTextSearch implements IProjectFtsService {

	@Autowired
	private IProjectDao projectDao;

	@Autowired
	private IIndexedProjectDao indexedProjectDao;

	@Autowired
	private Config config;

	private static Logger logger = Logger.getLogger(QuickAndDirtyProjectFullTextSearch.class.getName());

	public void updateProjetIndex(String projectId, boolean immediate) {

		if (!immediate) {
			// increasing the chances to actually catch the updated data in db (you know, eventual consistency thingy...)
			// ...I said this class was quick and dirty....
			Utils.waitABit();
		}

		Project project = projectDao.findOne(projectId);

		if (project == null) {
			logger.log(Level.WARNING, "cannot update full text search of non existant project : " + projectId);
		} else {

			IndexedProject ip = new IndexedProject(project);

			// indexing full search search based on content in the name, description and goal
			String textToIndex = new StringBuffer(project.getPdata().getName()).append(" ").append(project.getPdata().getDescription()).append(" ")
					.append(project.getPdata().getGoal()).toString();

			if (!Strings.isNullOrEmpty(textToIndex)) {
				StringTokenizer st = new StringTokenizer(textToIndex);
				while (st.hasMoreTokens()) {
					ip.addFtsTerm(st.nextToken());
				}
			}

			indexedProjectDao.updateIndex(ip);
		}
	}

	public List<ProjectOverview> searchForProjects(ProjectSearchQuery request) {

		List<IndexedProject> ips = indexedProjectDao.searchForProjects(request);

		Set<String> allIds = new HashSet<String>();
		for (IndexedProject ip : ips) {
			allIds.add(ip.getProjectId());
		}

		List<Project> projects = projectDao.loadAllProjects(allIds, request.getSortKey());

		List<ProjectOverview> projectOverview = new LinkedList<ProjectOverview>();

		if (projects != null) {
			for (Project project : projects) {
				projectOverview.add(new ProjectOverview(project));
			}
		}

		if (projectOverview != null) {
			Date expirationdate = new Date();
			expirationdate.setTime(expirationdate.getTime() + config.getCvExpirationDelayInMillis());
			for (ProjectOverview overview : projectOverview) {
				overview.generatePhotoLinks(expirationdate);
			}
		}

		return projectOverview;

	}

	public void ensureIndexOnLocation() {
		indexedProjectDao.ensureIndexOnLocation();
	}

}
