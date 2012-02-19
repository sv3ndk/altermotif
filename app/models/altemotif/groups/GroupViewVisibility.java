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
		return pep.isUserAllowedToCloseGroup(visitingUser);
	}

	
	

}
