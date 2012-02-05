package com.svend.dab.core.dao;

import com.svend.dab.core.beans.groups.ProjectGroup;

public interface IGroupDao {

	public void save(ProjectGroup group);

	public ProjectGroup retrieveGroupById(String groupId);
}
