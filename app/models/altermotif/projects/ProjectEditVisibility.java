package models.altermotif.projects;

import com.svend.dab.core.beans.projects.ProjectPep;

public class ProjectEditVisibility {
	
	private final ProjectPep pep;
	
	// this may be null (if the user is not logged in)
	private final String visitingUserId;
	

	public ProjectEditVisibility(ProjectPep pep, String visitingUserId) {
		super();
		this.pep = pep;
		this.visitingUserId = visitingUserId;
	}
	
	/////////////////////////////////////////////////////
	//

	public boolean isEditOfferVisible() {
		return pep.isAllowedToEditProjectOffer(visitingUserId);
	}

	public boolean isEditReasonVisible() {
		return pep.isAllowedToEditProjectReason(visitingUserId);
	}

	public boolean isEditTagsVisible() {
		return pep.isAllowedToEditProjectTags(visitingUserId);
	}

	public boolean isEditThemesVisible() {
		return pep.isAllowedToEditProjectThemes(visitingUserId);
	}
	
	public boolean isEditTasksVisible() {
		return pep.isAllowedToEditProjectTasks(visitingUserId);
	}

}
