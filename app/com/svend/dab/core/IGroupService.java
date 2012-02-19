package com.svend.dab.core;

import com.svend.dab.core.beans.groups.ProjectGroup;

public interface IGroupService {

	void createNewGroup(ProjectGroup createdGroup, String loggedInUserProfileId);

	ProjectGroup loadGroupById(String groupid, boolean preparePresignedLinks);

	void updateGroupData(ProjectGroup editedGroup);

}
