package models.altermotif.profile;

import java.util.List;

public class RemoveContactsJsonResponse {

	private List<String> removedIds;
	
	private boolean isAddToContactLinkVisible;
	
	private boolean isAddedToContactLinkVisible;

	public List<String> getRemovedIds() {
		return removedIds;
	}

	public void setRemovedIds(List<String> removedIds) {
		this.removedIds = removedIds;
	}

	public boolean isAddToContactLinkVisible() {
		return isAddToContactLinkVisible;
	}

	public void setAddToContactLinkVisible(boolean isAddToContactLinkVisible) {
		this.isAddToContactLinkVisible = isAddToContactLinkVisible;
	}

	public boolean isAddedToContactLinkVisible() {
		return isAddedToContactLinkVisible;
	}

	public void setAddedToContactLinkVisible(boolean isAddedToContactLinkVisible) {
		this.isAddedToContactLinkVisible = isAddedToContactLinkVisible;
	}
	
	
}
