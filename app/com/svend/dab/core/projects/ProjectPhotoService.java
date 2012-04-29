package com.svend.dab.core.projects;

import static com.svend.dab.core.PhotoUtils.JPEG_MIME_TYPE;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.svend.dab.core.PhotoUtils;
import com.svend.dab.core.beans.DabException;
import com.svend.dab.core.beans.DabUploadFailedException;
import com.svend.dab.core.beans.DabUploadFailedException.failureReason;
import com.svend.dab.core.beans.profile.Photo;
import com.svend.dab.core.beans.projects.Project;
import com.svend.dab.core.dao.IPhotoBinaryDao;
import com.svend.dab.core.dao.IProjectDao;
import com.svend.dab.eda.EventEmitter;
import com.svend.dab.eda.events.projects.ProjectMainPhotoUpdated;
import com.svend.dab.eda.events.s3.BinaryNoLongerRequiredEvent;

/**
 * @author svend
 * 
 */

@Component
public class ProjectPhotoService implements IProjectPhotoService {

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

	public void addOnePhoto(String projectId, File photoContent) {

		if (photoContent == null) {
			throw new DabUploadFailedException("cannot process: upload request null ?! ", failureReason.technicalError);
		}

		Project updatedProject = projectService.loadProject(projectId, false);

		if (updatedProject == null) {
			throw new DabUploadFailedException("cannot process upload: no project found for id profile found for  " + projectId, failureReason.technicalError);
		}

		if (updatedProject.getPhotoAlbum().isFull()) {
			throw new DabUploadFailedException("cannot process upload: project " + projectId + " has already enough photos! THis should be prevented on browser side!", failureReason.technicalError);
		}

		final boolean wasPhotoAlbumEmptyBeforeAddingThis = updatedProject.getPhotoAlbum().isPhotoPackEmpty();

		try {
			BufferedImage photoImage = ImageIO.read(photoContent);

			// todo: optimization: we could probably launch two threads here to perform both operation in parallel...
			byte[] normalSize = photoUtils.resizePhotoToNormalSize(photoImage);
			byte[] thumbSize = photoUtils.resizePhotoToThumbSize(photoImage);

			Photo newPhoto = photoUtils.createOnePhotoPlaceholder(updatedProject.getPhotoAlbum().getPhotoS3RootFolder(), updatedProject.getPhotoAlbum().getThumbsS3RootFolder());
			photoDao.savePhoto(newPhoto, normalSize, thumbSize, JPEG_MIME_TYPE);

			projectDao.addOnePhoto(updatedProject.getId(), newPhoto);

			if (wasPhotoAlbumEmptyBeforeAddingThis) {
				emitter.emit(new ProjectMainPhotoUpdated(newPhoto, projectId));
			}
		} catch (IOException e) {
			throw new DabUploadFailedException("could not read uploaded photo", failureReason.technicalError, e);
		}

	}

	public void removeProjectPhoto(Project project, int deletedPhotoIdx) {
		if (project == null) {
			logger.log(Level.WARNING, "Cannot remove a photo from a null profile: not doing anything");
		} else {

			Photo removed = project.getPhotoAlbum().getPhoto(deletedPhotoIdx);
			if (removed == null) {
				logger.log(Level.WARNING, "It seems the profile refused to remove photo with index == " + deletedPhotoIdx + " => not propagating any event");
			}

			if (project.getPhotoAlbum().getMainPhotoIndex() < deletedPhotoIdx) {
				projectDao.removeOnePhoto(project.getId(), removed);
			} else if (project.getPhotoAlbum().getMainPhotoIndex() == deletedPhotoIdx) {
				projectDao.removeOnePhotoAndResetMainPhotoIndex(project.getId(), removed);
			} else {
				projectDao.removeOnePhotoAndDecrementMainPhotoIndex(project.getId(), removed);
			}

			if (deletedPhotoIdx == 0) {
				// the new main photo will now be the second one (potentially null, which means we should remove the main photo from all project summary)
				emitter.emit(new ProjectMainPhotoUpdated(project.getPhotoAlbum().getPhoto(1), project.getId()));
			}

			// actual removal of the file from s3 is done asynchronously, in order to improve GUI response time
			try {
				emitter.emit(new BinaryNoLongerRequiredEvent(removed.getNormalPhotoLink()));
			} catch (DabException e) {
				logger.log(Level.WARNING, "Could not emit event for removing one photo of project " + project.getId() + " => this might lead to dead space in s3 storage", e);
			}
		}

	}

	public void replacePhotoCaption(Project project, int photoIndex, String photoCaption) {
		if (project == null) {
			logger.log(Level.WARNING, "Cannot update photo caption of a null project: not doing anything");
		} else {
			Photo editedPhoto = project.getPhotoAlbum().getPhoto(photoIndex);

			if (editedPhoto == null) {
				logger.log(Level.WARNING, "Cannot update photo caption: no photo found with index " + photoIndex);
			} else {
				projectDao.updatePhotoCaption(project.getId(), editedPhoto.getNormalPhotoLink().getS3Key(), photoCaption);
			}
		}
	}

	public void putPhotoInFirstPositio(Project project, int photoIndex) {
		if (project == null) {
			logger.log(Level.WARNING, "Cannot move photo in first position for a null project: not doing anything");
		} else {
			projectDao.movePhotoToFirstPosition(project.getId(), photoIndex);
			emitter.emit(new ProjectMainPhotoUpdated(project.getPhotoAlbum().getPhoto(photoIndex), project.getId()));
		}
	}

}
