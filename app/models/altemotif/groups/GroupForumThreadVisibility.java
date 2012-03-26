package models.altemotif.groups;

import com.svend.dab.core.beans.ForumThread;
import com.svend.dab.core.beans.groups.GroupPep;
import com.svend.dab.core.beans.projects.ProjectPep;

import controllers.BeanProvider;

public class GroupForumThreadVisibility {

	// this may be null (if the user is not logged in)
	private final String visitingUserId;

	private final ForumThread thread;
	private final GroupPep pep;
	
	private volatile Long numberOfThreadOfThisGroup;
	
	
	//////////////////////////////
	//

	public GroupForumThreadVisibility(String visitingUserId, ForumThread thread, GroupPep pep) {
		super();
		this.visitingUserId = visitingUserId;
		this.thread = thread;
		this.pep = pep;
	}

	public boolean isPostNewThreadLinkVisible() {
		return pep.isAllowedToPostNewMessage(visitingUserId, thread);
	}
	
	public boolean isDeletePostLinkVisible() {
		return pep.isAllowedToDeleteForumPosts(visitingUserId, thread);
	}

	public boolean isMovePostLinkVisible() {
		return pep.isAllowedToMoveForumPosts(visitingUserId, thread) && getNumberOfThreadOfThisGroup() > 1;
	}

	protected Long getNumberOfThreadOfThisGroup() {
		if (numberOfThreadOfThisGroup == null) {
			synchronized(this) {
				if (numberOfThreadOfThisGroup == null) {
					numberOfThreadOfThisGroup = BeanProvider.getForumThreadDao().countThreadsOfGroup(thread.getGroupid());
				}
			}
		}
		return numberOfThreadOfThisGroup;
	}
	
}
