package com.svend.dab.core.groups;

import java.util.List;

import com.svend.dab.core.beans.groups.GroupOverview;
import com.svend.dab.core.beans.projects.SearchQuery;

public interface IGroupFtsService {

	public abstract void updateGroupIndex(String groupId, boolean immediate);

	public abstract void ensureIndexOnLocation();

	public abstract List<GroupOverview> searchForGroups(SearchQuery backendRequest);

}