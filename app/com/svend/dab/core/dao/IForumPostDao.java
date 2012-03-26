package com.svend.dab.core.dao;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.svend.dab.core.beans.ForumPost;
import com.svend.dab.core.beans.profile.UserSummary;

public interface IForumPostDao {

	public ForumPost loadPost(String postId);
	
	public List<ForumPost> getAllPosts(String threadId);

	public void saveNewPost(ForumPost createdPost);

	public Set<String> findAllPostIdsOfThread(String threadId);

	public List<ForumPost> findThreadPostsExcluding(String threadId, Set<String> excludedPostIds);

	public void updateAuthorOfAllPostsFrom(UserSummary updatedSummary);

	public void deletePost(String postId);

	public void deletePostsOfThread(String threadId);

	public Long countPostOfThread(String id);

	public void updateThreadIdOfPost(String postId, String originalThreadId, String targetThreadId, Date updatedCreationDate, String updatedContent);



}
