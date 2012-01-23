package com.svend.dab.core.beans.projects;

import java.util.Date;

import org.springframework.data.annotation.Transient;

import web.utils.Utils;

/**
 * @author svend
 *
 */
public class ForumThread {

	private String id;
	
	private String projectId;
	
	
	private String title;
	private Date creationDate;
	private boolean isThreadPublic;
	private int numberOfPosts = 0;

	@Transient
	private String creationDateStr;
	
	// a bit ugly: this is only not null when sent to the browser in JSON
	@Transient
	private boolean mayUserUpdateVisibility;

	@Transient
	private boolean mayUserDeleteThisThread;

	@Transient
	private String threadUrl;

	public ForumThread(String projectId, String title, Date creationDate, int numberOfPosts, boolean isThreadPublic) {
		super();
		this.projectId = projectId;
		this.title = title;
		this.creationDate = creationDate;
		this.numberOfPosts = numberOfPosts;
		this.isThreadPublic = isThreadPublic;
		
		// makes sure it is translated directly
		getCreationDateStr();
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
		getCreationDateStr();
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

	public String getCreationDateStr() {
		creationDateStr = Utils.formatDate(creationDate);
		return creationDateStr;
	}

	public boolean isThreadPublic() {
		return isThreadPublic;
	}

	public void setThreadPublic(boolean isThreadPublic) {
		this.isThreadPublic = isThreadPublic;
	}

	public boolean isMayUserUpdateVisibility() {
		return mayUserUpdateVisibility;
	}

	public void setMayUserUpdateVisibility(boolean mayUserUpdateVisibility) {
		this.mayUserUpdateVisibility = mayUserUpdateVisibility;
	}

	public boolean isMayUserDeleteThisThread() {
		return mayUserDeleteThisThread;
	}

	public void setMayUserDeleteThisThread(boolean mayUserDeleteThisThread) {
		this.mayUserDeleteThisThread = mayUserDeleteThisThread;
	}

	public String isThreadUrl() {
		return threadUrl;
	}

	public void setThreadUrl(String threadUrl) {
		this.threadUrl = threadUrl;
	}


}
