package com.svend.dab.core.beans.message;

import java.io.Serializable;
import java.util.Date;


import com.svend.dab.core.beans.profile.ProfileRef;
import com.svend.dab.core.beans.profile.UserSummary;

/**
 * @author Svend
 * 
 */
public class UserMessage implements Serializable {

	private static final long serialVersionUID = -3623814541602126417L;

	private String id;
	
	private ProfileRef fromUser;
	private ProfileRef toUser;
	private String subject;
	private String content;
	private Date creationDate;
	private Boolean read = Boolean.FALSE;
	private Boolean deletedByRecipient = Boolean.FALSE;
	private Boolean deletedByEmitter = Boolean.FALSE;

	// -----------------------------------
	//

	public UserMessage() {
		super();
	}

	public UserMessage(ProfileRef fromUser, ProfileRef toUser, String subject, String content, Date creationDate) {
		super();
		this.fromUser = fromUser;
		this.toUser = toUser;
		this.subject = subject;
		this.content = content;
		this.creationDate = creationDate;
	}

	public UserMessage(UserMessage message) {
		if (message != null) {
			this.fromUser = message.fromUser;
			this.toUser = message.toUser;
			this.subject = message.subject;
			this.content = message.content;
			this.creationDate = message.creationDate;
			this.read = message.read;
			this.deletedByEmitter = message.deletedByEmitter;
			this.deletedByRecipient = message.deletedByRecipient;
		}
	}

	// -----------------------------------
	//

	// -----------------------------------
	// computed fields

	public Date getCreationDateTime() {
		if (creationDate == null) {
			return null;
		} else {
			return creationDate;
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	// -----------------------------------
	//

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Boolean isRead() {
		return read;
	}

	public void setRead(Boolean read) {
		this.read = read;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Boolean getDeletedByRecipient() {
		return deletedByRecipient;
	}

	public void setDeletedByRecipient(Boolean deletedByRecipient) {
		this.deletedByRecipient = deletedByRecipient;
	}

	public Boolean getDeletedByEmitter() {
		return deletedByEmitter;
	}

	public void setDeletedByEmitter(Boolean deletedByEmitter) {
		this.deletedByEmitter = deletedByEmitter;
	}

	public ProfileRef getFromUser() {
		return fromUser;
	}

	public void setFromUser(ProfileRef fromUser) {
		this.fromUser = fromUser;
	}

	public ProfileRef getToUser() {
		return toUser;
	}

	public void setToUser(ProfileRef toUser) {
		this.toUser = toUser;
	}

}
