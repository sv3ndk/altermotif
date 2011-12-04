package com.svend.dab.dao.mongo;

import com.svend.dab.core.beans.profile.Photo;
import com.svend.dab.core.beans.profile.UserSummary;
import com.svend.dab.core.beans.projects.Project;

/**
 * @author Svend
 *
 */
public interface IProjectDao {
	void updateProjectParticipation(String projectId, UserSummary updatedSummary);

	Project findOne(String projectId);

	void save(Project project);

	void updateProjectPDataLinksAndTags(String id, Project project);

	void addOnePhoto(String id, Photo newPhoto);

	void removeOnePhoto(String id, Photo removed);

	void removeOnePhotoAndResetMainPhotoIndex(String id, Photo removed);
	
	void removeOnePhotoAndDecrementMainPhotoIndex(String id, Photo removed);
	
	void updatePhotoCaption(String id, String s3PhotoKey, String photoCaption);

	void movePhotoToFirstPosition(String id, int mainPhotoIndex);

}
