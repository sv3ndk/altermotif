package com.svend.dab.core.dao;

import java.util.List;
import java.util.Set;

import com.svend.dab.core.beans.profile.UserSummary;
import com.svend.dab.core.beans.projects.ForumPost;

public interface IForumPostDao {

	public List<ForumPost> getAllPosts(String threadId);

	public void saveNewPost(ForumPost createdPost);

	public Set<String> findAllPostIdsOfThread(String threadId);

	public List<ForumPost> findThreadPostsExcluding(String threadId, Set<String> excludedPostIds);

	public void updateAuthorOfAllPostsFrom(UserSummary updatedSummary);

	public void deletePost(String postId);

	public void deletePostsOfThread(String threadId);

	public Long countPostOfThread(String id);


}
