package controllers.projects;

import models.altermotif.projects.EditedProject;
import models.altermotif.projects.ProjectEditVisibility;
import play.data.validation.Validation;
import web.utils.Utils;

import com.svend.dab.core.beans.projects.CreatedProjectPep;
import com.svend.dab.core.beans.projects.Project;

import controllers.BeanProvider;
import controllers.DabLoggedController;
import controllers.profile.ProfileHome;
import controllers.validators.DabValidators;

public class ProjectsNew extends DabLoggedController {

	public static void projectsNew() {
		Utils.addAllPossibleLanguageNamesToRenderArgs(getSessionWrapper(), renderArgs);
		Utils.addProjectThemesToRenderArgs(getSessionWrapper(), renderArgs);
		
		renderArgs.put("projectEditVisibility", new ProjectEditVisibility(new CreatedProjectPep(), getSessionWrapper().getLoggedInUserProfileId()));

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
			editedProject.applyToProject(createdProject, getSessionWrapper().getSelectedLg(), new CreatedProjectPep(), getSessionWrapper().getLoggedInUserProfileId());
			BeanProvider.getProjectService().createProject(createdProject, getSessionWrapper().getLoggedInUserProfileId());
			ProfileHome.profileHome();
		}

	}

}
