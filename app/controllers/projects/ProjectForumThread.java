package controllers.projects;

import java.util.Date;
import java.util.HashMap;
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
	 * @param t
	 */
	public static void projectForumThread(String t) {

		ForumThread thread = BeanProvider.getForumThreadDao().getThreadById(t);

		if (thread != null) {
			Project project = BeanProvider.getProjectService().loadProject(thread.getProjectId(), false);

			if (project != null) {
				ProjectPep pep = new ProjectPep(project);
				if (pep.isAllowedSeeThisForumThread(getSessionWrapper().getLoggedInUserProfileId(), thread.isThreadPublic())) {

					List<ForumPost> allPosts = BeanProvider.getForumPostDao().getAllPosts(thread.getId());
					Date expirationdate = new Date();
					expirationdate.setTime(expirationdate.getTime() + BeanProvider.getConfig().getCvExpirationDelayInMillis());
					for (ForumPost post : allPosts) {
						post.generatePhotoLink(expirationdate);
					}

					renderArgs.put("thread", thread);
					renderArgs.put("threadPosts", allPosts);
					renderArgs.put("project", project);
					renderArgs.put("forumThreadVisibility", new ProjectForumThreadVisibility(getSessionWrapper().getLoggedInUserProfileId(), thread, pep));
					render();
				} else {
					Application.index();
				}
			} else {
				Application.index();
			}
		} else {
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
					logger.log(Level.WARNING, "User is trying to post a message to a thread but is not allowed to!. ThreadId:" + threadId + "projectId:" + thread.getProjectId() + ",user: "
							+ getSessionWrapper().getLoggedInUserProfileId());
				}
			} else {
				logger.log(Level.WARNING,
						"User is trying to post a message to a thread linked to a non existing projet! This is impossible!. ThreadId:" + threadId + "projectId:" + thread.getProjectId() + ",user: "
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
						post.getCreationDateStr();
						post.setUserMayDelete(pep.isAllowedToDeleteForumPosts(getSessionWrapper().getLoggedInUserProfileId(), thread));
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

}
