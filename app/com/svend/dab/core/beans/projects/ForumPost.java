package com.svend.dab.core.beans.projects;

import java.util.Date;

import org.springframework.data.annotation.Transient;

import web.utils.Utils;

import com.svend.dab.core.beans.profile.UserSummary;

public class ForumPost {

	private String id;
	private String threadId;
	private String projectId;
	private Date creationDate;
	private UserSummary author;
	private String content;

	@Transient
	private String creationDateStr;
	
	@Transient
	private String authorProfilLink;
	
	private boolean userMayDelete;

	public ForumPost() {
		super();
	}

	public ForumPost(String threadId, String projectId, Date creationDate,
			UserSummary author, String content) {
		super();
		this.threadId = threadId;
		this.projectId = projectId;
		this.creationDate = creationDate;
		this.author = author;
		this.content = content;
		getCreationDateStr();
	}
	
	public void generatePhotoLink(Date expirationdate) {
		if (author != null) {
			author.generatePhotoLink(expirationdate);
		}
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getThreadId() {
		return threadId;
	}

	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		getCreationDateStr();
	}

	public UserSummary getAuthor() {
		return author;
	}

	public void setAuthor(UserSummary author) {
		this.author = author;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCreationDateStr() {
		creationDateStr = Utils.formatDateWithTime(creationDate);
		return creationDateStr;
	}

	public String getAuthorProfilLink() {
		return authorProfilLink;
	}

	public void setAuthorProfilLink(String authorProfilLink) {
		this.authorProfilLink = authorProfilLink;
	}

	public boolean isUserMayDelete() {
		return userMayDelete;
	}

	public void setUserMayDelete(boolean userMayDelete) {
		this.userMayDelete = userMayDelete;
	}

}
