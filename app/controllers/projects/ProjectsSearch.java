package controllers.projects;

import web.utils.Utils;
import controllers.BeanProvider;
import controllers.DabController;

public class ProjectsSearch extends DabController {
	
	public static void projectsSearch() {
		renderArgs.put("popularTags", BeanProvider.getProjectService().getPopularTags());
		Utils.addThemesToRenderArgs(getSessionWrapper(), renderArgs);
		Utils.addJsonThemesToRenderArgs(getSessionWrapper(), renderArgs, getSessionWrapper().getSelectedLg());
		render();
	}
	
}
