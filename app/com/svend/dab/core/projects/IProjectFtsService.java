package com.svend.dab.core.projects;

import java.util.List;

import com.svend.dab.core.beans.projects.ProjectOverview;
import com.svend.dab.core.beans.projects.SearchQuery;

/**
 * 
 * Full text search service over projects
 * 
 * @author svend
 *
 */
public interface IProjectFtsService {
	
	public void updateProjetIndex(String projectId, boolean immediate);
	
	public List<ProjectOverview> searchForProjects(SearchQuery request);

	public void ensureIndexOnLocation();

}
