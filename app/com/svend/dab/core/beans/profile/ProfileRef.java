/**
 * 
 */
package com.svend.dab.core.beans.profile;

import org.springframework.data.annotation.Id;

/**
 * 
 * This bean contains just enough information to be able to build a link to a user profile
 * 
 * @author Svend
 * 
 */
public class ProfileRef {

	@Id
	private String userName;
	private boolean isProfileActive = true;
	

	public ProfileRef(String userName, boolean isProfileActive) {
		super();
		this.userName = userName;
		this.isProfileActive = isProfileActive;
	}

	public ProfileRef() {
		super();
	}

	public ProfileRef(UserProfile user) {
		this(user.getUsername(), user.getPrivacySettings().isProfileActive());
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public boolean isProfileActive() {
		return isProfileActive;
	}

	public void setProfileActive(boolean isProfileActive) {
		this.isProfileActive = isProfileActive;
	}

}
