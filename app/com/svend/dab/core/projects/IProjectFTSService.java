package com.svend.dab.core.projects;

import java.util.List;

import com.svend.dab.core.beans.projects.ProjectOverview;
import com.svend.dab.core.beans.projects.ProjectSearchRequest;

/**
 * 
 * Full text search service over projects
 * 
 * @author svend
 *
 */
public interface IProjectFTSService {
	
	public void updateProjetIndex(String projectId);
	

	public List<ProjectOverview> searchForProjects(ProjectSearchRequest request);
	
	

}
