package com.svend.dab.core;

import java.util.Set;

import com.svend.dab.core.beans.ForumThread;
import com.svend.dab.core.beans.projects.ForumDiff;

public interface IForumService {

	public void postNewForumMessage(String authorId, ForumThread thread, String messageContent);

	public ForumDiff computeThreadDiff(String threadId, Set<String> knownPostIds);

	public abstract void removePost(String threadId, String postId);

	public void movePostToThread(String originalThreadId, String postId, String targetThreadId, String username);

}
