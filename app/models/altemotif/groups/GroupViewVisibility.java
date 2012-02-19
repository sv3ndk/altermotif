package models.altemotif.groups;

import com.svend.dab.core.beans.groups.GroupPep;

/**
 * 
 * Help class behind the "view group" page: contains a set of method returning boolean so that the 
 * page knows what is visible or not visible to the user who is currently viewing this page
 * 
 * @author svend
 *
 */
public class GroupViewVisibility {
	
	
	private final GroupPep pep;
	private final String visitingUser;
	
	public GroupViewVisibility(GroupPep pep, String visitingUser) {
		super();
		this.pep = pep;
		this.visitingUser = visitingUser;
	}

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
	
	

}
