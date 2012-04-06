package controllers.forum;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import models.altemotif.groups.GroupForumThreadVisibility;
import models.altermotif.MappedValue;
import models.altermotif.projects.ProjectForumThreadVisibility;
import play.mvc.Router;
import web.utils.Utils;

import com.svend.dab.core.beans.ForumPost;
import com.svend.dab.core.beans.ForumThread;
import com.svend.dab.core.beans.ForumThreadPermission;
import com.svend.dab.core.beans.groups.GroupPep;
import com.svend.dab.core.beans.groups.ProjectGroup;
import com.svend.dab.core.beans.projects.ForumDiff;
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
public class ForumThreadView extends DabController {

	private static Logger logger = Logger.getLogger(ForumThreadView.class.getName());

	/**
	 * called when the browser send a GET visualize the HTML content of one forum thread
	 * 
	 * @param threadId
	 */

	// TODO: remve lot's of copy/paste between this and groupForumThread
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
						renderArgs.put("forumThreadVisibility", new ProjectForumThreadVisibility(getSessionWrapper().getLoggedInUserProfileId(), visitedThread,
								pep));

						List<ForumThread> allThreads = BeanProvider.getForumThreadDao().loadProjectForumThreads(visitedThread.getProjectId());
						List<ForumThread> allOtherThread = new LinkedList<ForumThread>();
						for (ForumThread thr : allThreads) {
							if (!thr.getId().equals(visitedThread.getId())) {
								allOtherThread.add(thr);
							}
						}
						renderArgs.put("allOtherThread", allOtherThread);
						render("forum/ForumThreadView/forumThread.html");
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
	 * called when the browser send a GET visualize the HTML content of one forum thread
	 * 
	 * @param threadId
	 */
	public static void groupForumThread(String groupId, String threadId) {

		ForumThread visitedThread = BeanProvider.getForumThreadDao().getThreadById(threadId);

		if (visitedThread != null) {
			ProjectGroup group = BeanProvider.getGroupService().loadGroupById(groupId, false);

			if (group != null && group.getId().equals(visitedThread.getGroupid())) {

				GroupPep pep = new GroupPep(group);
				if (pep.isAllowedToSeeThisForumThread(getSessionWrapper().getLoggedInUserProfileId(), visitedThread.isThreadPublic())) {

					List<ForumPost> allPosts = BeanProvider.getForumPostDao().getAllPosts(visitedThread.getId());
					Date expirationdate = new Date();
					expirationdate.setTime(expirationdate.getTime() + BeanProvider.getConfig().getPhotoExpirationDelayInMillis());
					for (ForumPost post : allPosts) {
						post.generatePhotoLink(expirationdate);
					}

					renderArgs.put("thread", visitedThread);
					renderArgs.put("threadPosts", allPosts);
					renderArgs.put("group", group);
					renderArgs.put("forumThreadVisibility", new GroupForumThreadVisibility(getSessionWrapper().getLoggedInUserProfileId(), visitedThread, pep));

					List<ForumThread> allThreads = BeanProvider.getForumThreadDao().loadGroupForumThreads(visitedThread.getGroupid());
					List<ForumThread> allOtherThread = new LinkedList<ForumThread>();
					for (ForumThread thr : allThreads) {
						if (!thr.getId().equals(visitedThread.getId())) {
							allOtherThread.add(thr);
						}
					}
					renderArgs.put("allOtherThread", allOtherThread);
					render("forum/ForumThreadView/forumThread.html");
				} else {
					// user is not allowed to see this thread
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
			if (thread.getProjectId() != null) {
				postNewCommentToProjectForum(postContent, thread);
			} else if (thread.getGroupid() != null) {
				postNewCommentToGroupForum(postContent, thread);
			} else {
				logger.log(Level.WARNING, "User is trying to post a message but th treahd has neither a group nor a project!. ThreadId:" + threadId + ",user: "
						+ getSessionWrapper().getLoggedInUserProfileId());
				renderJSON(new MappedValue("result", "nok"));
			}

		} else {
			logger.log(Level.WARNING, "User is trying to post a message to a non existing thread. ThreadId:" + threadId + ",user: "
					+ getSessionWrapper().getLoggedInUserProfileId());
			renderJSON(new MappedValue("result", "nok"));
		}
	}

	protected static void postNewCommentToGroupForum(String postContent, ForumThread thread) {
		ProjectGroup group = BeanProvider.getGroupService().loadGroupById(thread.getGroupid(), false);
		if (group != null) {
			if (new GroupPep(group).isAllowedToPostNewMessage(getSessionWrapper().getLoggedInUserProfileId(), thread)) {
				BeanProvider.getForumService().postNewForumMessage(getSessionWrapper().getLoggedInUserProfileId(), thread, postContent);
				renderJSON(new MappedValue("result", "ok"));
			} else {
				logger.log(Level.WARNING, "User is trying to post a message to a thread but is not allowed to! ThreadId:" + thread.getId() + "projectId:"
						+ thread.getProjectId() + ",user: " + getSessionWrapper().getLoggedInUserProfileId());
			}
		} else {
			logger.log(Level.WARNING,
					"User is trying to post a message to a thread linked to a non existing projet! This is impossible! ThreadId:" + thread.getId()
							+ "projectId:" + thread.getProjectId() + ",user: " + getSessionWrapper().getLoggedInUserProfileId());
			renderJSON(new MappedValue("result", "nok"));
		}
	}

	protected static void postNewCommentToProjectForum(String postContent, ForumThread thread) {
		Project project = BeanProvider.getProjectService().loadProject(thread.getProjectId(), false);
		if (project != null) {
			if (new ProjectPep(project).isAllowedToPostNewMessage(getSessionWrapper().getLoggedInUserProfileId(), thread)) {
				BeanProvider.getForumService().postNewForumMessage(getSessionWrapper().getLoggedInUserProfileId(), thread, postContent);
				renderJSON(new MappedValue("result", "ok"));
			} else {
				logger.log(Level.WARNING, "User is trying to post a message to a thread but is not allowed to! ThreadId:" + thread.getId() + "projectId:"
						+ thread.getProjectId() + ",user: " + getSessionWrapper().getLoggedInUserProfileId());
			}
		} else {
			logger.log(Level.WARNING,
					"User is trying to post a message to a thread linked to a non existing projet! This is impossible! ThreadId:" + thread.getId()
							+ "projectId:" + thread.getProjectId() + ",user: " + getSessionWrapper().getLoggedInUserProfileId());
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

			ForumThreadPermission permisions = computeThreadPermissions(thread);

			if (permisions.isAllowedSeeThisForumThread()) {
				Set<String> knownPostIds = Utils.jsonToSetOfStrings(currentlyKnownIds);
				ForumDiff diff = BeanProvider.getForumService().computeThreadDiff(thread.getId(), knownPostIds);

				Date expirationdate = new Date();
				expirationdate.setTime(expirationdate.getTime() + BeanProvider.getConfig().getCvExpirationDelayInMillis());

				for (ForumPost post : diff.getNewPosts()) {
					Map<String, Object> linkParams = new HashMap<String, Object>();
					linkParams.put("vuser", post.getAuthor().getUserName());
					post.setAuthorProfilLink(Router.reverse("profile.ProfileView.profileView", linkParams).url);
					post.generatePhotoLink(expirationdate);
					post.getElapsedTimeSinceCreation();
					post.setUserMayDelete(permisions.isAllowedToDeleteForumPosts());
					post.setUserMayMove(permisions.isAllowedToMoveForumPosts());
				}

				renderJSON(diff);
			} else {
				logger.log(Level.WARNING, "User is trying to post a message to a thread but he is not allowed to! ThreadId:" + thread.getId() + "projectId:"
						+ thread.getProjectId() + ",user: " + getSessionWrapper().getLoggedInUserProfileId());

				renderJSON(new MappedValue("result", "nok"));
			}

			renderJSON(new MappedValue("result", "nok"));
		}
	}

	protected static ForumThreadPermission computeThreadPermissions(ForumThread thread) {

		if (thread != null)
			if (thread.getGroupid() != null) {
				ProjectGroup group = BeanProvider.getGroupService().loadGroupById(thread.getGroupid(), false);
				if (group != null) {
					GroupPep pep = new GroupPep(group);
					String userid = getSessionWrapper().getLoggedInUserProfileId();
					return new ForumThreadPermission(pep.isAllowedToSeeThisForumThread(userid, thread.isThreadPublic()), pep.isAllowedToDeleteForumPosts(
							userid, thread), pep.isAllowedToMoveForumPosts(userid, thread));
				}

			} else if (thread.getProjectId() != null) {
				Project project = BeanProvider.getProjectService().loadProject(thread.getProjectId(), false);
				if (project != null) {
					ProjectPep pep = new ProjectPep(project);
					String userid = getSessionWrapper().getLoggedInUserProfileId();
					return new ForumThreadPermission(pep.isAllowedSeeThisForumThread(userid, thread.isThreadPublic()), pep.isAllowedToDeleteForumPosts(userid,
							thread), pep.isAllowedToMoveForumPosts(userid, thread));
				}
			}

		return new ForumThreadPermission(false, false, false);
	}

	// ///////////////////////////
	// Delete

	public static void deletePost(String threadId, String postId) {
		ForumThread thread = BeanProvider.getForumThreadDao().getThreadById(threadId);

		if (thread != null) {
			if (thread.getProjectId() != null) {
				deletePostCommentFromProjectForum(thread, postId);
			} else if (thread.getGroupid() != null) {
				deletePostCommentFromGroupForum(thread, postId);
			} else {
				logger.log(Level.WARNING, "User is trying to remove a message but the treahd has neither a group nor a project!. ThreadId:" + threadId
						+ ",user: " + getSessionWrapper().getLoggedInUserProfileId());
				renderJSON(new MappedValue("result", "nok"));
			}

		} else {
			renderJSON(new MappedValue("result", "nok"));
		}
	}

	private static void deletePostCommentFromGroupForum(ForumThread thread, String postId) {
		ProjectGroup group = BeanProvider.getGroupService().loadGroupById(thread.getGroupid(), false);
		if (group != null) {
			if (new GroupPep(group).isAllowedToDeleteForumPosts(getSessionWrapper().getLoggedInUserProfileId(), thread)) {
				BeanProvider.getForumService().removePost(thread.getId(), postId);
			} else {
				renderJSON(new MappedValue("result", "nok"));
			}
		} else {
			renderJSON(new MappedValue("result", "nok"));
		}
	}

	private static void deletePostCommentFromProjectForum(ForumThread thread, String postId) {
		Project project = BeanProvider.getProjectService().loadProject(thread.getProjectId(), false);
		if (project != null) {
			ProjectPep pep = new ProjectPep(project);
			if (pep.isAllowedToDeleteForumPosts(getSessionWrapper().getLoggedInUserProfileId(), thread)) {
				BeanProvider.getForumService().removePost(thread.getId(), postId);
			} else {
				renderJSON(new MappedValue("result", "nok"));
			}
		} else {
			renderJSON(new MappedValue("result", "nok"));
		}
	}

	// //////////////////////////
	// Move

	public static void movePost(String originalThreadId, String targetThreadId, String postId) {
		ForumThread originalThread = BeanProvider.getForumThreadDao().getThreadById(originalThreadId);
		if (originalThread != null) {
			if (originalThread.getProjectId() != null) {
				movePostCommentFromProjectForum(originalThread, targetThreadId, postId);
			} else if (originalThread.getGroupid() != null) {
				movePostCommentFromGroupForum(originalThread, targetThreadId, postId);
			} else {
				renderJSON(new MappedValue("result", "nok"));
			}
		} else {
			renderJSON(new MappedValue("result", "nok"));
		}
	}

	private static void movePostCommentFromGroupForum(ForumThread originalThread, String targetThreadId, String postId) {
		ProjectGroup group = BeanProvider.getGroupService().loadGroupById(originalThread.getGroupid(), false);
		if (group != null) {
			if (new GroupPep(group).isAllowedToMoveForumPosts(getSessionWrapper().getLoggedInUserProfileId(), originalThread)) {
				BeanProvider.getForumService().movePostToThread(originalThread.getId(), postId, targetThreadId, getSessionWrapper().getLoggedInUserProfileId());
			} else {
				renderJSON(new MappedValue("result", "nok"));
			}
		} else {
			renderJSON(new MappedValue("result", "nok"));
		}

	}

	private static void movePostCommentFromProjectForum(ForumThread originalThread, String targetThreadId, String postId) {
		Project project = BeanProvider.getProjectService().loadProject(originalThread.getProjectId(), false);
		if (project != null) {
			ProjectPep pep = new ProjectPep(project);
			if (pep.isAllowedToMoveForumPosts(getSessionWrapper().getLoggedInUserProfileId(), originalThread)) {
				BeanProvider.getForumService().movePostToThread(originalThread.getId(), postId, targetThreadId, getSessionWrapper().getLoggedInUserProfileId());
			} else {
				renderJSON(new MappedValue("result", "nok"));
			}
		}
	}

}
