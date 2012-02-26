/**
 * 
 */
package com.svend.dab.dao.aws.s3;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.svend.dab.core.beans.DabIllegalFormatException;
import com.svend.dab.core.beans.aws.S3PhotoLink;
import com.svend.dab.core.beans.profile.Photo;
import com.svend.dab.core.dao.IPhotoBinaryDao;

/**
 * @author Svend
 * 
 */
@Component
public class AwsS3PhotoDao implements IPhotoBinaryDao {

	@Autowired
	private AwsS3Tool awsS3Tool;

	private static Logger logger = Logger.getLogger(AwsS3PhotoDao.class.getName());

	
	public void removePhoto(S3PhotoLink removedPhoto) {

		if (removedPhoto == null) {
			logger.log(Level.WARNING, "Refusing to remove a null photo from AWS S3, not doing anything");
			return;
		}

		awsS3Tool.removeBinary(removedPhoto.getS3BucketName(), removedPhoto.getFullS3Key());

	}

	
	public void savePhoto(Photo photo, byte[] normalSizedPhoto, byte[] thumbPhoto, String mimeType) {
		if (photo == null) {
			throw new DabIllegalFormatException("Cannot save photo: null Photo");
		} else if (photo.getNormalPhotoLink() == null || photo.getThumbLink() == null) {
			throw new DabIllegalFormatException("Cannot save photo: Photo with one null link");
		} else if (photo.getNormalPhotoLink().getS3BucketName() == null || photo.getNormalPhotoLink().getS3Key() == null) {
			throw new DabIllegalFormatException("Cannot save photo: null bucket name or null s3 key");
		} else if (mimeType == null) {
			throw new DabIllegalFormatException("Cannot save photo: null mime type");
		} else {

			awsS3Tool.uploadBinary(photo.getNormalPhotoLink().getS3BucketName(), photo.getNormalPhotoLink().getS3Key(), normalSizedPhoto, mimeType);
			awsS3Tool.uploadBinary(photo.getThumbLink().getS3BucketName(), photo.getThumbLink().getS3Key(), thumbPhoto, mimeType);
			
		}
	}
}
