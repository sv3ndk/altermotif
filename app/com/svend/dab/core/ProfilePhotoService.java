package com.svend.dab.core;

import static com.svend.dab.core.PhotoUtils.JPEG_MIME_TYPE;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.svend.dab.core.beans.DabException;
import com.svend.dab.core.beans.DabUploadFailedException;
import com.svend.dab.core.beans.DabUploadFailedException.failureReason;
import com.svend.dab.core.beans.profile.Photo;
import com.svend.dab.core.beans.profile.UserProfile;
import com.svend.dab.core.beans.profile.UserSummary;
import com.svend.dab.core.dao.IPhotoBinaryDao;
import com.svend.dab.dao.mongo.IUserProfileDao;
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
	@Override
	public void addOnePhoto(String username, File photoContent) {

		if (photoContent == null) {
			throw new DabUploadFailedException("cannot process: upload request null ?! ", failureReason.technicalError);
		}

		UserProfile profile = userProfileService.loadUserProfile(username, false);

		if (profile == null) {
			throw new DabUploadFailedException("cannot process: no user profile found for  " + username, failureReason.technicalError);
		}

		if (profile.isPhotoPackFullAlready()) {
			throw new DabUploadFailedException("cannot process: no user profile found for  " + username + ": already 20 photos! (the front end shoud prevent this!)", failureReason.technicalError);
		}

		byte[] receivedPhoto = photoUtils.readPhotoContent(photoContent);

		// todo: optimization: we could probably launch two threads here to perform both operation in parallel...
		byte[] normalSize = photoUtils.resizePhotoToNormalSize(receivedPhoto);
		byte[] thumbSize = photoUtils.resizePhotoToThumbSize(receivedPhoto);

		Photo photo = photoUtils.createOnePhotoPlaceholder(profile.getPhotoS3RootFolder(), profile.getThumbsS3RootFolder());

		// saves first the photo in S3 => in case of failure, we just have some lost space over there (+ a message on the user screen)
		photoDao.savePhoto(photo, normalSize, thumbSize, JPEG_MIME_TYPE);

		boolean hasMainPhotoChanged = profile.isPhotoPackEmpty();
		userProfileRepo.addOnePhoto(profile.getUsername(), photo);

		if (hasMainPhotoChanged) {
			UserProfile updatedProfile = userProfileRepo.findOne(profile.getUsername());
			emitter.emit(new UserSummaryUpdated(new UserSummary(updatedProfile)));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.svend.dab.core.IProfilePhotoService#removeProfilePhoto(com.svend.dab.core.beans.profile.UserProfile, int)
	 */
	@Override
	public void removeProfilePhoto(UserProfile profile, int deletedPhotoIdx) {

		if (profile == null) {
			logger.log(Level.WARNING, "Cannot remove a photo from a null profile: not doing anything");
		} else {

			Photo removed = profile.getPhoto(deletedPhotoIdx);
			if (removed == null) {
				logger.log(Level.WARNING, "It seems the profile refused to remove photo with index == " + deletedPhotoIdx + " => not propagating any event");
			}

			if (profile.getMainPhotoIndex() < deletedPhotoIdx) {
				userProfileRepo.removeOnePhoto(profile.getUsername(), removed);
			} else if (profile.getMainPhotoIndex() == deletedPhotoIdx) {
				userProfileRepo.removeOnePhotoAndResetMainPhotoIndex(profile.getUsername(), removed);
				profile.setMainPhotoIndex(0);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.svend.dab.core.IProfilePhotoService#movePhotoToFirstPosition(com.svend.dab.core.beans.profile.UserProfile, int)
	 */
	@Override
	public void movePhotoToFirstPosition(UserProfile userProfile, int photoIndex) {
		if (userProfile != null && userProfile.getPhotos() != null && photoIndex >= 0 && photoIndex < userProfile.getPhotos().size()) {
			userProfileRepo.movePhotoToFirstPosition(userProfile.getUsername(), photoIndex);
			userProfile.setMainPhotoIndex(photoIndex);
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
	@Override
	public void updatePhotoCaption(UserProfile userProfile, int photoIndex, String profilePhotoCaption) {

		if (userProfile != null && userProfile.getPhotos() != null && photoIndex >= 0 && photoIndex < userProfile.getPhotos().size()) {

			Photo updatedPhoto = userProfile.getPhotos().get(photoIndex);
			if (profilePhotoCaption == null) {
				updatedPhoto.setCaption("");
			} else {
				updatedPhoto.setCaption(profilePhotoCaption);
			}
			userProfileService.updatePhotoGallery(userProfile, false);

		} else {
			logger.log(Level.WARNING, "Not updating photo caption: invalid index or user profile null or user profile with null photo set. Index=" + photoIndex);
		}
	}

}