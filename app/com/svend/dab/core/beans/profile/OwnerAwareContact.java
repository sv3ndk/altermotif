/**
 * 
 */
package com.svend.dab.core.beans.profile;

import java.util.Date;

/**
 * 
 * A {@link Contact} who is able to tell who the "other" user is => he knows his owner
 * 
 * @author Svend
 * 
 */
public class OwnerAwareContact extends Contact {

	private final String contactOwner;

	private final Contact delegate;

	private UserSummary cachedOtherUser;

	public OwnerAwareContact(Contact contact, String contactOwner) {
		super();
		this.contactOwner = contactOwner;
		this.delegate = contact;
	}

	/**
	 * OK, this is a bit ugly: when we store the {@link Contact} inside a profile, we only store ony {@link UserSummary}: the one of the "other" user,
	 * independantly of who is the "requester" of the contact relationship
	 * 
	 * => this method returns this "other" user
	 * 
	 * @return
	 */
	public UserSummary getOtherUser() {

		if (cachedOtherUser == null) {
			cachedOtherUser = delegate.getOtherUser(contactOwner);
		}

		return cachedOtherUser;
	}

	// ---------------------------------------
	// delegation to the actual contact

	public boolean isHasInvitationText() {
		return delegate.isHasInvitationText();
	}

	public String getRequestingUserMainPhotoThumbLink() {
		return delegate.getRequestingUserMainPhotoThumbLink();
	}

	public String getRecipientUserMainPhotoThumbLink() {
		return delegate.getRecipientUserMainPhotoThumbLink();
	}

	public boolean isPending() {
		return delegate.isPending();
	}

	public boolean isRequestedBy(String username) {
		return delegate.isRequestedBy(username);
	}


	public boolean isRequestedTo(String username) {
		return delegate.isRequestedTo(username);
	}

	public boolean isInvolving(String username) {
		return delegate.isInvolving(username);
	}



	public void generatePhotoLink(Date expirationdate) {
		delegate.generatePhotoLink(expirationdate);
	}

	public Date getRequestDate() {
		return delegate.getRequestDate();
	}

	public void setRequestDate(Date requestDate) {
		delegate.setRequestDate(requestDate);
	}

	public Date getConfirmDate() {
		return delegate.getConfirmDate();
	}

	public void setConfirmDate(Date confirmDate) {
		delegate.setConfirmDate(confirmDate);
	}

	public STATUS getStatus() {
		return delegate.getStatus();
	}

	public void setStatus(STATUS status) {
		delegate.setStatus(status);
	}

	public boolean equals(Object obj) {
		return delegate.equals(obj);
	}

	public String getInvitationText() {
		return delegate.getInvitationText();
	}

	public void setInvitationText(String invitationText) {
		delegate.setInvitationText(invitationText);
	}

	public String getContactId() {
		return delegate.getContactId();
	}

	public void setContactId(String contactId) {
		delegate.setContactId(contactId);
	}

	public UserSummary getRequestedByUser() {
		return delegate.getRequestedByUser();
	}

	public void setRequestedByUser(UserSummary requestedByUser) {
		delegate.setRequestedByUser(requestedByUser);
	}

	public UserSummary getRequestedToUser() {
		return delegate.getRequestedToUser();
	}

	public void setRequestedToUser(UserSummary requestedToUser) {
		delegate.setRequestedToUser(requestedToUser);
	}

	public String toString() {
		return delegate.toString();
	}

}
