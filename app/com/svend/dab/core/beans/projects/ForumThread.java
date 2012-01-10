package com.svend.dab.core.beans.projects;

import java.util.Date;

public class ForumThread {

	private String id;
	private String projectId;
	private String title;
	private Date creationDate;
	private int numberOfPosts = 0;

	public ForumThread() {
		super();
	}

	public ForumThread(String projectId, String title, Date creationDate,
			int numberOfPosts) {
		super();
		this.projectId = projectId;
		this.title = title;
		this.creationDate = creationDate;
		this.numberOfPosts = numberOfPosts;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public int getNumberOfPosts() {
		return numberOfPosts;
	}

	public void setNumberOfPosts(int numberOfPosts) {
		this.numberOfPosts = numberOfPosts;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

}
