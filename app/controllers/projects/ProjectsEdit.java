package controllers.projects;

import java.util.logging.Level;
import java.util.logging.Logger;

import models.altermotif.projects.EditedProject;
import models.altermotif.projects.ProjectEditVisibility;
import play.data.validation.Validation;
import web.utils.Utils;

import com.svend.dab.core.beans.projects.Project;
import com.svend.dab.core.beans.projects.ProjectPep;

import controllers.Application;
import controllers.BeanProvider;
import controllers.DabLoggedController;
import controllers.profile.ProfileHome;
import controllers.validators.DabValidators;

public class ProjectsEdit extends DabLoggedController{
	
	private static Logger logger = Logger.getLogger(ProjectsEdit.class.getName());
	
	
	public final static String FLASH_SKIP_LOADING_PROFILE ="skipLd";
	public final static String FLASH_PROJECT_ID ="pid";
	
	public static void projectsEdit(String p) {

		Project project = BeanProvider.getProjectService().loadProject(p, true);
		
		if (project != null) {
			
			ProjectPep pep = new ProjectPep(project);
			
			if (pep.isAllowedToEditAtLeastPartially(getSessionWrapper().getLoggedInUserProfileId())) {
				renderArgs.put("editedProjectName", project.getPdata().getName());
				renderArgs.put("editedProjectGoal", project.getPdata().getGoal());
				renderArgs.put("projectEditVisibility", new ProjectEditVisibility(new ProjectPep(project), getSessionWrapper().getLoggedInUserProfileId()));
				
				if (!flash.contains(FLASH_SKIP_LOADING_PROFILE)) {
					renderArgs.put("editedProject", new EditedProject(project, getSessionWrapper().getSelectedLg()));
				}
				
				Utils.addAllPossibleLanguageNamesToRenderArgs(getSessionWrapper(), renderArgs);
				Utils.addProjectThemesToRenderArgs(getSessionWrapper(), renderArgs);
				
				flash.put(FLASH_PROJECT_ID, p);
				render();
			} else {
				logger.log(Level.WARNING, "user is not allowed to edit this project => redirecting to home page");
				Application.index();
			}
			
		} else {
			logger.log(Level.WARNING, "could not find project with id ==" + p  + "> redirecting to application home");
			Application.index();
		}
	}
	
	
	public static void doEditProject(EditedProject editedProject) {
		
		DabValidators.validateEditedProject(editedProject, validation, flash);
		
		if (Validation.hasErrors()) {
			params.flash();
			
			// overwrites the flash data with the corrected versions
			flash.put("editedProject.allLinksJson", editedProject.getAllLinksJson());

			Validation.keep();
			flash.put(FLASH_SKIP_LOADING_PROFILE, true);
			projectsEdit(flash.get(FLASH_PROJECT_ID));
		} else {
			
			Project updated = BeanProvider.getProjectService().loadProject(flash.get(FLASH_PROJECT_ID), false);
			
			if (updated == null) {
				logger.log(Level.WARNING, "could not retrieve updated project in DB with id == " + flash.get(FLASH_PROJECT_ID) + " => not updating anything");
			} else {
				
				ProjectPep pep = new ProjectPep(updated);
				
				if (pep.isAllowedToEditAtLeastPartially(getSessionWrapper().getLoggedInUserProfileId())) {
					
					editedProject.applyToProject(updated, getSessionWrapper().getSelectedLg(), pep, getSessionWrapper().getLoggedInUserProfileId());
					BeanProvider.getProjectService().updateProjectCore(updated, editedProject.getParsedTasks(), editedProject.getParsedRemovedTasksIds(), editedProject.getParsedAssets(), editedProject.getParsedRemovedAssetsIds());
					
				} else {
					logger.log(Level.WARNING, "user is trying to update a project but is not allowed to! user is " + getSessionWrapper().getLoggedInUserProfileId() + ", project id is " + updated.getId());
				}

			}
			ProfileHome.profileHome();
		}
	}
	
}
