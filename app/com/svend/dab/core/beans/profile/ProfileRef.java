package com.svend.dab.core.beans.profile;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import play.mvc.Router;

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

	@Transient
	private String linkToProfile;

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
	
	public void prepareLinkToProfiles() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("vuser", this.userName);
		this.linkToProfile = Router.reverse("profile.ProfileView.profileView", params).toString();			
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

	public String getLinkToProfile() {
		return linkToProfile;
	}

	public void setLinkToProfile(String linkToProfile) {
		this.linkToProfile = linkToProfile;
	}


}
