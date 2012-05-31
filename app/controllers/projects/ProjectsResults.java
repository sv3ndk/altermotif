package controllers.projects;

import models.altermotif.projects.WebSearchRequest;
import web.utils.Utils;
import controllers.BeanProvider;
import controllers.DabController;

/**
 * Project search results
 * 
 * @author svend
 *
 */
public class ProjectsResults extends DabController {

	public static void projectsResults(WebSearchRequest r) {
		
		if (r == null || r.isEmpty()) {
			// refusing to search based on empty criterias
			ProjectsSearch.projectsSearch();
		} else {

			Utils.addDefaultReferenceLocationToRenderArgs(renderArgs, getSessionWrapper().getLoggedInUserProfileId());
			Utils.addAllPossibleLanguageNamesToRenderArgs(getSessionWrapper(), renderArgs);

			renderArgs.put("originalSearchRequestJson", Utils.objectToJsonString(r));
			renderArgs.put("originalSearchRequest", r);
			renderArgs.put("projectsOverviews", BeanProvider.getProjectIndexDao().searchForProjects(r.toBackendRequest()));
			render();
		}
	}

}
