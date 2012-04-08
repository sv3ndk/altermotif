package com.svend.dab.core.dao;

import java.util.List;
import java.util.Set;

import com.svend.dab.core.beans.groups.GroupParticipant.ROLE;
import com.svend.dab.core.beans.groups.ProjectGroup;
import com.svend.dab.core.beans.profile.Photo;
import com.svend.dab.core.beans.profile.UserSummary;
import com.svend.dab.core.beans.projects.Project.STATUS;
import com.svend.dab.core.beans.projects.ProjectSummary;
import com.svend.dab.core.beans.projects.SearchQuery.SORT_KEY;

public interface IGroupDao {

	public Set<String> getAllGroupsIds();

	public void save(ProjectGroup group);

	public ProjectGroup retrieveGroupById(String groupId);
	
	public List<ProjectGroup> loadAllGroups(Set<String> allIds, SORT_KEY sortKey);

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

	public void updateProjectStatus(String groupId, String projectId, STATUS newStatus);
	/////////////////////////
	// tag count
	
	public void launchCountGroupTagsJob();

	//////////////////////////
	// photos
	
	public void addOnePhoto(String id, Photo newPhoto);

	public void removeOnePhoto(String id, Photo removed);

	public void removeOnePhotoAndResetMainPhotoIndex(String id, Photo removed);

	public void removeOnePhotoAndDecrementMainPhotoIndex(String id, Photo removed);

	public void updatePhotoCaption(String id, String s3Key, String photoCaption);

	public void movePhotoToFirstPosition(String id, int photoIndex);




}
