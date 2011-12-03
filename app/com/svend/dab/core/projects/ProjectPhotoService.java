package com.svend.dab.core.projects;

import static com.svend.dab.core.PhotoUtils.JPEG_MIME_TYPE;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.svend.dab.core.PhotoUtils;
import com.svend.dab.core.beans.DabException;
import com.svend.dab.core.beans.DabUploadFailedException;
import com.svend.dab.core.beans.DabUploadFailedException.failureReason;
import com.svend.dab.core.beans.profile.Photo;
import com.svend.dab.core.beans.profile.UserProfile;
import com.svend.dab.core.beans.profile.UserSummary;
import com.svend.dab.core.beans.projects.Project;
import com.svend.dab.core.beans.projects.ProjectSummary;
import com.svend.dab.core.dao.IPhotoBinaryDao;
import com.svend.dab.dao.mongo.IProjectDao;
import com.svend.dab.eda.EventEmitter;
import com.svend.dab.eda.events.profile.UserSummaryUpdated;
import com.svend.dab.eda.events.projects.ProjectMainPhotoUpdated;
import com.svend.dab.eda.events.s3.BinaryNoLongerRequiredEvent;

/**
 * @author svend
 *
 */

@Component
public class ProjectPhotoService implements IProjectPhotoService{

	@Autowired
	private IPhotoBinaryDao photoDao;
	
	@Autowired
	IProjectService projectService;

	@Autowired
	IProjectDao projectDao;
	
	@Autowired
	private PhotoUtils photoUtils;
	
	@Autowired
	private EventEmitter emitter;

	private static Logger logger = Logger.getLogger(ProjectPhotoService.class.getName());
	
	// -----------------------------------------------
	// -----------------------------------------------
	
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
		
		// todo: optimization: we could probably launch two threads here to perform both operation in parallel...
		byte[] normalSize = photoUtils.resizePhotoToNormalSize(receivedPhoto);
		byte[] thumbSize = photoUtils.resizePhotoToThumbSize(receivedPhoto);
		
		Photo newPhoto = photoUtils.createOnePhotoPlaceholder(updatedProject.getPhotoS3RootFolder(), updatedProject.getThumbsS3RootFolder());
		photoDao.savePhoto(newPhoto, normalSize, thumbSize, JPEG_MIME_TYPE);
		
		boolean hasMainPhotoChanged = updatedProject.isPhotoPackEmpty();
		projectDao.addOnePhoto(updatedProject.getId(), newPhoto);

		if (hasMainPhotoChanged) {
			emitter.emit(new ProjectMainPhotoUpdated(newPhoto, projectId));
		}
		
	}


	@Override
	public void removeProjectPhoto(Project project, int deletedPhotoIdx) {
		if (project == null) {
			logger.log(Level.WARNING, "Cannot remove a photo from a null profile: not doing anything");
		} else {

			Photo removed = project.getPhoto(deletedPhotoIdx);
			if (removed == null) {
				logger.log(Level.WARNING, "It seems the profile refused to remove photo with index == " + deletedPhotoIdx + " => not propagating any event");
			}

			projectDao.removeOnePhoto(project.getId(), removed);

			if (deletedPhotoIdx == 0) {
				// the new main photo will now be the second one (potentially null, which means we should remove the main photo from all project summary
				emitter.emit(new ProjectMainPhotoUpdated(project.getPhoto(1), project.getId()));
			}

			// actual removal of the file from s3 is done asynchronously, in order to improve gui response time
			try {
				emitter.emit(new BinaryNoLongerRequiredEvent(removed.getNormalPhotoLink()));
			} catch (DabException e) {
				logger.log(Level.WARNING, "Could not emit event for removing one photo of project " + project.getId() + " => this might lead to dead space in s3 storage", e);
			}
		}
		
	}


}
