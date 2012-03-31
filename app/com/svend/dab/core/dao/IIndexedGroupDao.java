package com.svend.dab.core.dao;

import java.util.List;

import com.svend.dab.core.beans.groups.IndexedGroup;
import com.svend.dab.core.beans.projects.SearchQuery;

public interface IIndexedGroupDao {

	void updateIndex(IndexedGroup indexedGroup);

	void ensureIndexOnLocation();

	List<IndexedGroup> searchGroups(SearchQuery request);

}
