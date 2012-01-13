package com.svend.dab.core.beans.projects;

import java.util.Date;

import com.svend.dab.core.beans.profile.ProfileRef;

public class ForumPost {

	private String id;
	private String threadId;
	private String projectId;
	private Date creationDate;
	private ProfileRef author;
	private String content;
	

	public ForumPost() {
		super();
	}

	public ForumPost(String threadId, String projectId, Date creationDate,
			ProfileRef author, String content) {
		super();
		this.threadId = threadId;
		this.projectId = projectId;
		this.creationDate = creationDate;
		this.author = author;
		this.content = content;
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
	}

	public ProfileRef getAuthor() {
		return author;
	}

	public void setAuthor(ProfileRef author) {
		this.author = author;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
