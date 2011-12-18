package com.svend.dab.dao.mongo;

import java.util.List;

import com.svend.dab.core.beans.profile.Photo;
import com.svend.dab.core.beans.profile.UserSummary;
import com.svend.dab.core.beans.projects.Participant;
import com.svend.dab.core.beans.projects.Participant.ROLE;
import com.svend.dab.core.beans.projects.Project;
import com.svend.dab.core.beans.projects.Project.STATUS;

/**
 * @author Svend
 *
 */
public interface IProjectDao {
	void updateProjectParticipation(String projectId, UserSummary updatedSummary);

	Project findOne(String projectId);

	void save(Project project);

	// this is getting less and less atomic...
	// TODO: clean this up...
	void updateProjectPDataAndLinksAndTagsAndThemes(String id, Project project);
	
	void updateProjectStatus(String id, STATUS newStatus);
	
	// project photos

	void addOnePhoto(String id, Photo newPhoto);

	void removeOnePhoto(String id, Photo removed);

	void removeOnePhotoAndResetMainPhotoIndex(String id, Photo removed);
	
	void removeOnePhotoAndDecrementMainPhotoIndex(String id, Photo removed);
	
	void updatePhotoCaption(String id, String s3PhotoKey, String photoCaption);

	void movePhotoToFirstPosition(String id, int mainPhotoIndex);
	
	///////////////////////////
	// project participants

	void addOneParticipant(String projectId, Participant createdParticipant);

	void updateParticipantList(String projectId, List<Participant> newPList);

	void markParticipantAsAccepted(String projectId, String participantId);

	void removeParticipant(String projectId, String userId);

	void updateParticipantRole(String projectId, String userId, ROLE role);
	
	/**
	 * @param projectId
	 * @return a project with only the participants (if any)
	 */
	public Project loadProjectParticipants(String projectId);

	void updateOwnerShipProposed(String projectId, String userName, boolean b);




}
