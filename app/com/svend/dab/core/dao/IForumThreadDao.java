package com.svend.dab.core.dao;

import java.util.List;

import com.svend.dab.core.beans.projects.ForumThread;

public interface IForumThreadDao {

   public List<ForumThread> loadProjectForumThreads(String projectId);

   public ForumThread createNewThread(ForumThread forumThread);

}
