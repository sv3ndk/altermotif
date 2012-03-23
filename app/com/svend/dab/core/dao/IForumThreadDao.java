package com.svend.dab.core.dao;

import java.util.List;

import com.svend.dab.core.beans.projects.ForumThread;

public interface IForumThreadDao {

	public List<ForumThread> loadProjectForumThreads(String projectId);
	public List<ForumThread> loadGroupForumThreads(String groupid);

	public ForumThread createNewThread(ForumThread forumThread);

	public void updateThreadVisibility(String threadId, boolean isThreadPublic);

	public ForumThread getThreadById(String threadId);

	public void deleteThread(String threadId);

	public void updateNumberOfPosts(String id, Long countPostOfThread);

	public Long countThreadsOfProject(String projectId);


}
