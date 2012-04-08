package com.svend.dab.core.groups;

import static com.svend.dab.core.PhotoUtils.JPEG_MIME_TYPE;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.svend.dab.core.PhotoUtils;
import com.svend.dab.core.beans.DabException;
import com.svend.dab.core.beans.DabUploadFailedException;
import com.svend.dab.core.beans.DabUploadFailedException.failureReason;
import com.svend.dab.core.beans.groups.GroupSummary;
import com.svend.dab.core.beans.groups.ProjectGroup;
import com.svend.dab.core.beans.profile.Photo;
import com.svend.dab.core.dao.IGroupDao;
import com.svend.dab.core.dao.IPhotoBinaryDao;
import com.svend.dab.eda.EventEmitter;
import com.svend.dab.eda.events.groups.GroupSummaryUpdated;
import com.svend.dab.eda.events.projects.ProjectMainPhotoUpdated;
import com.svend.dab.eda.events.s3.BinaryNoLongerRequiredEvent;

@Service
public class GroupPhotoService implements IGroupPhotoService {

	@Autowired
	private IGroupService groupService;
	
	@Autowired
	private PhotoUtils photoUtils;
	
	@Autowired
	private IGroupDao groupDao;
	
	@Autowired
	private IPhotoBinaryDao photoDao;
	
	@Autowired
	private EventEmitter emitter;
	
	private static Logger logger = Logger.getLogger(GroupPhotoService.class.getName());
	
	public void addOnePhoto(String groupId, File photoContent) {
		
		if (photoContent == null) {
			throw new DabUploadFailedException("cannot process: upload request null ?! ", failureReason.technicalError);
		}

		ProjectGroup updatedGroup = groupService.loadGroupById(groupId, false);
		
		if (updatedGroup == null) {
			throw new DabUploadFailedException("cannot process upload: no group found for id profile found for  " + groupId, failureReason.technicalError);
		}
		
		if (updatedGroup.getPhotoAlbum().isFull()) {
			throw new DabUploadFailedException("cannot process upload: group " + groupId + " has already enough photos! THis should be prevented on browser side!", failureReason.technicalError);
		}
		
		final boolean wasPhotoAlbumEmptyBeforeAddingThis = updatedGroup.getPhotoAlbum().isPhotoPackEmpty(); 
		
		byte[] receivedPhoto = photoUtils.readPhotoContent(photoContent);
		
		// todo: optimization: we could probably launch two threads here to perform both operation in parallel...
		byte[] normalSize = photoUtils.resizePhotoToNormalSize(receivedPhoto);
		byte[] thumbSize = photoUtils.resizePhotoToThumbSize(receivedPhoto);
		
		Photo newPhoto = photoUtils.createOnePhotoPlaceholder(updatedGroup.getPhotoAlbum().getPhotoS3RootFolder(), updatedGroup.getPhotoAlbum().getThumbsS3RootFolder());
		photoDao.savePhoto(newPhoto, normalSize, thumbSize, JPEG_MIME_TYPE);
		
		groupDao.addOnePhoto(updatedGroup.getId(), newPhoto);

		if (wasPhotoAlbumEmptyBeforeAddingThis) {
			GroupSummaryUpdated event = new GroupSummaryUpdated(new GroupSummary(updatedGroup));
			event.getUpdatedSummary().setMainPhoto(newPhoto);
			emitter.emit(event);
		}
	}

	public void removePhoto(ProjectGroup group, int deletedPhotoIdx) {
		if (group == null) {
			logger.log(Level.WARNING, "Cannot remove a photo from a null group: not doing anything");
		} else {

			Photo removed = group.getPhotoAlbum().getPhoto(deletedPhotoIdx);
			if (removed == null) {
				logger.log(Level.WARNING, "It seems the profile refused to remove photo with index == " + deletedPhotoIdx + " => not propagating any event");
			}
			
			if (group.getPhotoAlbum().getMainPhotoIndex() < deletedPhotoIdx) {
				groupDao.removeOnePhoto(group.getId(), removed);
			} else if (group.getPhotoAlbum().getMainPhotoIndex() == deletedPhotoIdx) {
				groupDao.removeOnePhotoAndResetMainPhotoIndex(group.getId(), removed);
			} else {
				groupDao.removeOnePhotoAndDecrementMainPhotoIndex(group.getId(), removed);
			}

			if (deletedPhotoIdx == 0) {
				// the new main photo will now be the second one (potentially null, which means we should remove the main photo from all project summary)
				GroupSummaryUpdated event = new GroupSummaryUpdated(new GroupSummary(group));
				event.getUpdatedSummary().setMainPhoto(group.getPhotoAlbum().getPhoto(1));
				emitter.emit(event);
			}

			// actual removal of the file from s3 is done asynchronously, in order to improve GUI response time
			try {
				emitter.emit(new BinaryNoLongerRequiredEvent(removed.getNormalPhotoLink()));
			} catch (DabException e) {
				logger.log(Level.WARNING, "Could not emit event for removing one photo of group " + group.getId() + " => this might lead to dead space in s3 storage", e);
			}
		}

	}

	public void replacePhotoCaption(ProjectGroup group, int photoIndex, String photoCaption) {
		if (group == null) {
			logger.log(Level.WARNING, "Cannot update photo caption of a null group: not doing anything");
		} else {
			Photo editedPhoto = group.getPhotoAlbum().getPhoto(photoIndex);
			
			if (editedPhoto == null) {
				logger.log(Level.WARNING, "Cannot update photo caption: no photo found with index " + photoIndex);
			} else {
				groupDao.updatePhotoCaption(group.getId(), editedPhoto.getNormalPhotoLink().getS3Key(), photoCaption);
			}
			
		}

	}

	public void putPhotoInFirstPositio(ProjectGroup group, int photoIndex) {
		if (group == null) {
			logger.log(Level.WARNING, "Cannot move photo in first position for a null group: not doing anything");
		} else {
			groupDao.movePhotoToFirstPosition(group.getId(), photoIndex);
			GroupSummaryUpdated event = new GroupSummaryUpdated(new GroupSummary(group));
			event.getUpdatedSummary().setMainPhoto(group.getPhotoAlbum().getPhoto(photoIndex));
			emitter.emit(event);
		}

	}

}
