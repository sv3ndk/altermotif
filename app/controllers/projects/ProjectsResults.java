package controllers.projects;

import web.utils.Utils;
import models.altermotif.projects.WebSearchRequest;
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
			renderArgs.put("originalSearchRequestJson", Utils.objectToJsonString(r));
			renderArgs.put("projectsOverviews", BeanProvider.getProjectFullTextSearchService().searchForProjects(r.toBackendRequest()));
			render();
		}
		
	}

}
