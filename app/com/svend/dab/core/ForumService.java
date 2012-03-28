package com.svend.dab.core;

import java.util.Date;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import web.utils.Utils;

import com.google.common.collect.Sets;
import com.svend.dab.core.beans.ForumPost;
import com.svend.dab.core.beans.ForumThread;
import com.svend.dab.core.beans.profile.UserProfile;
import com.svend.dab.core.beans.profile.UserSummary;
import com.svend.dab.core.beans.projects.ForumDiff;
import com.svend.dab.core.dao.IForumPostDao;
import com.svend.dab.core.dao.IForumThreadDao;
import com.svend.dab.core.dao.IUserProfileDao;

@Service
public class ForumService implements IForumService {

	private static Logger logger = Logger.getLogger(ForumService.class.getName());
	
	@Autowired
	private IForumPostDao forumPostDao;

	@Autowired
	private IForumThreadDao forumThreadDao;

	@Autowired
	private IUserProfileDao userProfileRepo;

	// ///////////////////////////////////////////
	// project forum

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.svend.dab.core.projects.IProjectService#postNewForumMessage(java.lang.String, com.svend.dab.core.beans.ForumThread, java.lang.String)
	 */
	public void postNewForumMessage(String authorId, ForumThread thread, String messageContent) {

		UserProfile author = userProfileRepo.retrieveUserProfileById(authorId);
		if (author == null) {
			logger.log(Level.WARNING, "User with id " + authorId
					+ " is trying to post a message but has not registered profile! This is impossible! Not doing anything");
		} else {
			ForumPost createdPost = new ForumPost(thread.getId(), /* thread.getProjectId(), */new Date(), new UserSummary(author), messageContent);
			forumPostDao.saveNewPost(createdPost);
			updateNumberOfPostsOfThread(thread.getId());
		}
	}

	public ForumDiff computeThreadDiff(String threadId, Set<String> knownPostIds) {

		ForumDiff response = new ForumDiff();

		// removed threads
		Set<String> allPostIds = forumPostDao.findAllPostIdsOfThread(threadId);
		response.setDeletedPostIds(Sets.difference(knownPostIds, allPostIds).immutableCopy());

		// new threads
		response.setNewPosts(forumPostDao.findThreadPostsExcluding(threadId, knownPostIds));

		return response;
	}

	public void removePost(String threadId, String postId) {

		// TODO: two upates here => should send an event...
		forumPostDao.deletePost(postId);
		forumThreadDao.updateNumberOfPosts(threadId, forumPostDao.countPostOfThread(threadId));

	}

	public void movePostToThread(String originalThreadId, String postId, String targetThreadId, String username) {

		ForumPost post = forumPostDao.loadPost(postId);

		if (post != null && post.getThreadId().equals(originalThreadId)) {
			// TODO: two upates here => should send an event...

			StringBuffer updatedContent = new StringBuffer();
			updatedContent.append("===============\n");
			updatedContent.append("Forwarded by: ").append(username).append("\n");
			updatedContent.append("Original date: ").append(Utils.formatDate(post.getCreationDate())).append("\n");
			updatedContent.append("Original message:\n\n ").append(post.getContent());

			forumPostDao.updateThreadIdOfPost(postId, originalThreadId, targetThreadId, new Date(), updatedContent.toString());
			updateNumberOfPostsOfThread(originalThreadId);
			updateNumberOfPostsOfThread(targetThreadId);
		}

	}

	// ////////////////////////

	protected void updateNumberOfPostsOfThread(String threadId) {
		forumThreadDao.updateNumberOfPosts(threadId, forumPostDao.countPostOfThread(threadId));
	}

}
