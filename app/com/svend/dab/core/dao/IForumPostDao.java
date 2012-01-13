package com.svend.dab.core.dao;

import java.util.List;

import com.svend.dab.core.beans.projects.ForumPost;
import com.svend.dab.core.beans.projects.ForumThread;

public interface IForumPostDao {

	public List<ForumPost> getPosts(String threadId);


}
