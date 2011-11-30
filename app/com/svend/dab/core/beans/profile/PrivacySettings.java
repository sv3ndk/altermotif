package com.svend.dab.core.beans.profile;

/**
 * @author Svend
 * 
 */
public class PrivacySettings {

	// visibility defaults to everybody for all settings
	private VISIBILITY firstNameVisibility = VISIBILITY.everybody;
	private VISIBILITY lastNameVisibility = VISIBILITY.everybody;
	private VISIBILITY ageVisibility = VISIBILITY.everybody;
	private VISIBILITY photosVisibility = VISIBILITY.everybody;
	private VISIBILITY cvVisibility = VISIBILITY.everybody;
	
	// this must be false by default, because checkboxes are not submitted from HTML form => we only receive info if true (otherwise we must consider false)
	private boolean isProfileActive;

	// this enum must be aligned with the select items in the profilePrivacySettings.xhtml (or rather, this xhtml should be aligned with this enum...)
	public enum VISIBILITY {
		everybody,

		loggedin,

		mycontacts,

		nobody;

	}

	public PrivacySettings() {
		super();
	}


	public VISIBILITY getFirstNameVisibility() {
		return firstNameVisibility;
	}

	public void setFirstNameVisibility(VISIBILITY firstNameVisibility) {
		this.firstNameVisibility = firstNameVisibility;
	}

	public VISIBILITY getLastNameVisibility() {
		return lastNameVisibility;
	}

	public void setLastNameVisibility(VISIBILITY lastNameVisibility) {
		this.lastNameVisibility = lastNameVisibility;
	}

	public VISIBILITY getAgeVisibility() {
		return ageVisibility;
	}

	public void setAgeVisibility(VISIBILITY ageVisibility) {
		this.ageVisibility = ageVisibility;
	}

	public VISIBILITY getPhotosVisibility() {
		return photosVisibility;
	}

	public void setPhotosVisibility(VISIBILITY photosVisibility) {
		this.photosVisibility = photosVisibility;
	}

	public VISIBILITY getCvVisibility() {
		return cvVisibility;
	}

	public void setCvVisibility(VISIBILITY cvVisibility) {
		this.cvVisibility = cvVisibility;
	}

	public boolean isProfileActive() {
		return isProfileActive;
	}

	public void setProfileActive(boolean isProfileActive) {
		this.isProfileActive = isProfileActive;
	}


}
