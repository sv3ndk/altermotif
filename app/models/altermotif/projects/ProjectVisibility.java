package models.altermotif.projects;

import com.svend.dab.core.beans.projects.ProjectPep;

/**
 * @author svend
 *
 */
public class ProjectVisibility {
	
	private final ProjectPep pep;
	
	// this may be null (if the user is not logged in)
	private final String visitingUserId;
	
	
	public ProjectVisibility(ProjectPep pep, String visitingUserId) {
		super();
		this.pep = pep;
		this.visitingUserId = visitingUserId;
	}


	
	
	//////////////////////////
	// toolbox visibility
	
	public boolean isEditProjectLinkVisisble() {
		return pep.isAllowedToEditAtLeastPartially(visitingUserId);
	}
	

	public boolean isCancelProjectLinkVisisble() {
		return pep.isAllowedToCancel(visitingUserId);
	}

	public boolean isEndProjectLinkVisisble() {
		return pep.isAllowedToTerminate(visitingUserId);
	}
	
	
	public boolean isToolBoxVisible() {
		return isEditProjectLinkVisisble() || isCancelProjectLinkVisisble() || isEndProjectLinkVisisble();
	}
	
	
	///////////////////////////////
	// photo gallery
	
	public boolean isPhotoGalleryVisible() {
		return false;
	}
	

}
