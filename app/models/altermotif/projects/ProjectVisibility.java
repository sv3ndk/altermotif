package models.altermotif.projects;

import com.svend.dab.core.beans.projects.Project;
import com.svend.dab.core.beans.projects.ProjectPep;

/**
 * @author svend
 *
 */
public class ProjectVisibility {
	
	private final ProjectPep pep;
	
	// this may be null (if the user is not logged in)
	private final String visitingUserId;
	
	private final Project project;
	
	
	public ProjectVisibility(ProjectPep pep, Project project, String visitingUserId) {
		super();
		this.pep = pep;
		this.project = project;
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
		return project.getPhotos() != null;
	}
	
	public boolean isEditPhotoGalleryLinkVisible() {
		return pep.isAllowedToEditPhotoGallery(visitingUserId);
	}
	

}
