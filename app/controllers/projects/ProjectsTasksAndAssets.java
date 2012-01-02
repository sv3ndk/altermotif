package controllers.projects;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.svend.dab.core.beans.projects.Project;
import com.svend.dab.core.beans.projects.Project.STATUS;

import controllers.BeanProvider;
import controllers.DabLoggedController;

public class ProjectsTasksAndAssets extends DabLoggedController {

	private static Logger logger = Logger.getLogger(ProjectsTasksAndAssets.class.getName());
	
	public static void doGetTaskList(String projectId) {
	
		// TODO: optimization: we could only load the project status and list of tasks here
		Project project = BeanProvider.getProjectService().loadProject(projectId, true);
		if (project != null && project.getStatus() != STATUS.cancelled) {
			// have to keep the flash otherwise the project id is lost in the project edit controller
			flash.keep();
			renderJSON(project.getTasks());
		} else {
			logger.log(Level.WARNING, "could not find project => returning an empty json array instead of a task list");
			// TODO
		}
	}
	
	public static void doGetAssetsList(String projectId) {
		
		// TODO: optimization: we could only load the project status and list of assets here
		Project project = BeanProvider.getProjectService().loadProject(projectId, true);
		if (project != null && project.getStatus() != STATUS.cancelled) {
			flash.keep();
			renderJSON(project.getAssets());
		} else {
			logger.log(Level.WARNING, "could not find project => returning an empty json array instead of an asset list");
			// TODO
		}
		
	}

		
	
}