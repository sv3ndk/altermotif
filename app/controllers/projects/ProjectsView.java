package controllers.projects;

import java.util.logging.Level;
import java.util.logging.Logger;

import models.altermotif.projects.ProjectVisibility;

import web.utils.Utils;

import com.svend.dab.core.beans.projects.Project;
import com.svend.dab.core.beans.projects.ProjectPep;

import controllers.Application;
import controllers.BeanProvider;
import controllers.DabController;

public class ProjectsView extends DabController{

	private static Logger logger = Logger.getLogger(ProjectsView.class.getName());
	
	public static void projectsView(String p) {
			
		Project project = BeanProvider.getProjectService().loadProject(p, true);
		if (project != null) {
			renderArgs.put("visitedProject", project);
			renderArgs.put("projectVisibility", new ProjectVisibility(new ProjectPep(project), project, getSessionWrapper().getLoggedInUserProfileId()));
			
			Utils.addAllPossibleLanguageNamesToRenderArgs(getSessionWrapper(), renderArgs);
			render();
		} else {
			logger.log(Level.WARNING, "could not find project => redirecting to application home");
			Application.index();
		}
			
		
	}
	
}
