package controllers.projects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import models.altermotif.MappedValue;
import models.altermotif.ResponseNode;
import models.altermotif.projects.ProjectVisibility;
import models.altermotif.projects.RemovedParticpantsResponse;
import web.utils.Utils;

import com.svend.dab.core.beans.projects.Participant;
import com.svend.dab.core.beans.projects.ParticipantList;
import com.svend.dab.core.beans.projects.Participation;
import com.svend.dab.core.beans.projects.Project;
import com.svend.dab.core.beans.projects.ProjectPep;
import com.svend.dab.core.beans.projects.ParticpantsIdList;

import controllers.Application;
import controllers.BeanProvider;
import controllers.DabController;

public class ProjectsView extends DabController {

	private static Logger logger = Logger.getLogger(ProjectsView.class.getName());

	public static void projectsView(String p) {

		Project project = BeanProvider.getProjectService().loadProject(p, true);
		if (project != null) {
			renderArgs.put("visitedProject", project);
			renderArgs.put("projectVisibility", new ProjectVisibility(new ProjectPep(project), project, getSessionWrapper().getLoggedInUserProfileId()));

			Utils.addAllPossibleLanguageNamesToRenderArgs(getSessionWrapper(), renderArgs);
			render();
		} else {
			logger.log(Level.WARNING, "could not find project => redirecting to application home");
			Application.index();
		}
	}

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
	

	// --------------------------------------------------------
	// async refresh logic

	public static void doDetermineRemovedParticipantsAndApplications(String projectId, String knownParticipantUsernames, String knownApplicationUsernames) {
		Set<String> knownParticipantUsernamesSet = Utils.jsonToSetOfStrings(knownParticipantUsernames);
		Set<String> knownApplicationUsernamesSet = Utils.jsonToSetOfStrings(knownApplicationUsernames);
		ParticpantsIdList removedParticipants = BeanProvider.getProjectService().determineRemovedParticipants(projectId, knownParticipantUsernamesSet, knownApplicationUsernamesSet);
		renderJSON(removedParticipants);
	}
	
	public static void doDetermineAddedParticipantsAndApplications(String projectId, String knownParticipantUsernames, String knownApplicationUsernames) {
		
		Set<String> knownParticipantUsernamesSet = Utils.jsonToSetOfStrings(knownParticipantUsernames);
		Set<String> knownApplicationUsernamesSet = Utils.jsonToSetOfStrings(knownApplicationUsernames);
		ParticipantList addedParticipants = BeanProvider.getProjectService().determineAddedParticipants(projectId, knownParticipantUsernamesSet, knownApplicationUsernamesSet);
		
		Project project = BeanProvider.getProjectService().loadProject(projectId, false);
		
		renderArgs.put("_projectVisibility", new ProjectVisibility(new ProjectPep(project), project, getSessionWrapper().getLoggedInUserProfileId()));
		renderArgs.put("_numberOfConfirmedParticipants", project.getNumberOfConfirmedParticipants());
		renderArgs.put("_numberOfApplications", project.getNumberOfApplications());
		
		renderArgs.put("_confirmedParticipants", addedParticipants.getConfirmedParticipants());
		renderArgs.put("_unconfirmedActiveParticipants", addedParticipants.getUnconfirmedParticipants());
		
		renderTemplate("tags/projects/viewProjectParticipantsAndApplications.html");
	}
	
	
	
	public static void doRetrieveParticipantContentData(String projectId, String participant) {
		
		Project project = BeanProvider.getProjectService().loadProject(projectId, false);
		if (project != null) {
		
			Participant participantData = project.getParticipation(participant);
			
			if (participantData != null) {
				renderArgs.put("_participant", participantData);
				renderArgs.put("_projectVisibility", new ProjectVisibility(new ProjectPep(project), project, getSessionWrapper().getLoggedInUserProfileId()));
				renderTemplate("tags/projects/participant.html");
			} else {
				
				logger.log(Level.WARNING, "user trying retrieve participant content data of for a particpant which does not belong to the proejct, project id is : " + projectId + " this should be impossible!");
			}
			
		} else {
			logger.log(Level.WARNING, "user trying retrieve participant content data of non existant project : " + projectId + " this should be impossible!");
		}
	}
	

}