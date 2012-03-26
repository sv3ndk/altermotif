package models.altermotif.projects;

import com.svend.dab.core.beans.ForumThread;
import com.svend.dab.core.beans.projects.ProjectPep;

import controllers.BeanProvider;

public class ProjectForumThreadVisibility {

	// this may be null (if the user is not logged in)
	private final String visitingUserId;

	private final ForumThread thread;
	private final ProjectPep pep;
	
	private volatile Long numberOfThreadOfThisProject;
	
	
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

	public boolean isMovePostLinkVisible() {
		
		// TODO: also check here that there are at least two thread in this project (otherwise moving does not make sense)
		
		
		return pep.isAllowedToMoveForumPosts(visitingUserId, thread) && getNumberOfThreadOfThisProject() > 1;
	}

	protected Long getNumberOfThreadOfThisProject() {
		if (numberOfThreadOfThisProject == null) {
			synchronized(this) {
				if (numberOfThreadOfThisProject == null) {
					numberOfThreadOfThisProject = BeanProvider.getForumThreadDao().countThreadsOfProject(thread.getProjectId());
				}
			}
		}
		return numberOfThreadOfThisProject;
	}
	
}
