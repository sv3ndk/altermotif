package models.altemotif.groups;

import java.util.LinkedList;
import java.util.List;

import com.svend.dab.core.beans.groups.GroupPep;
import com.svend.dab.core.beans.projects.Participation;

/**
 * 
 * Help class behind the "view group" page: contains a set of method returning boolean so that the page knows what is visible or not visible to the user who is
 * currently viewing this page
 * 
 * @author svend
 * 
 */
public class GroupViewVisibility {

	private final GroupPep pep;
	private final String visitingUser;
	private final List<Participation> projetsWhereUserIsAdmin = new LinkedList<Participation>();

	public GroupViewVisibility(GroupPep pep, String visitingUser) {
		super();
		this.pep = pep;
		this.visitingUser = visitingUser;
	}

	public void addProjectsWhereUserIsAdmin(List<Participation> projects) {
		if (projects != null) {
			projetsWhereUserIsAdmin.addAll(projects);
		}
	}

	// /////////////////////////////////////

	// toolbox
	public boolean isToolBoxVisible() {
		return isEditGroupLinkVisisble() || isEndGroupLinkVisisble();
	}

	public boolean isEditGroupLinkVisisble() {
		return pep.isUserAllowedToEditGroup(visitingUser);
	}

	public boolean isEndGroupLinkVisisble() {

		if (pep.isUserAllowedToCloseGroup(visitingUser)) {
			return true;
		}

		// if the user may not close the group, he may still see the link if he is admin => if he clicks, the GUI will tell him to first remove all other admins
		return pep.isUserAdmin(visitingUser);
	}

	public boolean isEndGroupLinkEffective() {
		return pep.isUserAllowedToCloseGroup(visitingUser);
	}

	// /////////////////////////////////
	// user participant management

	public boolean isApplyToGroupLinkVisisble() {
		return pep.isUserAllowedToApplyToGroup(visitingUser);
	}

	public boolean isAlreadyApplyToGroupLinkVisisble() {
		return pep.getGroup().hasAppliedForGroupMembership(visitingUser);
	}

	public boolean isUserApplicationsAreVisible() {
		return pep.isUserAllowedToAcceptAndRejectUserApplications(visitingUser);
	}

	public boolean isLeaveLinkVisible(String groupUserId) {
		if (groupUserId == null) {
			return false;
		}

		if (groupUserId.equals(visitingUser)) {
			return pep.isUserAllowedToLeaveGroup(visitingUser);
		} else {
			return false;
		}
	}

	public boolean isMakeAdminLinkVisible(String groupUserId) {
		return pep.isUserAllowedToMakeAdmin(visitingUser, groupUserId);
	}

	public boolean isMakeMemberLinkVisible(String groupUserId) {
		return pep.isUserAllowedToMakeMember(visitingUser, groupUserId);
	}

	public boolean isRemoveMemberLinkVisible(String groupUserId) {

		if (groupUserId == null) {
			return false;
		}

		if (groupUserId.equals(visitingUser)) {
			// even if a user may leave a group: the "remove member" is never displayed besides his profile (we show the "leave group" link in that case)
			return false;
		} else {
			return pep.isUserAllowedToRemoveUser(visitingUser, groupUserId);
		}
	}

	// /////////////////////////////////
	// project participant management

	public boolean isApplyWithProjetLinkVisible() {
		return projetsWhereUserIsAdmin != null && !projetsWhereUserIsAdmin.isEmpty();
	}

	public boolean isProjectsApplicationsAreVisible() {
		return pep.isUserAllowedToAcceptAndRejectProjectApplications(visitingUser);
	}

}
