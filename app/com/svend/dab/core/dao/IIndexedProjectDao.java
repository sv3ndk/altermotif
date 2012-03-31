package com.svend.dab.core.dao;

import java.util.List;

import com.svend.dab.core.beans.projects.IndexedProject;
import com.svend.dab.core.beans.projects.SearchQuery;

/**
 * @author svend
 *
 */
public interface IIndexedProjectDao {

	public void updateIndex(IndexedProject ip);

	public List<IndexedProject> searchForProjects(SearchQuery request);

	public void ensureIndexOnLocation();
	
	
}
