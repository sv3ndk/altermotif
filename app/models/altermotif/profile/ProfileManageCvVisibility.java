package models.altermotif.profile;

import models.altermotif.AbstractRenderableModel;

import com.svend.dab.core.beans.profile.UserProfile;

public class ProfileManageCvVisibility extends AbstractRenderableModel{

	public static String CV_VISIBILITY_RENDERARG_NAME = "cvVisibility";

	
	private final UserProfile editedUserProfile;

	public ProfileManageCvVisibility(UserProfile editedUserProfile) {
		super();
		this.editedUserProfile = editedUserProfile;
	}

	
	public boolean isDownloadCvLinkVisible() {
		return editedUserProfile.hasCv();
	}


	@Override
	protected String getRenderParamName() {
		return CV_VISIBILITY_RENDERARG_NAME;
	}
	
}
