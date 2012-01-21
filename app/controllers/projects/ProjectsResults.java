package controllers.projects;

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

			// default reference point when sorting results by proximity: by default Brussels, or if the user is logged in and has specified a location in his profile, we take that instead
			String defaultRefencePoint = "City of Brussels, Belgium";
			String defaultSortByProximityReferenceLatitude = "50.8503396";
			String defaultSortByProximityReferenceLongitude = "4.351710300000036";
			if (getSessionWrapper().isLoggedIn()) {
				UserProfile loggedInProfile = BeanProvider.getUserProfileService().loadUserProfile(getSessionWrapper().getLoggedInUserProfileId(), false);
				if (loggedInProfile != null && loggedInProfile.getPdata().getLocation() != null) {
					defaultRefencePoint = loggedInProfile.getPdata().getLocation();
					defaultSortByProximityReferenceLatitude = loggedInProfile.getPdata().getLocationLat();
					defaultSortByProximityReferenceLongitude = loggedInProfile.getPdata().getLocationLong();
				}
			}
			
			renderArgs.put("defaultSortByProximityReference", defaultRefencePoint);
			renderArgs.put("defaultSortByProximityReferenceLatitude", defaultSortByProximityReferenceLatitude);
			renderArgs.put("defaultSortByProximityReferenceLongitude", defaultSortByProximityReferenceLongitude);
			renderArgs.put("originalSearchRequestJson", Utils.objectToJsonString(r));
			renderArgs.put("projectsOverviews", BeanProvider.getProjectFullTextSearchService().searchForProjects(r.toBackendRequest()));
			render();
		}
		
	}

}
