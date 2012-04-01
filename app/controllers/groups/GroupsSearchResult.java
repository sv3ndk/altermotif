package controllers.groups;

import models.altermotif.projects.WebSearchRequest;
import controllers.BeanProvider;
import controllers.DabLoggedController;

public class GroupsSearchResult extends DabLoggedController{

	
	public static void groupsSearchResult(WebSearchRequest r) {
		
		if (r == null || r.isEmpty()) {
			// refusing to search based on empty criterias
			GroupsSearch.groupsSearch();
		} else {
			
			renderArgs.put("groupsOverviews", BeanProvider.getGroupFullTextSearchService().searchForGroups(r.toBackendRequest()));
			
			render();
		}
		
	}

	
}
