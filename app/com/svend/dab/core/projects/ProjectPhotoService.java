package com.svend.dab.core.projects;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.svend.dab.core.PhotoUtils;
import com.svend.dab.core.beans.DabUploadFailedException;
import com.svend.dab.core.beans.DabUploadFailedException.failureReason;
import com.svend.dab.core.beans.projects.Project;

/**
 * @author svend
 *
 */

@Component
public class ProjectPhotoService implements IProjectPhotoService{

	@Autowired
	IProjectService projectService;
	
	@Autowired
	private PhotoUtils photoUtils;
	
	@Override
	public void addOnePhoto(String projectId, File photoContent) {
		
		if (photoContent == null) {
			throw new DabUploadFailedException("cannot process: upload request null ?! ", failureReason.technicalError);
		}

		Project updatedProject = projectService.loadProject(projectId, false);
		
		if (updatedProject == null) {
			throw new DabUploadFailedException("cannot process upload: no project found for id profile found for  " + projectId, failureReason.technicalError);
		}
		
		if (updatedProject.isPhotoPackFull()) {
			throw new DabUploadFailedException("cannot process upload: project " + projectId + " has already enough photos! THis should be prevented on browser side!", failureReason.technicalError);
		}
		
		
		byte[] receivedPhoto = photoUtils.readPhotoContent(photoContent);
		
		
	}

}
