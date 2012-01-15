package models.altermotif.projects;

import com.svend.dab.core.beans.projects.ForumThread;
import com.svend.dab.core.beans.projects.ProjectPep;

public class ProjectForumThreadVisibility {

	// this may be null (if the user is not logged in)
	private final String visitingUserId;

	private final ForumThread thread;

	private final ProjectPep pep;
	
	//////////////////////////////
	//

	public ProjectForumThreadVisibility(String visitingUserId, ForumThread thread, ProjectPep pep) {
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

}
