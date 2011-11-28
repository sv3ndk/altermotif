package com.svend.dab.dao.mongo;

import com.svend.dab.core.beans.profile.UserSummary;
import com.svend.dab.core.beans.projects.Project;
import com.svend.dab.core.beans.projects.ProjectData;

/**
 * @author Svend
 *
 */
public interface IProjectDao {
	void updateProjectParticipation(String projectId, UserSummary updatedSummary);

	Project findOne(String projectId);

	void save(Project project);

	void updateProjectPDataLinksAndTags(String id, Project project);
}
