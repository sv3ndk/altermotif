package com.svend.dab.core.dao;

import java.util.List;

import com.svend.dab.core.beans.projects.ProjectOverview;
import com.svend.dab.core.beans.projects.SearchQuery;


public interface IProjectIndexDao {

	public void updateIndex (String projectId, boolean immediate);
	
	public List<ProjectOverview> searchForProjects(SearchQuery request);

	
}
