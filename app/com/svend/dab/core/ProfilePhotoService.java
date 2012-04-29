package com.svend.dab.core;

import static com.svend.dab.core.PhotoUtils.JPEG_MIME_TYPE;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.svend.dab.core.beans.DabException;
import com.svend.dab.core.beans.DabUploadFailedException;
import com.svend.dab.core.beans.DabUploadFailedException.failureReason;
import com.svend.dab.core.beans.profile.Photo;
import com.svend.dab.core.beans.profile.UserProfile;
import com.svend.dab.core.beans.profile.UserSummary;
import com.svend.dab.core.dao.IPhotoBinaryDao;
import com.svend.dab.core.dao.IUserProfileDao;
import com.svend.dab.eda.EventEmitter;
import com.svend.dab.eda.events.profile.UserSummaryUpdated;
import com.svend.dab.eda.events.s3.BinaryNoLongerRequiredEvent;

/**
 * @author Svend
 * 
 */
@Service
public class ProfilePhotoService implements IProfilePhotoService {

	@Autowired
	private IPhotoBinaryDao photoDao;

	@Autowired
	private EventEmitter emitter;

	@Autowired
	private IUserProfileDao userProfileRepo;

	@Autowired
	private IUserProfileService userProfileService;

	@Autowired
	private PhotoUtils photoUtils;

	private static Logger logger = Logger.getLogger(ProfilePhotoService.class.getName());

	// -------------------------------------
	// -------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.svend.dab.core.IProfilePhotoService#addOnePhoto(com.svend.dab.core.beans.profile.UserProfile, byte[])
	 */
	public void addOnePhoto(String username, File photoContent) {

		if (photoContent == null) {
			throw new DabUploadFailedException("cannot process: upload request null ?! ", failureReason.technicalError);
		}

		UserProfile profile = userProfileService.loadUserProfile(username, false);

		if (profile == null) {
			throw new DabUploadFailedException("cannot process: no user profile found for  " + username, failureReason.technicalError);
		}

		if (profile.getPhotoAlbum().isFull()) {
			throw new DabUploadFailedException("cannot process: no user profile found for  " + username + ": already 20 photos! (the front end shoud prevent this!)", failureReason.technicalError);
		}

		try {
			BufferedImage photoImage = ImageIO.read(photoContent);
			
			// todo: optimization: we could probably launch two threads here to perform both operation in parallel...
			byte[] normalSize = photoUtils.resizePhotoToNormalSize(photoImage);
			byte[] thumbSize = photoUtils.resizePhotoToThumbSize(photoImage);
			Photo photo = photoUtils.createOnePhotoPlaceholder(profile.getPhotoAlbum().getPhotoS3RootFolder(), profile.getPhotoAlbum().getThumbsS3RootFolder());
	
			// saves first the photo in S3 => in case of failure, we just have some lost space over there (+ a message on the user screen)
			photoDao.savePhoto(photo, normalSize, thumbSize, JPEG_MIME_TYPE);
	
			boolean hasMainPhotoChanged = profile.getPhotoAlbum().isPhotoPackEmpty();
			userProfileRepo.addOnePhoto(profile.getUsername(), photo);
	
			if (hasMainPhotoChanged) {
				UserProfile updatedProfile = userProfileRepo.retrieveUserProfileById(profile.getUsername());
				emitter.emit(new UserSummaryUpdated(new UserSummary(updatedProfile)));
			}
			
		} catch (IOException e) {
			throw new DabUploadFailedException("could not read uploaded photo", failureReason.technicalError, e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.svend.dab.core.IProfilePhotoService#removeProfilePhoto(com.svend.dab.core.beans.profile.UserProfile, int)
	 */
	public void removeProfilePhoto(UserProfile profile, int deletedPhotoIdx) {

		if (profile == null) {
			logger.log(Level.WARNING, "Cannot remove a photo from a null profile: not doing anything");
		} else {

			Photo removed = profile.getPhotoAlbum().getPhoto(deletedPhotoIdx);
			if (removed == null) {
				logger.log(Level.WARNING, "It seems the profile refused to remove photo with index == " + deletedPhotoIdx + " => not propagating any event");
			} else {
				
				if (profile.getPhotoAlbum().getMainPhotoIndex() < deletedPhotoIdx) {
					userProfileRepo.removeOnePhoto(profile.getUsername(), removed);
				} else if (profile.getPhotoAlbum().getMainPhotoIndex() == deletedPhotoIdx) {
					userProfileRepo.removeOnePhotoAndResetMainPhotoIndex(profile.getUsername(), removed);
					profile.getPhotoAlbum().setMainPhotoIndex(0);
					emitter.emit(new UserSummaryUpdated(new UserSummary(profile)));
				} else {
					userProfileRepo.removeOnePhotoAndDecrementMainPhotoIndex(profile.getUsername(), removed);
				}
	
				// actual removal of the file from s3 is done asynchronously, in order to improve gui response time
				try {
					emitter.emit(new BinaryNoLongerRequiredEvent(removed.getNormalPhotoLink()));
				} catch (DabException e) {
					logger.log(Level.WARNING, "Could not emit event for removing one photo of profile " + profile.getUsername() + " => this might lead to dead space in s3 storage", e);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.svend.dab.core.IProfilePhotoService#movePhotoToFirstPosition(com.svend.dab.core.beans.profile.UserProfile, int)
	 */
	public void movePhotoToFirstPosition(UserProfile userProfile, int photoIndex) {
		if (userProfile != null && userProfile.getPhotoAlbum().getPhotos() != null && photoIndex >= 0 && photoIndex < userProfile.getPhotoAlbum().getPhotos().size()) {
			userProfileRepo.movePhotoToFirstPosition(userProfile.getUsername(), photoIndex);
			userProfile.getPhotoAlbum().setMainPhotoIndex(photoIndex);
			emitter.emit(new UserSummaryUpdated(new UserSummary(userProfile)));

		} else {
			logger.log(Level.WARNING, "Not setting photo as profile photo: invalid index or user profile null or user profile with null photo set. Index=" + photoIndex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.svend.dab.core.IProfilePhotoService#updatePhotoCaption(com.svend.dab.core.beans.profile.UserProfile, int, java.lang.String)
	 */
	public void updatePhotoCaption(UserProfile userProfile, int photoIndex, String photoCaption) {

		if (userProfile != null ) {

			Photo editedPhoto = userProfile.getPhotoAlbum().getPhoto(photoIndex);
			userProfileRepo.updatePhotoCaption(userProfile.getUsername(), editedPhoto.getNormalPhotoLink().getS3Key(), photoCaption);

		} else {
			logger.log(Level.WARNING, "Not updating photo caption: invalid index or user profile null or user profile with null photo set. Index=" + photoIndex);
		}
	}

}