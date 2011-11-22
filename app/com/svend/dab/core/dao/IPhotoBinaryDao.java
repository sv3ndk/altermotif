package com.svend.dab.core.dao;

import com.svend.dab.core.beans.aws.S3PhotoLink;
import com.svend.dab.core.beans.profile.Photo;

/**
 * @author Svend
 *
 */
public interface IPhotoBinaryDao {

	
	
	/**
	 * @param removedPhoto
	 */
	public abstract void removePhoto(S3PhotoLink removedPhoto);
	

//	public abstract void savePhoto(Photo photo, InputStream photoContentStream, String detectedMimeType, int length);


	public abstract void savePhoto(Photo photo, byte[] normalSize, byte[] thumbSize, String mimeType);
	

	
}
