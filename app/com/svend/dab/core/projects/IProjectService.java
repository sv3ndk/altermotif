package com.svend.dab.core.projects;

import com.svend.dab.core.beans.projects.Project;

/**
 * @author Svend
 *
 */
public interface IProjectService {
	
	
	public void createProject(Project createdProject, String creatorId);
	
	public Project loadProject(String projectId, boolean generatePhotoLinks);

	public void updateProject(Project updated);

	public void applyToProject(String loggedInUserProfileId, String applicationText, Project project);

	public void cancelApplication(String rejectedApplicantId, Project project);

	public void acceptApplication(String applicantId, Project project);


}
