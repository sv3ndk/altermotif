package com.svend.dab.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.svend.dab.core.dao.IForumPostDao;
import com.svend.dab.core.dao.IForumThreadDao;

@Service
public class ForumService implements IForumService {
	
	@Autowired
	private IForumPostDao forumPostDao;
	
	@Autowired
	private IForumThreadDao forumThreadDao;
	

	public void removePost(String threadId, String postId) {

		// TODO: two upates here => should send an event...
		forumPostDao.deletePost(postId);
		forumThreadDao.updateNumberOfPosts(threadId, forumPostDao.countPostOfThread(threadId));

	}

}
