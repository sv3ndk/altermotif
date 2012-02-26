package controllers.projects;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import models.altermotif.MappedValue;
import models.altermotif.projects.ProjectForumThreadVisibility;
import play.mvc.Router;
import web.utils.Utils;

import com.svend.dab.core.beans.projects.ForumDiff;
import com.svend.dab.core.beans.projects.ForumPost;
import com.svend.dab.core.beans.projects.ForumThread;
import com.svend.dab.core.beans.projects.Project;
import com.svend.dab.core.beans.projects.Project.STATUS;
import com.svend.dab.core.beans.projects.ProjectPep;

import controllers.Application;
import controllers.BeanProvider;
import controllers.DabController;

/**
 * @author svend
 * 
 */
public class ProjectForumThread extends DabController {

	private static Logger logger = Logger.getLogger(ProjectForumThread.class.getName());

	/**
	 * called when the browser send a GET visualize the HTML content of one forum thread
	 * 
	 * @param threadId
	 */
	public static void projectForumThread(String projectId, String threadId) {

		ForumThread visitedThread = BeanProvider.getForumThreadDao().getThreadById(threadId);

		if (visitedThread != null) {
			Project project = BeanProvider.getProjectService().loadProject(visitedThread.getProjectId(), false);
			if (project != null && project.getStatus() != STATUS.cancelled) {
				if (project.getId().equals(projectId) && project.getId().equals(visitedThread.getProjectId())) {

					ProjectPep pep = new ProjectPep(project);
					if (pep.isAllowedSeeThisForumThread(getSessionWrapper().getLoggedInUserProfileId(), visitedThread.isThreadPublic())) {

						List<ForumPost> allPosts = BeanProvider.getForumPostDao().getAllPosts(visitedThread.getId());
						Date expirationdate = new Date();
						expirationdate.setTime(expirationdate.getTime() + BeanProvider.getConfig().getPhotoExpirationDelayInMillis());
						for (ForumPost post : allPosts) {
							post.generatePhotoLink(expirationdate);
						}

						renderArgs.put("thread", visitedThread);
						renderArgs.put("threadPosts", allPosts);
						renderArgs.put("project", project);
						renderArgs.put("forumThreadVisibility", new ProjectForumThreadVisibility(getSessionWrapper().getLoggedInUserProfileId(), visitedThread, pep));

						List<ForumThread> allThreads = BeanProvider.getForumThreadDao().loadProjectForumThreads(visitedThread.getProjectId());
						List<ForumThread> allOtherThread = new LinkedList<ForumThread>();
						for (ForumThread thr : allThreads) {
							if (!thr.getId().equals(visitedThread.getId())) {
								allOtherThread.add(thr);
							}
						}
						renderArgs.put("allOtherThread", allOtherThread);

						render();
					} else {
						// user is not allowed to see this thread
						Application.index();
					}
				} else {
					// project ID mismatch
					Application.index();
				}
			} else {
				// thread of a non existing project or a cancelled project
				Application.index();
			}
		} else {
			// no thread found for this threadid
			Application.index();
		}
	}

	/**
	 * @param threadId
	 * @param postContent
	 */
	public static void postNewComment(String threadId, String postContent) {

		ForumThread thread = BeanProvider.getForumThreadDao().getThreadById(threadId);

		if (thread != null) {
			Project project = BeanProvider.getProjectService().loadProject(thread.getProjectId(), false);
			if (project != null) {
				ProjectPep pep = new ProjectPep(project);
				if (pep.isAllowedToPostNewMessage(getSessionWrapper().getLoggedInUserProfileId(), thread)) {
					BeanProvider.getProjectService().postNewForumMessage(getSessionWrapper().getLoggedInUserProfileId(), thread, postContent);
					renderJSON(new MappedValue("result", "ok"));
				} else {
					logger.log(Level.WARNING, "User is trying to post a message to a thread but is not allowed to! ThreadId:" + threadId + "projectId:" + thread.getProjectId() + ",user: "
							+ getSessionWrapper().getLoggedInUserProfileId());
				}
			} else {
				logger.log(Level.WARNING,
						"User is trying to post a message to a thread linked to a non existing projet! This is impossible! ThreadId:" + threadId + "projectId:" + thread.getProjectId() + ",user: "
								+ getSessionWrapper().getLoggedInUserProfileId());
				renderJSON(new MappedValue("result", "nok"));
			}
		} else {
			logger.log(Level.WARNING, "User is trying to post a message to a non existing thread. ThreadId:" + threadId + ",user: " + getSessionWrapper().getLoggedInUserProfileId());
			renderJSON(new MappedValue("result", "nok"));
		}
	}

	/**
	 * @param threadId
	 * @param currentlyKnownIds
	 */
	public static void getNewAndDeletedPosts(String threadId, String currentlyKnownIds) {

		ForumThread thread = BeanProvider.getForumThreadDao().getThreadById(threadId);

		if (thread != null) {
			Project project = BeanProvider.getProjectService().loadProject(thread.getProjectId(), false);
			if (project != null) {
				ProjectPep pep = new ProjectPep(project);
				if (pep.isAllowedSeeThisForumThread(getSessionWrapper().getLoggedInUserProfileId(), thread.isThreadPublic())) {
					Set<String> knownPostIds = Utils.jsonToSetOfStrings(currentlyKnownIds);
					ForumDiff diff = BeanProvider.getProjectService().computeThreadDiff(thread.getId(), knownPostIds);

					Date expirationdate = new Date();
					expirationdate.setTime(expirationdate.getTime() + BeanProvider.getConfig().getCvExpirationDelayInMillis());

					for (ForumPost post : diff.getNewPosts()) {
						Map<String, Object> linkParams = new HashMap<String, Object>();
						linkParams.put("vuser", post.getAuthor().getUserName());
						post.setAuthorProfilLink(Router.reverse("profile.ProfileView.profileView", linkParams).url);
						post.generatePhotoLink(expirationdate);
						post.getElapsedTimeSinceCreation();
						post.setUserMayDelete(pep.isAllowedToDeleteForumPosts(getSessionWrapper().getLoggedInUserProfileId(), thread));
						post.setUserMayMove(pep.isAllowedToMoveForumPosts(getSessionWrapper().getLoggedInUserProfileId(), thread));
					}

					renderJSON(diff);
				} else {
					renderJSON(new MappedValue("result", "nok"));
				}
			} else {
				renderJSON(new MappedValue("result", "nok"));
			}
		} else {
			renderJSON(new MappedValue("result", "nok"));
		}
	}

	public static void deletePost(String threadId, String postId) {
		ForumThread thread = BeanProvider.getForumThreadDao().getThreadById(threadId);

		if (thread != null) {
			Project project = BeanProvider.getProjectService().loadProject(thread.getProjectId(), false);
			if (project != null) {
				ProjectPep pep = new ProjectPep(project);
				if (pep.isAllowedToDeleteForumPosts(getSessionWrapper().getLoggedInUserProfileId(), thread)) {
					// TODO: clean up: delegate to project service here
					BeanProvider.getForumPostDao().deletePost(postId);
					BeanProvider.getForumThreadDao().updateNumberOfPosts(thread.getId(), BeanProvider.getForumPostDao().countPostOfThread(thread.getId()));
				} else {
					renderJSON(new MappedValue("result", "nok"));
				}
			} else {
				renderJSON(new MappedValue("result", "nok"));
			}
		} else {
			renderJSON(new MappedValue("result", "nok"));
		}
	}

	public static void movePost(String originalThreadId, String targetThreadId, String postId) {

		ForumThread originalThread = BeanProvider.getForumThreadDao().getThreadById(originalThreadId);

		if (originalThread != null) {
			Project project = BeanProvider.getProjectService().loadProject(originalThread.getProjectId(), false);
			if (project != null) {
				ProjectPep pep = new ProjectPep(project);
				if (pep.isAllowedToMoveForumPosts(getSessionWrapper().getLoggedInUserProfileId(), originalThread)) {
					BeanProvider.getProjectService().movePostToThread(originalThreadId, postId, targetThreadId, getSessionWrapper().getLoggedInUserProfileId());
				} else {
					renderJSON(new MappedValue("result", "nok"));
				}
			} else {
				renderJSON(new MappedValue("result", "nok"));
			}
		} else {
			renderJSON(new MappedValue("result", "nok"));
		}

	}

}
