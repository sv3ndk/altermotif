package controllers.projects;

import com.svend.dab.core.beans.Config;
import com.svend.dab.core.beans.profile.UserProfile;

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

			boolean useSearchReferenceLocationFromConfig = true;
			
			if (getSessionWrapper().isLoggedIn()) {
				UserProfile loggedInProfile = BeanProvider.getUserProfileService().loadUserProfile(getSessionWrapper().getLoggedInUserProfileId(), false);
				if (loggedInProfile != null && loggedInProfile.getPdata().getLocation() != null) {
					renderArgs.put("defaultRefenceLocation", loggedInProfile.getPdata().getLocation());
					renderArgs.put("defaultReferenceLatitude", loggedInProfile.getPdata().getLocationLat());
					renderArgs.put("defaultReferenceLongitude", loggedInProfile.getPdata().getLocationLong());
					useSearchReferenceLocationFromConfig = false;
				}
			}
			if (useSearchReferenceLocationFromConfig) {
				Config config = BeanProvider.getConfig();
				renderArgs.put("defaultRefenceLocation", config.getDefaultSearchReferenceLocation().getLocation());
				renderArgs.put("defaultReferenceLatitude", config.getDefaultSearchReferenceLocation().getLatitude());
				renderArgs.put("defaultReferenceLongitude", config.getDefaultSearchReferenceLocation().getLongitude());
			}
			
			Utils.addAllPossibleLanguageNamesToRenderArgs(getSessionWrapper(), renderArgs);

			renderArgs.put("originalSearchRequestJson", Utils.objectToJsonString(r));
			renderArgs.put("originalSearchRequest", r);
			renderArgs.put("projectsOverviews", BeanProvider.getProjectFullTextSearchService().searchForProjects(r.toBackendRequest()));
			render();
		}
	}
}
