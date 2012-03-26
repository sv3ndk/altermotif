package com.svend.dab.core.beans;

import java.util.Date;

import org.springframework.data.annotation.Transient;

import web.utils.Utils;

import com.svend.dab.core.beans.profile.UserSummary;

public class ForumPost {

	public static long ONE_MINUTE_IN_MILLIS = 60 * 1000;
	public static long ONE_HOUR_IN_MILLIS = 60 * 60 * 1000;
	public static long ONE_DAY_IN_MILLIS = 24 * 60 * 60 * 1000;
	
	private String id;
	private String threadId;
//	private String projectId;
	private Date creationDate;
	private UserSummary author;
	private String content;

	public enum ELAPSED_TIME_UNIT {
		NONE("nolabel"), SECONDS("elapsedTimeSeconds"), MINUTES("elapsedTimeMinutes"), HOURS("elapsedTimeHours");

		private ELAPSED_TIME_UNIT(String label) {
			this.label = label;
		}

		private final String label;

		public String getLabel() {
			return label;
		}

	}

	@Transient
	private String elapsedTimeSinceCreation;
	
	@Transient
	private ELAPSED_TIME_UNIT elapsedTimeUnit;

	@Transient
	private String authorProfilLink;

	@Transient
	private boolean userMayDelete;

	@Transient
	private boolean userMayMove;

	public ForumPost() {
		super();
	}

	public ForumPost(String threadId, /*String projectId, */ Date creationDate, UserSummary author, String content) {
		super();
		this.threadId = threadId;
//		this.projectId = projectId;
		this.creationDate = creationDate;
		this.author = author;
		this.content = content;
		getElapsedTimeSinceCreation();
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

//	public String getProjectId() {
//		return projectId;
//	}
//
//	public void setProjectId(String projectId) {
//		this.projectId = projectId;
//	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
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

	public boolean isUserMayMove() {
		return userMayMove;
	}

	public void setUserMayMove(boolean userMayMove) {
		this.userMayMove = userMayMove;
	}

	public String getElapsedTimeSinceCreation() {
		
		long elapsedMillis = Utils.countElapsedMillisSince(creationDate);
		
		if (elapsedMillis < ONE_MINUTE_IN_MILLIS) {
			elapsedTimeUnit = ELAPSED_TIME_UNIT.SECONDS;
			elapsedTimeSinceCreation = "";
		} else if (elapsedMillis < ONE_HOUR_IN_MILLIS) {
			elapsedTimeUnit = ELAPSED_TIME_UNIT.MINUTES;
			elapsedTimeSinceCreation = Integer.toString( (int) Math.floor (elapsedMillis / ONE_MINUTE_IN_MILLIS));
		} else if (elapsedMillis < ONE_HOUR_IN_MILLIS) {
			elapsedTimeUnit = ELAPSED_TIME_UNIT.HOURS;
			elapsedTimeSinceCreation = Integer.toString( (int) Math.floor (elapsedMillis / ONE_HOUR_IN_MILLIS));
		} else {
			elapsedTimeUnit = ELAPSED_TIME_UNIT.NONE;
			elapsedTimeSinceCreation = Utils.formatDate(creationDate);
		}
		
		return elapsedTimeSinceCreation;
	}

	public ELAPSED_TIME_UNIT getElapsedTimeUnit() {
		return elapsedTimeUnit;
	}

}
