package models.altermotif.projects;

import com.google.common.base.Strings;
import com.svend.dab.core.beans.projects.Project;
import com.svend.dab.core.beans.projects.ProjectPep;
import com.svend.dab.core.beans.projects.Participant.ROLE;

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
		if (pep.isAllowedToCancelProject(visitingUserId)) {
			return true;
		}
		
		// if the user may not cancel the project, maybe he can still see the link (and receive a message saying to first remove users) 
		// this is a pure UI concept => not present in the PEP but here 
		ROLE role = project.findRoleOfUser(visitingUserId);
		return role == ROLE.initiator;
		
	}

	/**
	 * @return if true, clicking on the link actually triggers the cancellation. Otherwise, just a message is displayed
	 */
	public boolean isCancelProjectLinkEffective() {
		return pep.isAllowedToCancelProject(visitingUserId);
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
	
	//////////////////////////////////
	// applications
	
	public boolean isVisibleApplicationBox() {
		return pep.isAllowedToSeeApplications(visitingUserId);
	}
	
	public boolean isAllowedToAcceptOrRejectApplications() {
		return pep.isAllowedToAcceptOrRejectApplications(visitingUserId);
	}
	
	
	public boolean isApplyLinkVisible() {
		if (visitingUserId == null) {
			return false;
		}
		return ! project.isUserAlreadyMember(visitingUserId) && ! project.isUserApplying(visitingUserId);
	}
	
	public boolean isCancelApplicationLinkVisible() {
		if (visitingUserId == null) {
			return false;
		}
		return project.isUserApplying(visitingUserId);
	}
	
	public boolean isDeleteParticipantLinkVisible(String rejectedUserId) {
		return pep.isAllowedToEjectParticipant(visitingUserId, rejectedUserId) && ! Strings.isNullOrEmpty(visitingUserId) && !visitingUserId.equals(rejectedUserId);
	}
	
	public boolean isLeaveProjectLinkVisible(String rejectedUserId) {
		return visitingUserId != null && visitingUserId.equals(rejectedUserId) && pep.isAllowedToLeave(visitingUserId);
	}
	
	public boolean isMakeAdminLinkVisible(String upgradedUserId) {
		return pep.isAllowedToMakeAdmin(visitingUserId, upgradedUserId);
	}

	public boolean isMakeMemberLinkVisible(String downgradedUser) {
		return pep.isAllowedToMakeMember(visitingUserId, downgradedUser);
	}

	public boolean isGiveOwnershipLinkVisible(String upgradedUser) {
		return pep.isAllowedToGiveOwnership(visitingUserId, upgradedUser);
	}

	public boolean isCancelGiveOwnershipLinkVisible(String upgradedUser) {
		return pep.isAllowedToCancelOwnershipTransfer(visitingUserId, upgradedUser);
	}

	public boolean isAcceptOwnershipLinkVisible(String upgradedUser) {
		// we only display this link in the container showing this user (this is not a PEP related decision, this is just  a graphical decision)
		return upgradedUser != null && upgradedUser.equals(visitingUserId) && pep.isAllowedToAcceptOrRefuseOwnershipTransfer(visitingUserId);
	}
	
	public boolean isRefuseOwnershipLinkVisible(String upgradedUser) {
		// we only display this link in the container showing this user (this is not a PEP related decision, this is just  a graphical decision)
		return upgradedUser != null && upgradedUser.equals(visitingUserId) && pep.isAllowedToAcceptOrRefuseOwnershipTransfer(visitingUserId);
	}

}
