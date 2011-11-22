package com.svend.dab.core.beans.profile;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.data.annotation.Id;

/**
 * @author svend
 * 
 */

public class Contact {
	
	@Id
	private String contactId;
	
	// there is no "rejected" status: when a request is rejected, the Contact is simply removed gefrom DB
	public enum STATUS {
		pending, accepted
	}
	
	// the user who sent the contact invitation 
	private UserSummary requestedByUser;
	
	// the user who received the contact invitation
	private UserSummary requestedToUser;
	
	private Date requestDate;
	private Date confirmDate;
	
	private STATUS status;
	private String invitationText;

	private static Logger logger = Logger.getLogger(Contact.class.getName());

	public Contact(String contactId) {
		super();
		this.contactId = contactId;
	}


	public Contact() {
		super();
	}
	

	// --------------------------------------------
	// --------------------------------------------

	@Override
	public String toString() {
		return "id = " + contactId + " fromUser: " + requestedByUser + " touser: " + requestedToUser;
	}
	
	
	
	public UserSummary getOtherUser(String username) {
		
		if (username == null) {
			return null;
		}
		
		if (requestedByUser != null && username.equals(requestedByUser.getUserName())) {
			return requestedToUser;
		} else {
			return requestedByUser;
			
		}
	}

	
	public boolean isHasInvitationText() {
		return invitationText != null && ! "".equals(invitationText);
	}
	
	/**
	 * @return
	 */
	public String getRequestingUserMainPhotoThumbLink() {
		if (requestedByUser == null ) {
			return "";
		}
		return requestedByUser.getMainPhotoThumbLink();
	}

	/**
	 * @return
	 */
	public String getRecipientUserMainPhotoThumbLink() {
		if (requestedToUser == null ) {
			return "";
		}
		return requestedToUser.getMainPhotoThumbLink();
	}

	
	/**
	 * @return
	 */
	public boolean isPending() {
		return status != null && status.equals(STATUS.pending);
	}

	/**
	 * @param username
	 * @return
	 */
	public boolean isRequestedBy(String username) {
		if (username == null || requestedByUser == null) {
			return false;
		} else {
			return username.equals(requestedByUser.getUserName());
		} 
	}
	/**
	 * @param username
	 * @return
	 */
	public boolean isRequestedTo(String username) {
		if (username == null || requestedToUser == null) {
			return false;
		} else {
			return username.equals(requestedToUser.getUserName());
		} 
	}

	/**
	 * @param username
	 * @return true if this user is part of the relationship described by this {@link Contact} object
	 */
	public boolean isInvolving(String username) {

		if (username == null ) {
			return false;
		}

		return isRequestedBy(username) || isRequestedTo(username);  
		
	}



	/**
	 * 
	 */
	public void generatePhotoLink(Date expirationdate) {
		
		if (requestedByUser != null) {
			requestedByUser.generatePhotoLink(expirationdate);
		}

		if (requestedToUser != null) {
			requestedToUser.generatePhotoLink(expirationdate);
		}
	}

	// --------------------------------------------
	// --------------------------------------------

	public Date getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}

	public Date getConfirmDate() {
		return confirmDate;
	}

	public void setConfirmDate(Date confirmDate) {
		this.confirmDate = confirmDate;
	}


	public STATUS getStatus() {
		return status;
	}

	public void setStatus(STATUS status) {
		this.status = status;
	}

	public String getInvitationText() {
		return invitationText;
	}

	public void setInvitationText(String invitationText) {
		this.invitationText = invitationText;
	}

	public String getContactId() {
		return contactId;
	}

	public void setContactId(String contactId) {
		this.contactId = contactId;
	}

	public UserSummary getRequestedByUser() {
		return requestedByUser;
	}

	public void setRequestedByUser(UserSummary requestedByUser) {
		logger.log(Level.INFO, "setting requestedByUser to " + requestedByUser);
		this.requestedByUser = requestedByUser;
	}

	public UserSummary getRequestedToUser() {
		return requestedToUser;
	}

	public void setRequestedToUser(UserSummary requestedToUser) {
		logger.log(Level.INFO, "setting requestedToUser to " + requestedToUser);
		this.requestedToUser = requestedToUser;
	}



}
