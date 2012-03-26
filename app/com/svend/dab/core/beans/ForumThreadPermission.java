package com.svend.dab.core.beans;

public class ForumThreadPermission {

	private final boolean isAllowedSeeThisForumThread;
	private final boolean isAllowedToDeleteForumPosts;
	private final boolean isAllowedToMoveForumPosts;

	public ForumThreadPermission(boolean isAllowedSeeThisForumThread, boolean isAllowedToDeleteForumPosts, boolean isAllowedToMoveForumPosts) {
		super();
		this.isAllowedSeeThisForumThread = isAllowedSeeThisForumThread;
		this.isAllowedToDeleteForumPosts = isAllowedToDeleteForumPosts;
		this.isAllowedToMoveForumPosts = isAllowedToMoveForumPosts;
	}

	public boolean isAllowedSeeThisForumThread() {
		return isAllowedSeeThisForumThread;
	}

	public boolean isAllowedToDeleteForumPosts() {
		return isAllowedToDeleteForumPosts;
	}

	public boolean isAllowedToMoveForumPosts() {
		return isAllowedToMoveForumPosts;
	}

}
