package com.svend.dab.core.beans.profile;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Id;

/**
 * @author Svend
 * 
 *         subset of the atttributes defining a user
 * 
 */
public class UserSummary implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	private String userName;
	private boolean isProfileActive = true;
	
	private Photo mainPhoto;
	private String location;
	

	@Override
	public String toString() {
		return "userName: " + userName + ", location="+location;
	}

	// ------------------------
	//

	public UserSummary() {
		super();
	}


	public UserSummary(UserProfile user) {
		this(user.getUsername(), user.getPdata(), user.getMainPhoto(), user.getPrivacySettings().isProfileActive());
	}
	
	
	public UserSummary(String username, PersonalData personalData, Photo mainPhoto, boolean active) {
		this.userName = username;
		this.location = personalData.getLocation();
		this.mainPhoto = mainPhoto;
		this.isProfileActive = active;
	}
	

	/**
	 * copy constructor
	 * 
	 * @param user
	 */
	public UserSummary(UserSummary user) {
		if (user != null) {
			this.userName = user.getUserName();
			this.location = user.getLocation();
			this.mainPhoto = user.getMainPhotoThumb();
		}
	}

	// ------------------------
	//
	
	


	/**
	 * 
	 */
	public void generatePhotoLink(Date expirationdate) {
		if (mainPhoto != null) {
			mainPhoto.generatePresignedLinks(expirationdate, false, true);
		}
	}
	
	
	/**
	 * @return
	 */
	public String getMainPhotoThumbLink() {
		if (mainPhoto == null ) {
			return "";
		} else {
			return mainPhoto.getThumbAddress();
		}
	}
	
	// ------------------------
	//

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Photo getMainPhotoThumb() {
		return mainPhoto;
	}

	public void setMainPhotoThumb(Photo mainPhotoThumb) {
		this.mainPhoto = mainPhotoThumb;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public boolean isProfileActive() {
		return isProfileActive;
	}


	public void setProfileActive(boolean isProfileActive) {
		this.isProfileActive = isProfileActive;
	}


}
