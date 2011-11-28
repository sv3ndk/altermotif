package controllers.projects;

import java.util.logging.Logger;

import models.altermotif.projects.EditedProject;
import play.data.validation.Validation;
import web.utils.Utils;

import com.svend.dab.core.beans.projects.Project;

import controllers.Application;
import controllers.BeanProvider;
import controllers.DabLoggedController;
import controllers.validators.DabValidators;

public class ProjectsNew extends DabLoggedController {

	private static Logger logger = Logger.getLogger(ProjectsNew.class.getName());

	public static void projectsNew() {

		Utils.addAllPossibleLanguageNamesToRenderArgs(getSessionWrapper(), renderArgs);
		render();
	}

	public static void doCreateProject(EditedProject editedProject) {

		DabValidators.validateCreatedProject(editedProject, validation, flash);

		if (Validation.hasErrors()) {
			params.flash();
			Validation.keep();
			projectsNew();
		} else {
			
			Project createdProject = new Project(); 
			editedProject.applyToProject(createdProject, getSessionWrapper().getSelectedLg());
			BeanProvider.getProjectService().createProject(createdProject, getSessionWrapper().getLoggedInUserProfileId());
			Application.index();
		}

	}

}
