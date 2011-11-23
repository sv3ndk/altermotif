/**
 * 
 */
package com.svend.dab.core;


import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.svend.dab.core.beans.Config;
import com.svend.dab.core.beans.DabException;
import com.svend.dab.core.beans.profile.Photo;
import com.svend.dab.core.beans.profile.UserProfile;
import com.svend.dab.core.beans.profile.UserSummary;
import com.svend.dab.core.dao.IPhotoBinaryDao;
import com.svend.dab.eda.EventEmitter;
import com.svend.dab.eda.events.profile.UserSummaryUpdated;
import com.svend.dab.eda.events.s3.BinaryNoLongerRequiredEvent;
import com.svend.dab.core.beans.DabUploadFailedException;
import com.svend.dab.core.beans.DabUploadFailedException.failureReason;

/**
 * @author Svend
 * 
 */
@Service
public class ProfilePhotoService implements IProfilePhotoService {

	public static int NORMAL_PHOTO_MAX_GREATEST_DIMENSION = 800;
	
	// keeping 160px and not 80 in order to improve image quality of the thumbnail
	public static int THUMB_PHOTO_MAX_GREATEST_DIMENSION = 160;
	
	public static String JPEG_MIME_TYPE ="image/jpeg";

	@Autowired
	private IPhotoBinaryDao photoDao;

	@Autowired
	private Config config;

	@Autowired
	private EventEmitter emitter;

	@Autowired
	private IUserProfileService userProfileService;

	private static Logger logger = Logger.getLogger(ProfilePhotoService.class.getName());

	// -------------------------------------
	// -------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.svend.dab.core.IProfilePhotoService#addOnePhoto(com.svend.dab.core.beans.profile.UserProfile, byte[])
	 */
	@Override
	
	public void addOnePhoto(UserProfile profile, byte[] photoContent) {

		final InputStream photoContentStream = new BufferedInputStream(new ByteArrayInputStream(photoContent));
		try {

			// size should never be too big here: this is checked while reading the stream in the upload servlet
			if (photoContent == null || photoContent.length == 0 || photoContent.length > config.getMaxUploadedPhotoSizeInBytes()) {
				throw new DabUploadFailedException("Photo size is 0", failureReason.fileFormatIncorrectError);
			}

			Photo photo = profile.createOnePhotoPlaceholder();

			// todo: optimization: we could probably launch two threads here to perform both operation in parallel...
			byte[] normalSize = resizePhotoToNormalSize(photoContent);
			byte[] thumbSize = resizePhotoToThumbSize(photoContent);

			photoDao.savePhoto(photo, normalSize, thumbSize, JPEG_MIME_TYPE);
			userProfileService.updatePhotoGallery(profile);
			

			if (photo.equals(profile.getMainPhoto())) {
				// sends a event to propagate the photo update
				emitter.emit(new UserSummaryUpdated(new UserSummary(profile)));
			}

		} finally {
			try {
				photoContentStream.close();
			} catch (IOException e) {
				logger.log(Level.WARNING, "Could not close uploaded content stream", e);
			}
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
			Photo removed = profile.removePhoto(deletedPhotoIdx);
			
			if (removed == null) {
				logger.log(Level.WARNING, "It seems the profile refused to remove photo with index == " + deletedPhotoIdx + " => not propagating any event");
				return;
			}
			
			userProfileService.updatePhotoGallery(profile);

			// sends a event to propagate the photo update
			emitter.emit(new UserSummaryUpdated(new UserSummary(profile)));

			// actual removal of the file from s3 is done asynchronously, in order to improve gui response time
			try {
				
				emitter.emit(new BinaryNoLongerRequiredEvent(removed.getNormalPhotoLink()));
			} catch (DabException e) {
				logger.log(Level.WARNING, "Could not emit event for removing one photo of profile " + profile.getUsername()
						+ " => this might lead to dead space in s3 storage", e);
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
			userProfile.movePhotoToFirstPosition(photoIndex);
			userProfileService.updatePhotoGallery(userProfile);

			// sends a event to propagate the photo update
			emitter.emit(new UserSummaryUpdated(new UserSummary(userProfile)));

		} else {
			logger.log(Level.WARNING, "Not setting photo as profile photo: invalid index or user profile null or user profile with null photo set. Index="
					+ photoIndex);
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
			userProfileService.updatePhotoGallery(userProfile);

		} else {
			logger.log(Level.WARNING, "Not updating photo caption: invalid index or user profile null or user profile with null photo set. Index=" + photoIndex);
		}
	}

	// -------------------------------------
	// -------------------------------------


	
	
	/**
	 * @param photoContent
	 * @return
	 */
	protected byte[] resizePhotoToThumbSize(byte[] photoContent) {
		return resizePhotoToTargetSize(photoContent, THUMB_PHOTO_MAX_GREATEST_DIMENSION);
	}
	
	
	
	/**
	 * @param photoContent
	 * @return
	 */
	protected byte[] resizePhotoToNormalSize(byte[] photoContent) {
		return resizePhotoToTargetSize(photoContent, NORMAL_PHOTO_MAX_GREATEST_DIMENSION);
	}
	

	/**
	 * @param photoContent
	 * @return
	 */
	protected byte[] resizePhotoToTargetSize(byte[] photoContent, int targetMaxDimension) {

		InputStream in = null;
		ByteArrayOutputStream baos = null;
		try {
			in = new ByteArrayInputStream(photoContent);
			BufferedImage image = ImageIO.read(in);
			
			if (image == null) {
				throw new DabUploadFailedException("cannot read this image", failureReason.fileFormatIncorrectError);
			}

			double scaleCoef = computeCoef(image.getWidth(), image.getHeight(), targetMaxDimension);

			if (scaleCoef == 1d) {
				return photoContent;
			} else {

				
				int newWidth = (int) (image.getWidth() * scaleCoef);
				int newHeight = (int) (image.getHeight() * scaleCoef);

				int type = image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image.getType();
				BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, type);

				Graphics2D g = resizedImage.createGraphics();
				g.setComposite(AlphaComposite.Src);
				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.drawImage(image, 0, 0, newWidth, newHeight, null);
				g.dispose();
				
				baos = new ByteArrayOutputStream();
				ImageIO.write( resizedImage, "jpg", baos );
				
				return baos.toByteArray();

			}

		} catch (IOException e) {
			throw new DabUploadFailedException("", failureReason.technicalError, e);
			
		} finally {

			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					logger.log(Level.WARNING, "Error while trying to close stream, ignoring...", e);
				}
			}

			if (baos != null) {
				try {
					baos.close();
				} catch (Exception e) {
					logger.log(Level.WARNING, "Error while trying to close stream, ignoring...", e);
				}
			}

		}
	}

	/**
	 * @param width
	 * @param height
	 * @param targetMaxDimension
	 * @return
	 */
	private double computeCoef(int width, int height, int targetMaxDimension) {

		if (width == 0 || height == 0) {
			// not scaling a 0 sized image
			return 1d;
		}

		if (width > targetMaxDimension || height > targetMaxDimension) {

			if (width > height) {
				return (double) targetMaxDimension / width;
			} else {
				return (double) targetMaxDimension / height;
			}

		} else {
			return 1d;
		}
	}



}
