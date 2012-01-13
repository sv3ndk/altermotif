package controllers.projects;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import models.altermotif.MappedValue;
import models.altermotif.projects.ProjectViewVisibility;
import web.utils.Utils;

import com.svend.dab.core.beans.projects.ForumPost;
import com.svend.dab.core.beans.projects.ForumThread;
import com.svend.dab.core.beans.projects.Participant;
import com.svend.dab.core.beans.projects.ParticipantList;
import com.svend.dab.core.beans.projects.ParticpantsIdList;
import com.svend.dab.core.beans.projects.Project;
import com.svend.dab.core.beans.projects.Project.STATUS;
import com.svend.dab.core.beans.projects.ProjectPep;

import controllers.Application;
import controllers.BeanProvider;
import controllers.DabController;
import controllers.profile.ProfileHome;

public class ProjectsView extends DabController {

	private static Logger logger = Logger.getLogger(ProjectsView.class.getName());

	public static void projectsView(String p) {
		Project project = BeanProvider.getProjectService().loadProject(p, true);
		if (project != null && project.getStatus() != STATUS.cancelled) {
			renderArgs.put("visitedProject", project);
			renderArgs.put("projectVisibility", new ProjectViewVisibility(new ProjectPep(project), project, getSessionWrapper().getLoggedInUserProfileId()));
			renderArgs.put("allThreads", BeanProvider.getProjectForumThreadDao().loadProjectForumThreads(p));
			Utils.addAllPossibleLanguageNamesToRenderArgs(getSessionWrapper(), renderArgs);
			render();
		} else {
			logger.log(Level.WARNING, "could not find project => redirecting to application home");
			Application.index();
		}
	}

	// ////////////////////////////////////////////////////////
	// project cancellation and termination

	public static void cancelProject(String projectId) {
		Project project = BeanProvider.getProjectService().loadProject(projectId, false);
		if (project != null) {
			ProjectPep pep = new ProjectPep(project);

			if (pep.isAllowedToCancelProject(getSessionWrapper().getLoggedInUserProfileId())) {
				BeanProvider.getProjectService().cancelProject(project);
			} else {
				logger.log(Level.WARNING, "user trying cancel a proejct but is not allowed to,  this should be impossible, userid is" + getSessionWrapper().getLoggedInUserProfileId()
						+ ", projectid is " + projectId);
				Application.index();
			}
		} else {
			logger.log(Level.WARNING, "user trying to cancel a non existant project : " + projectId + " this should be impossible!");
			Application.index();
		}

		try {
			// ugly wait: the cancellation is asynchronous => refreshing the page right away is usually too early: waiting 1 sec greatly increases the chances that the transfer has actually been done
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}

		ProfileHome.profileHome();
	}

	public static void terminateProject(String projectId) {
		Project project = BeanProvider.getProjectService().loadProject(projectId, false);
		if (project != null) {
			ProjectPep pep = new ProjectPep(project);

			if (pep.isAllowedToTerminate(getSessionWrapper().getLoggedInUserProfileId())) {
				BeanProvider.getProjectService().terminateProject(project);
			} else {
				logger.log(Level.WARNING, "user trying terminate a project but is not allowed to, this should be impossible, userid is" + getSessionWrapper().getLoggedInUserProfileId()
						+ ", projectid is " + projectId);
				Application.index();
			}
		} else {
			logger.log(Level.WARNING, "user trying to terminate a non existant project : " + projectId + " this should be impossible!");
			Application.index();
		}

		try {
			// ugly wait: the cancellation is asynchronous => refreshing the page right away is usually too early: waiting 1 sec greatly increases the chances that the transfer has actually been done
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		projectsView(projectId);
	}

	public static void restartProject(String projectId) {
		Project project = BeanProvider.getProjectService().loadProject(projectId, false);
		if (project != null) {
			ProjectPep pep = new ProjectPep(project);

			if (pep.isAllowedToRestartProject(getSessionWrapper().getLoggedInUserProfileId())) {
				BeanProvider.getProjectService().restartProject(project);
			} else {
				logger.log(Level.WARNING, "user trying restart a project but is not allowed to, this should be impossible, userid is" + getSessionWrapper().getLoggedInUserProfileId()
						+ ", projectid is " + projectId);
				Application.index();
			}
		} else {
			logger.log(Level.WARNING, "user trying to restart a non existant project : " + projectId + " this should be impossible!");
			Application.index();
		}

		try {
			// ugly wait: the cancellation is asynchronous => refreshing the page right away is usually too early: waiting 1 sec greatly increases the chances that the transfer has actually been done
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		projectsView(projectId);
	}

	// ///////////////////////////////////////
	// project applications

	/**
	 * @param projectId
	 */
	public static void doApplyToProject(String projectId, String applicationText) {
		if (!getSessionWrapper().isLoggedIn()) {
			logger.log(Level.WARNING, "non logged in user  trying to apply to project : " + projectId + " this should be impossible!");
			return;
		}

		Project project = BeanProvider.getProjectService().loadProject(projectId, false);
		if (project != null) {

			if (project.isUserApplying(getSessionWrapper().getLoggedInUserProfileId()) || project.isUserAlreadyMember(getSessionWrapper().getLoggedInUserProfileId())) {
				// not letting the user apply several times
			} else {
				BeanProvider.getProjectService().applyToProject(getSessionWrapper().getLoggedInUserProfileId(), applicationText, project);
			}

		} else {
			logger.log(Level.WARNING, "user trying to apply to a non existant project : " + projectId + " this should be impossible!");
		}
	}

	/**
	 * @param projectId
	 */
	public static void doCancelApplyToProject(String projectId) {
		if (!getSessionWrapper().isLoggedIn()) {
			logger.log(Level.WARNING, "non logged in user trying to cancel project application to project : " + projectId + " this should be impossible!");
			return;
		}

		Project project = BeanProvider.getProjectService().loadProject(projectId, false);
		if (project != null) {

			if (project.isUserApplying(getSessionWrapper().getLoggedInUserProfileId())) {
				BeanProvider.getProjectService().cancelApplication(getSessionWrapper().getLoggedInUserProfileId(), project);
			}

		} else {
			logger.log(Level.WARNING, "user trying to cancel application to a non existant project : " + projectId + " this should be impossible!");
		}

	}

	/**
	 * @param projectId
	 * @param applicant
	 */
	public static void doRejectApplicationToProject(String projectId, String applicant) {
		Project project = BeanProvider.getProjectService().loadProject(projectId, false);
		if (project != null) {
			ProjectPep pep = new ProjectPep(project);

			if (pep.isAllowedToAcceptOrRejectApplications(getSessionWrapper().getLoggedInUserProfileId())) {
				BeanProvider.getProjectService().cancelApplication(applicant, project);
			} else {
				logger.log(Level.WARNING, "user trying to reject application but is not allowed to,  this should be impossible, userid is" + getSessionWrapper().getLoggedInUserProfileId()
						+ ", projectid is " + projectId);
			}
		} else {
			logger.log(Level.WARNING, "user trying to reject application to a non existant project : " + projectId + " this should be impossible!");
		}

	}

	/**
	 * @param projectId
	 * @param applicant
	 */
	public static void doAcceptApplicationToProject(String projectId, String applicant) {

		Project project = BeanProvider.getProjectService().loadProject(projectId, false);
		if (project != null) {

			ProjectPep pep = new ProjectPep(project);

			if (pep.isAllowedToAcceptOrRejectApplications(getSessionWrapper().getLoggedInUserProfileId())) {
				BeanProvider.getProjectService().acceptApplication(applicant, project);
			} else {
				logger.log(Level.WARNING, "user trying to accept application but is not allowed to,  this should be impossible, userid is" + getSessionWrapper().getLoggedInUserProfileId()
						+ ", projectid is " + projectId);
			}
		} else {
			logger.log(Level.WARNING, "user trying to accept application to a non existant project : " + projectId + " this should be impossible!");
		}

		renderJSON(new MappedValue("truc", "much"));
	}

	// ------------------------------------------------
	// participants

	public static void doRemoveParticipantOfProject(String projectId, String participant) {

		Project project = BeanProvider.getProjectService().loadProject(projectId, false);
		if (project != null) {

			ProjectPep pep = new ProjectPep(project);

			if (pep.isAllowedToEjectParticipant(getSessionWrapper().getLoggedInUserProfileId(), participant)) {
				BeanProvider.getProjectService().removeParticipant(participant, project);
			} else {
				logger.log(Level.WARNING, "user trying to remove participant but is not allowed to,  this should be impossible, userid is" + getSessionWrapper().getLoggedInUserProfileId()
						+ ", projectid is " + projectId);
			}
		} else {
			logger.log(Level.WARNING, "user trying to remove participant to a non existant project : " + projectId + " this should be impossible!");
		}
		renderJSON(new MappedValue("truc", "much"));
	}

	/**
	 * @param projectId
	 */
	public static void doLeaveProject(String projectId) {

		Project project = BeanProvider.getProjectService().loadProject(projectId, false);
		if (project != null) {

			ProjectPep pep = new ProjectPep(project);

			if (pep.isAllowedToLeave(getSessionWrapper().getLoggedInUserProfileId())) {
				BeanProvider.getProjectService().removeParticipant(getSessionWrapper().getLoggedInUserProfileId(), project);
			} else {
				logger.log(Level.WARNING, "user trying to leave project but is not allowed to,  this should be impossible, userid is" + getSessionWrapper().getLoggedInUserProfileId()
						+ ", projectid is " + projectId);
			}
		} else {
			logger.log(Level.WARNING, "user trying to leave a non existant project : " + projectId + " this should be impossible!");
		}

		renderJSON(new MappedValue("truc", "much"));
	}

	public static void doMakeAdmin(String projectId, String participant) {
		Project project = BeanProvider.getProjectService().loadProject(projectId, false);
		if (project != null) {

			ProjectPep pep = new ProjectPep(project);

			if (pep.isAllowedToMakeAdmin(getSessionWrapper().getLoggedInUserProfileId(), participant)) {
				BeanProvider.getProjectService().makeAdmin(participant, project);
			} else {
				logger.log(Level.WARNING, "user trying to make participant admin but is not allowed to,  this should be impossible, userid is" + getSessionWrapper().getLoggedInUserProfileId()
						+ ", projectid is " + projectId);
			}
		} else {
			logger.log(Level.WARNING, "user trying to make participant admin of non existant project : " + projectId + " this should be impossible!");
		}
		renderJSON(new MappedValue("truc", "much"));
	}

	public static void doMakeMember(String projectId, String participant) {
		Project project = BeanProvider.getProjectService().loadProject(projectId, false);
		if (project != null) {

			ProjectPep pep = new ProjectPep(project);

			if (pep.isAllowedToMakeMember(getSessionWrapper().getLoggedInUserProfileId(), participant)) {
				BeanProvider.getProjectService().makeMember(participant, project);
			} else {
				logger.log(Level.WARNING, "user trying to make participant member but is not allowed to,  this should be impossible, userid is" + getSessionWrapper().getLoggedInUserProfileId()
						+ ", projectid is " + projectId);
			}
		} else {
			logger.log(Level.WARNING, "user trying to make participant member of non existant project : " + projectId + " this should be impossible!");
		}
		renderJSON(new MappedValue("truc", "much"));
	}

	public static void doGiveOwnership(String projectId, String participant) {
		Project project = BeanProvider.getProjectService().loadProject(projectId, false);
		if (project != null) {

			ProjectPep pep = new ProjectPep(project);

			if (pep.isAllowedToGiveOwnership(getSessionWrapper().getLoggedInUserProfileId(), participant)) {
				BeanProvider.getProjectService().proposeOwnerShip(participant, project);
			} else {
				logger.log(Level.WARNING, "user trying to give ownership to participant member but is not allowed to,  this should be impossible, userid is"
						+ getSessionWrapper().getLoggedInUserProfileId() + ", projectid is " + projectId);
			}
		} else {
			logger.log(Level.WARNING, "user trying to give ownership to participant member of non existant project : " + projectId + " this should be impossible!");
		}
		renderJSON(new MappedValue("truc", "much"));
	}

	public static void doCancelGiveOwnership(String projectId, String participant) {
		Project project = BeanProvider.getProjectService().loadProject(projectId, false);
		if (project != null) {
			ProjectPep pep = new ProjectPep(project);

			if (pep.isAllowedToCancelOwnershipTransfer(getSessionWrapper().getLoggedInUserProfileId(), participant)) {
				BeanProvider.getProjectService().cancelOwnershipTransfer(participant, project);
			} else {
				logger.log(Level.WARNING, "user trying cancel an ownership transfer to participant member but is not allowed to,  this should be impossible, userid is"
						+ getSessionWrapper().getLoggedInUserProfileId() + ", projectid is " + projectId);
			}
		} else {
			logger.log(Level.WARNING, "user trying to cancel an  ownership transfer to participant member of non existant project : " + projectId + " this should be impossible!");
		}
		renderJSON(new MappedValue("truc", "much"));
	}

	/**
	 * @param projectId
	 */
	public static void doRefuseOwnership(String projectId) {
		Project project = BeanProvider.getProjectService().loadProject(projectId, false);
		if (project != null) {
			ProjectPep pep = new ProjectPep(project);

			if (pep.isAllowedToAcceptOrRefuseOwnershipTransfer(getSessionWrapper().getLoggedInUserProfileId())) {
				BeanProvider.getProjectService().cancelOwnershipTransfer(getSessionWrapper().getLoggedInUserProfileId(), project);
			} else {
				logger.log(Level.WARNING, "user trying refuse an ownership transfer to participant member but is not allowed to,  this should be impossible, userid is"
						+ getSessionWrapper().getLoggedInUserProfileId() + ", projectid is " + projectId);
			}
		} else {
			logger.log(Level.WARNING, "user trying to refuse an  ownership transfer to participant member of non existant project : " + projectId + " this should be impossible!");
		}
		renderJSON(new MappedValue("truc", "much"));
	}

	/**
	 * @param projectId
	 */
	public static void acceptOwnership(String projectId) {
		Project project = BeanProvider.getProjectService().loadProject(projectId, false);
		if (project != null) {
			ProjectPep pep = new ProjectPep(project);
			if (pep.isAllowedToAcceptOrRefuseOwnershipTransfer(getSessionWrapper().getLoggedInUserProfileId())) {
				BeanProvider.getProjectService().confirmOwnershipTransfer(getSessionWrapper().getLoggedInUserProfileId(), project);
			} else {
				logger.log(Level.WARNING, "user trying accept an ownership transfer to participant member but is not allowed to,  this should be impossible, userid is"
						+ getSessionWrapper().getLoggedInUserProfileId() + ", projectid is " + projectId);
				Application.index();
			}
		} else {
			logger.log(Level.WARNING, "user trying to accept an ownership transfer to participant member of non existant project : " + projectId + " this should be impossible!");
			Application.index();
		}

		try {
			// ugly wait: the ownership transfer is asynchronous => refreshing the page right away is usually too early: waiting 1 sec greatly increases the chances that the transfer has actually been
			// done
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}

		projectsView(projectId);
	}

	// --------------------------------------------------------
	// async refresh logic

	public static void doDetermineRemovedParticipantsAndApplications(String projectId, String knownParticipantUsernames, String knownApplicationUsernames) {
		Set<String> knownParticipantUsernamesSet = Utils.jsonToSetOfStrings(knownParticipantUsernames);
		Set<String> knownApplicationUsernamesSet = Utils.jsonToSetOfStrings(knownApplicationUsernames);
		ParticpantsIdList removedParticipants = BeanProvider.getProjectService().determineRemovedParticipants(projectId, knownParticipantUsernamesSet, knownApplicationUsernamesSet);

		Project project = BeanProvider.getProjectService().loadProject(projectId, false);
		removedParticipants.setCancelProjectLinkEffective(new ProjectViewVisibility(new ProjectPep(project), project, getSessionWrapper().getLoggedInUserProfileId()).isCancelProjectLinkEffective());

		renderJSON(removedParticipants);
	}

	public static void doDetermineAddedParticipantsAndApplications(String projectId, String knownParticipantUsernames, String knownApplicationUsernames) {
		Set<String> knownParticipantUsernamesSet = Utils.jsonToSetOfStrings(knownParticipantUsernames);
		Set<String> knownApplicationUsernamesSet = Utils.jsonToSetOfStrings(knownApplicationUsernames);
		ParticipantList addedParticipants = BeanProvider.getProjectService().determineAddedParticipants(projectId, knownParticipantUsernamesSet, knownApplicationUsernamesSet);

		Project project = BeanProvider.getProjectService().loadProject(projectId, false);

		renderArgs.put("_projectVisibility", new ProjectViewVisibility(new ProjectPep(project), project, getSessionWrapper().getLoggedInUserProfileId()));
		renderArgs.put("_numberOfConfirmedParticipants", project.getNumberOfConfirmedParticipants());
		renderArgs.put("_numberOfApplications", project.getNumberOfApplications());

		renderArgs.put("_confirmedParticipants", addedParticipants.getConfirmedParticipants());
		renderArgs.put("_unconfirmedActiveParticipants", addedParticipants.getUnconfirmedParticipants());

		renderTemplate("tags/projects/viewProjectParticipantsAndApplications.html");
	}

	public static void doRetrieveParticipantContentData(String projectId, String participant) {
		Project project = BeanProvider.getProjectService().loadProject(projectId, false);
		if (project != null) {
			Participant participantData = project.getParticipant(participant);
			if (participantData != null) {
				renderArgs.put("_participant", participantData);
				renderArgs.put("_projectVisibility", new ProjectViewVisibility(new ProjectPep(project), project, getSessionWrapper().getLoggedInUserProfileId()));
				renderTemplate("tags/projects/participant.html");
			} else {

				logger.log(Level.WARNING, "user trying retrieve participant content data of for a particpant which does not belong to the proejct, project id is : " + projectId
						+ " this should be impossible!");
			}
		} else {
			logger.log(Level.WARNING, "user trying retrieve participant content data of non existant project : " + projectId + " this should be impossible!");
		}
	}

	// ///////////////////////////////////////
	// forum and thread

	/**
	 * @param projectId
	 * @param threadTitle
	 */
	public static void doAddThread(String projectId, String threadTitle, boolean isThreadPublic) {
		Project project = BeanProvider.getProjectService().loadProject(projectId, false);
		if (project != null) {
			
			if (project != null) {
				if (new ProjectPep(project).isAllowedAddForumThread(getSessionWrapper().getLoggedInUserProfileId())) {
					ForumThread createdThread = BeanProvider.getProjectService().createdNewForumThread(projectId, threadTitle, isThreadPublic);
					renderJSON(createdThread);
				} else {
					logger.log(Level.WARNING, "user trying to add a thread to a project but is not allowed to: projectId:" + projectId + "userid:"+getSessionWrapper().getLoggedInUserProfileId());
				}
			}
		} else {
			logger.log(Level.WARNING, "user trying to add a thread to a non existant project : " + projectId + " this should be impossible!");
		}
	}

	/**
	 * @param threadId
	 */
	public static void doRetrieveForumPosts(String threadId) {
		List<ForumPost> posts = BeanProvider.getForumPostDao().getPosts(threadId);
		renderJSON(posts);
	}

	public static void changeThreadVisibility(String projectId, String threadId, boolean isThreadPublic) {
		Project project = BeanProvider.getProjectService().loadProject(projectId, false);
		if (project != null) {
			if (new ProjectPep(project).isAllowedToUpdateThread(getSessionWrapper().getLoggedInUserProfileId())) {
				BeanProvider.getForumThreadDao().updateThreadVisibility(projectId, threadId, isThreadPublic);
				renderJSON(BeanProvider.getForumThreadDao().getThreadById(threadId));
			} else {
				logger.log(Level.WARNING, "user trying to change visibility of a thread but is not allowed to: projectId:" + projectId + ", threadId:" + threadId + "userid:"
						+ getSessionWrapper().getLoggedInUserProfileId());
			}
		} else {
			logger.log(Level.WARNING, "user trying to change visibility of a thread of a non existant project : " + projectId + " this should be impossible!");
		}
	}

	public static void deleteThread(String projectId, String threadId) {
		Project project = BeanProvider.getProjectService().loadProject(projectId, false);
		if (project != null) {
			if (new ProjectPep(project).isAllowedToUpdateThread(getSessionWrapper().getLoggedInUserProfileId())) {
				BeanProvider.getForumThreadDao().deleteThread(projectId, threadId);
				renderJSON(new MappedValue("removeThreadId", threadId));
			} else {
				logger.log(Level.WARNING, "user trying to delete a thread but is not allowed to: projectId:" + projectId + ", threadId:" + threadId + "userid:"
						+ getSessionWrapper().getLoggedInUserProfileId());
			}
		} else {
			logger.log(Level.WARNING, "user trying to delete a thread of a non existant project : " + projectId + " this should be impossible!");
		}
	}

}