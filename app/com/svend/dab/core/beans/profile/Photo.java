package com.svend.dab.core.beans.profile;

import java.io.Serializable;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.data.annotation.Id;

import com.svend.dab.core.beans.aws.S3Link;

/**
 * @author svend
 * 
 */
public class Photo implements Serializable {

	private static Logger logger = Logger.getLogger(Photo.class.getName());

	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;

	private String caption;

	private S3Link normalPhotoLink;

	private S3Link thumbLink;

	// ----------------------------
	// ----------------------------

	public Photo() {
		super();
	}

	public Photo(String caption, S3Link normalPhotoLink, S3Link thumbLink) {
		super();
		this.caption = caption;
		this.normalPhotoLink = normalPhotoLink;
		this.thumbLink = thumbLink;
	}

	@Override
	public boolean equals(Object obj) {

		// "equals" here means "points to the same binary in s3"

		if (obj == null) {
			return false;
		}

		Photo otherPhoto = (Photo) obj;

		// caption is not taken into account for equals: if it is different, it is still the same photo (with a different caption):

		if (normalPhotoLink == null) {
			return otherPhoto.getNormalPhotoLink() == null;
		}

		return normalPhotoLink.equals(otherPhoto.getNormalPhotoLink());
	}

	// ----------------------------
	// ----------------------------

	/**
	 * @param expirationdate
	 */
	public void generatePresignedLinks(Date expirationdate, boolean generateNormalLink, boolean generateThumbLink) {

		if (normalPhotoLink != null && generateNormalLink) {
			normalPhotoLink.generateUrl(expirationdate);
		}

		if (thumbLink != null && generateThumbLink) {
			thumbLink.generateUrl(expirationdate);
		}

	}

	@Override
	public String toString() {
		return "Photo, generated link=" + getNormalPhotoAddress() + ", caption=" + getCaption() + " photo s3 location is " + normalPhotoLink;
	}

	/**
	 * 
	 * Tells to the UI if this photo is actually existing in persistence
	 * 
	 * @return
	 */
	public boolean getIsPhotoEmpty() {
		return isPhotoEmpty();
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public boolean isPhotoEmpty() {
		return normalPhotoLink == null;
	}

	/**
	 * Safe "getLink" for the UI (delegates to the wrapped link)
	 * 
	 * @return
	 */
	public String getNormalPhotoAddress() {
		if (normalPhotoLink == null) {
			return null;
		}

		return normalPhotoLink.getAddress();
	}

	public S3Link getThumbLink() {
		return thumbLink;
	}

	/**
	 * Safe "getLink" for the UI (delegates to the wrapped link)
	 * 
	 * @return
	 */
	public String getThumbAddress() {
		if (thumbLink == null) {
			return null;
		}

		return thumbLink.getAddress();
	}

	// ----------------------------
	// ----------------------------

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public S3Link getNormalPhotoLink() {
		return normalPhotoLink;
	}

	public void setNormalPhotoLink(S3Link wrappedLink) {
		this.normalPhotoLink = wrappedLink;
	}

	public void setThumbLink(S3Link thumbLink) {
		this.thumbLink = thumbLink;
	}

}
