package com.svend.dab.core;

import com.svend.dab.core.beans.groups.GroupParticipant.ROLE;
import com.svend.dab.core.beans.groups.ProjectGroup;

public interface IGroupService {

	void createNewGroup(ProjectGroup createdGroup, String loggedInUserProfileId);

	ProjectGroup loadGroupById(String groupid, boolean preparePresignedLinks);

	void updateGroupData(ProjectGroup editedGroup);

	void closeGroup(String groupId);

	void applyToGroup(String groupId, String loggedInUserProfileId);

	void cancelUserApplicationToGroup(String groupId, String loggedInUserProfileId);

	void acceptUserApplicationToGroup(String groupId, String applicantId);

	void removeUserFromGroup(String groupId, String loggedInUserProfileId);

	void updateUserParticipantRole(String groupId, String userId, ROLE role);

}
