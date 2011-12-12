package com.svend.dab.core.beans.profile;

/**
 * security Policy enforcement point for actions on the Privacy settings of a {@link UserProfile} 
 * 
 * @author svend
 *
 */
public class PrivacySettingsPep {

	private final UserProfile profile;

	public PrivacySettingsPep(UserProfile profile) {
		super();
		this.profile = profile;
	}

	
	public boolean isAllowedToUpdateProfileActiveStatus() {
		if (profile == null) {
			return false;
		}
		
		if (!profile.getPrivacySettings().isProfileActive()) {
			// if a user is inactive, he may always get back to active
			return true;
		} else {
			// otherwise, he may only desactivate if he is not the owner of a project 
			return ! profile.isOwnerOfAtLeastOneProject();
		}
		
	}
	
}
