package com.svend.dab.core.beans;

import java.util.List;

import org.springframework.data.annotation.Transient;

import play.mvc.Router;

import com.svend.dab.core.beans.profile.Photo;

public class PhotoAlbum {

	private List<Photo> photos;
	
	@Transient
	private final String photoS3RootFolder;
	@Transient
	private final String thumbS3RootFolder;
	
	// this implies the same max for all galleries! => store this out of here!
	public static final int MAX_NUMBER_OF_PHOTOS = 20;
	
	private final String defaultMainPhoto;
	
	private int mainPhotoIndex;
	
	// arrays of 20 links to the photos of the user (no matter how many actually present in DB)
	@Transient
	private PhotoPack cachedPhotosPack20;

	public PhotoAlbum(String photoS3RootFolder, String thumbS3RootFolder, String defaultMainPhoto) {
		super();
		this.photoS3RootFolder = photoS3RootFolder;
		this.thumbS3RootFolder = thumbS3RootFolder;
		this.defaultMainPhoto = defaultMainPhoto;
	}
	
	
	//////////////////////////////////////
	//
	
	public String getMainPhotoAddress() {
		
		if (getMainPhoto().getNormalPhotoAddress() == null) {
			return Router.reverse(defaultMainPhoto).toString();
		} else {
			return getMainPhoto().getNormalPhotoAddress();
		}
		
	}
	
	/**
	 * @return
	 */
	public Photo getMainPhoto() {

		if (photos != null && !photos.isEmpty()) {
			return photos.get(mainPhotoIndex);
		}

		return new Photo();
	}

	public List<Photo> getPhotosPack20() {
		if (cachedPhotosPack20 == null) {
			synchronized (this) {
				if (cachedPhotosPack20 == null) {
					cachedPhotosPack20 = new  PhotoPack(20, photos);
				}
			}
		}

		return cachedPhotosPack20.getPack();
	}
	
	
	public boolean isPhotoPackFull() {
		return photos != null && photos.size() > MAX_NUMBER_OF_PHOTOS;
	}

	public String getPhotoS3RootFolder() {
		return photoS3RootFolder;
	}

	public String getThumbsS3RootFolder() {
		return thumbS3RootFolder;
	}
	
	public boolean isPhotoPackEmpty() {
		return photos == null || photos.size() == 0;
	}
	
	public Photo getPhoto(int photoIdx) {
		if (isPhotoPackEmpty() || photos.size() <= photoIdx) {
			return null;
		}
		return photos.get(photoIdx);
	}
	

}
