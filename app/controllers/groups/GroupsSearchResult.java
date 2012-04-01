package controllers.groups;

import web.utils.Utils;
import models.altermotif.projects.WebSearchRequest;
import controllers.BeanProvider;
import controllers.DabLoggedController;

public class GroupsSearchResult extends DabLoggedController{

	
	public static void groupsSearchResult(WebSearchRequest r) {
		
		if (r == null || r.isEmpty()) {
			// refusing to search based on empty criterias
			GroupsSearch.groupsSearch();
		} else {
			
			Utils.addDefaultReferenceLocationToRenderArgs(renderArgs, getSessionWrapper().getLoggedInUserProfileId());

			renderArgs.put("originalSearchRequestJson", Utils.objectToJsonString(r));
			renderArgs.put("originalSearchRequest", r);
			
			renderArgs.put("groupsOverviews", BeanProvider.getGroupFullTextSearchService().searchForGroups(r.toBackendRequest()));
			
			render();
		}
		
	}

	
}
