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
	
	
	/**
	 * @param projectId
	 */
	public static void doApplyToProject(String projectId, String applicationText) {
		if (!getSessionWrapper().isLoggedIn()) {
			logger.log(Level.WARNING, "non logged in user  trying to apply to project : " + projectId +" this should be impossible!");
			return;
		}
		
		Project project = BeanProvider.getProjectService().loadProject(projectId, false);
		if (project != null) {
			
			if (project.isUserAlreadyApplying(getSessionWrapper().getLoggedInUserProfileId()) || project.isUserAlreadyMember(getSessionWrapper().getLoggedInUserProfileId())) {
				// not letting the user apply several times
			} else {
				BeanProvider.getProjectService().applyToProject(getSessionWrapper().getLoggedInUserProfileId(), applicationText, project);
			}
			
		} else {
			logger.log(Level.WARNING, "user trying to apply to a non existant project : " + projectId +" this should be impossible!");
		}
	}
	
	
	/**
	 * @param projectId
	 */
	public static void doCancelApplyToProject(String projectId) {
		if (!getSessionWrapper().isLoggedIn()) {
			logger.log(Level.WARNING, "non logged in user trying to cancel project application to project : " + projectId +" this should be impossible!");
			return;
		}
		
		Project project = BeanProvider.getProjectService().loadProject(projectId, false);
		if (project != null) {
			
			if ( project.isUserAlreadyApplying(getSessionWrapper().getLoggedInUserProfileId()) ) {
				BeanProvider.getProjectService().cancelApplication(getSessionWrapper().getLoggedInUserProfileId(), project);
			}
			
		} else {
			logger.log(Level.WARNING, "user trying to cancel application to a non existant project : " + projectId +" this should be impossible!");
		}
		
	}
	
	public static void doRejectApplicationToProject(String projectId, String applicant) {
		
		Project project = BeanProvider.getProjectService().loadProject(projectId, false);
		if (project != null) {
			
			ProjectPep pep = new ProjectPep(project);
			
			if (pep.isAllowedToAcceptOrRejectApplications(getSessionWrapper().getLoggedInUserProfileId())) {
				BeanProvider.getProjectService().cancelApplication(applicant, project);
			} else {
				logger.log(Level.WARNING, "user trying to reject application but is not allowed to,  this should be impossible, userid is" + getSessionWrapper().getLoggedInUserProfileId() + ", projectid is " + projectId);
			}
			
			
		} else {
			logger.log(Level.WARNING, "user trying to reject application to a non existant project : " + projectId +" this should be impossible!");
		}
		
		
		
	}
	
}
