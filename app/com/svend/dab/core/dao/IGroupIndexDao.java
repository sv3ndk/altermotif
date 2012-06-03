package com.svend.dab.core.dao;

import java.util.List;

import com.svend.dab.core.beans.groups.GroupOverview;
import com.svend.dab.core.beans.projects.SearchQuery;

public interface IGroupIndexDao {

	public void updateIndex (String groupId, boolean immediate);
	
	public List<GroupOverview> searchForGroups(SearchQuery request);

}
