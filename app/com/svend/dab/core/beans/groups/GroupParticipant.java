package com.svend.dab.core.beans.groups;

import java.util.Date;

import com.svend.dab.core.beans.profile.UserSummary;

public class GroupParticipant {

	public enum ROLE {

		admin("groupRoleAdmin"),
		member("groupRoleMember");

		private final String label;

		private ROLE(String label) {
			this.label = label;
		}

		public String getLabel() {
			return label;
		}
	}

	private ROLE role;
	private UserSummary user;
	private boolean accepted = true;
	private String applicationText;

	public GroupParticipant(ROLE role, UserSummary user, boolean accepted, String applicationText) {
		this.role = role;
		this.user = user;
		this.accepted = accepted;
		this.applicationText = applicationText;
	}

	public GroupParticipant(ROLE role, UserSummary userSummary) {
		this(role, userSummary, true, "");
	}

	public void generatePhotoLinks(Date expirationdate) {
		if (user != null) {
			user.generatePhotoLink(expirationdate);
		}
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

}
