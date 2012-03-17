package com.svend.dab.core.dao;

import com.svend.dab.core.beans.groups.GroupParticipant.ROLE;
import com.svend.dab.core.beans.groups.ProjectGroup;
import com.svend.dab.core.beans.profile.Photo;
import com.svend.dab.core.beans.profile.UserSummary;
import com.svend.dab.core.beans.projects.ProjectSummary;

public interface IGroupDao {

	public void save(ProjectGroup group);

	public ProjectGroup retrieveGroupById(String groupId);

	public void updateParticipantOfAllGroupsWith(UserSummary updatedSummary);

	public void updateGroupData(ProjectGroup editedGroup);

	public void updateGroupActiveStatus(String groupId, boolean b);

	public void removeUserParticipant(String groupId, String participantId);

	public void addUserApplication(String groupId, UserSummary userId);

	public void setUserApplicationAcceptedStatus(String groupId, String userId, boolean b);

	public void updateParticipantRole(String groupId, String userId, ROLE role);

	public void addProjectApplication(String groupId, ProjectSummary project, String applicationText);

	public void removeProjectParticipant(String groupId, String projectId);

	public void setProjectApplicationAcceptedStatus(String groupId, String projectId, boolean b);

	public void updateProjectMainPhoto(String groupId, String projectId, Photo mainPhoto);

}
