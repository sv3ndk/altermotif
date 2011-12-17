package com.svend.dab.core.beans.projects;

import java.util.Date;

import com.svend.dab.core.beans.profile.UserProfile;
import com.svend.dab.core.beans.profile.UserSummary;

/**
 * 
 * 
 * 
 * @author Svend
 * 
 */
public class Participant {

	public enum ROLE {

		// "initiator" is actually the "owner" (initiator is the old term, we changed to "owner" when we introduced the concept of transfer)
		initiator("projectRoleInitiator"),

		admin("projectRoleAdmin"),

		member("projectRoleMember");

		private final String label;

		private ROLE(String label) {
			this.label = label;
		}

		public String getLabel() {
			return label;
		}
	}

	private ROLE role;

	// this is true if the current owner of the project has offered the owner of the project to this participant, but he has not accepted/refused yet
	private boolean ownershipProposed = false;

	private UserSummary user;

	// this determine if the application to participate to this project has been accepted
	private boolean accepted = true;

	private String applicationText;

	public Participant(ROLE role, UserProfile user, String applicationText) {
		super();
		this.role = role;
		this.applicationText = applicationText;
		this.user = new UserSummary(user);
	}

	public Participant() {
		super();
	}

	public void generatePhotoLinks(Date expirationdate) {
		user.generatePhotoLink(expirationdate);
	}

	public ROLE getRole() {
		return role;
	}

	public void setRole(ROLE role) {
		this.role = role;
	}

	public UserSummary getUser() {
		return user;
	}

	public void setUser(UserSummary user) {
		this.user = user;
	}

	public boolean isAccepted() {
		return accepted;
	}

	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}

	public String getApplicationText() {
		return applicationText;
	}

	public void setApplicationText(String applicationText) {
		this.applicationText = applicationText;
	}

	public boolean isOwnershipProposed() {
		return ownershipProposed;
	}

	public void setOwnershipProposed(boolean ownershipProposed) {
		this.ownershipProposed = ownershipProposed;
	}

}
