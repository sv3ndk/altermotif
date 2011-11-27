package controllers.projects;

import web.utils.Utils;

import com.google.common.base.Strings;

import controllers.BeanProvider;
import controllers.DabController;

public class ProjectsView extends DabController{

	
	public static void projectsView(String vp) {
		if (!Strings.isNullOrEmpty(vp)) {
			renderArgs.put("visitedProject", BeanProvider.getProjectService().loadProject(vp, true));
		}
		
		Utils.addAllPossibleLanguageNamesToRenderArgs(getSessionWrapper(), renderArgs);
		
		render();
	}
	
}
