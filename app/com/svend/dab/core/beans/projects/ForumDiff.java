package com.svend.dab.core.beans.projects;

import java.util.List;
import java.util.Set;

/**
 * 
 * This bean represents a difference, expressed in {@link ForumPost}, between two states of the forum.
 * 
 * This is used to get send this diff to the browser when it needs to updates its diplayedList
 * 
 * @author svend
 *
 */
public class ForumDiff {
	
	List<ForumPost> newPosts;
	
	Set<String> deletedPostIds;

	public List<ForumPost> getNewPosts() {
		return newPosts;
	}

	public void setNewPosts(List<ForumPost> newPosts) {
		this.newPosts = newPosts;
	}

	public Set<String> getDeletedPostIds() {
		return deletedPostIds;
	}

	public void setDeletedPostIds(Set<String> deletedPostIds) {
		this.deletedPostIds = deletedPostIds;
	}
	
	

}
