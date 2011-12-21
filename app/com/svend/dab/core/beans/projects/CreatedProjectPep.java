package com.svend.dab.core.beans.projects;

/**
 * Special case of the policy enforcement point in the case of created project: in taht case, the concept of "role" of the editing user is of different (the logged in user will become owner, but for the moment he is not yet anything on this not-yet existing proejct)
 * 
 * @author svend
 *
 */
public class CreatedProjectPep extends ProjectPep {

	public CreatedProjectPep() {
		super(null);
	}
	
	///
	
	@Override
	public boolean isAllowedToEditProjectOffer(String user) {
		return true;
	}
	
	 @Override
	public boolean isAllowedToEditProjectTags(String user) {
		 return true;
	}
	 
	 @Override
	public boolean isAllowedToEditProjectReason(String user) {
		 return true;
	}
	
	
}
