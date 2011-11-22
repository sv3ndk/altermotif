package com.svend.dab.core;

import com.svend.dab.core.beans.projects.Project;

/**
 * @author Svend
 *
 */
public interface IProjectService {
	
	
	public void createProject(Project createdProject, String creatorId);
	
	public Project loadProject(String projectId, boolean generatePhotoLinks);
	
	

}
