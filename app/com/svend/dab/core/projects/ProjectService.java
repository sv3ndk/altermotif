package com.svend.dab.core.projects;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import web.utils.Utils;

import com.google.common.base.Strings;
import com.svend.dab.core.beans.Config;
import com.svend.dab.core.beans.projects.Asset;
import com.svend.dab.core.beans.projects.Participant;
import com.svend.dab.core.beans.projects.Participant.ROLE;
import com.svend.dab.core.beans.projects.ParticipantList;
import com.svend.dab.core.beans.projects.ParticpantsIdList;
import com.svend.dab.core.beans.projects.Project;
import com.svend.dab.core.beans.projects.Project.STATUS;
import com.svend.dab.core.beans.projects.RankedTag;
import com.svend.dab.core.beans.projects.Task;
import com.svend.dab.core.dao.IProjectDao;
import com.svend.dab.core.dao.ITagCountDao;
import com.svend.dab.eda.EventEmitter;
import com.svend.dab.eda.events.projects.ProjectApplicationAccepted;
import com.svend.dab.eda.events.projects.ProjectApplicationCancelled;
import com.svend.dab.eda.events.projects.ProjectApplicationEvent;
import com.svend.dab.eda.events.projects.ProjectCancelled;
import com.svend.dab.eda.events.projects.ProjectCreated;
import com.svend.dab.eda.events.projects.ProjectOwnershipAccepted;
import com.svend.dab.eda.events.projects.ProjectOwnershipProposed;
import com.svend.dab.eda.events.projects.ProjectParticipantRemoved;
import com.svend.dab.eda.events.projects.ProjectStatusChanged;
import com.svend.dab.eda.events.projects.ProjectUpdated;
import com.svend.dab.eda.events.projects.UserProjectRoleUpdated;

@Component("projectService")
public class ProjectService implements IProjectService {

	private static Logger logger = Logger.getLogger(ProjectService.class.getName());

	@Autowired
	private EventEmitter eventEmitter;

	@Autowired
	private IProjectDao projectDao;

	@Autowired
	private ITagCountDao tagCountDao;

	@Autowired
	private Config config;

	// -------------------
	//

	public void createProject(Project createdProject, String creatorId) {
		createdProject.setId(UUID.randomUUID().toString().replace("-", ""));
		createdProject.getPdata().setCreationDate(new Date());
		createdProject.setStatus(STATUS.started);
		eventEmitter.emit(new ProjectCreated(createdProject, creatorId));
	}

	public void updateProjectCore(Project updated, Set<Task> updatedTasks, Set<String> removedTasksIds, Set<Asset> updatedAssets, Set<String> removedAssetsIds) {

		// any id starting with "new" has been created by the browser for any new Task
		// replacing here with a cleaner id, more "unique"
		if (updatedTasks != null) {
			for (Task task : updatedTasks) {
				if (Strings.isNullOrEmpty(task.getId()) || task.getId().startsWith("new")) {
					task.setId(UUID.randomUUID().toString());
				}
				task.applyAssigneeSummraiesToAssigneeUsernames();
			}
		}

		if (updatedAssets != null) {
			for (Asset asset : updatedAssets) {
				if (Strings.isNullOrEmpty(asset.getId()) || asset.getId().startsWith("new")) {
					asset.setId(UUID.randomUUID().toString());
				}
				asset.applyAssigneeSummraiesToAssigneeUsernames();
			}
		}

		eventEmitter.emit(new ProjectUpdated(updated, updatedTasks, removedTasksIds, updatedAssets, removedAssetsIds));
	}

	public Project loadProject(String projectId, boolean generatePhotoLinks) {

		if (Strings.isNullOrEmpty(projectId)) {
			return null;
		}

		Project prj = projectDao.findOne(projectId);

		if (prj != null) {
			prj.prepareTasksAndAssetsUsersummary();

			if (generatePhotoLinks) {
				Date expirationdate = new Date();
				expirationdate.setTime(expirationdate.getTime() + config.getCvExpirationDelayInMillis());
				prj.generatePhotoLinks(expirationdate);
			}
		}

		return prj;
	}

	// //////////////////////////////////////////////////
	// project applications

	public void applyToProject(String userId, String applicationText, Project project) {
		if (Strings.isNullOrEmpty(userId) || project == null) {
			logger.log(Level.WARNING, "not letting a null user applying to a project or a user applying to a null project");
			return;
		}
		eventEmitter.emit(new ProjectApplicationEvent(userId, project.getId(), applicationText));
	}

	public void cancelApplication(String userId, Project project) {
		if (Strings.isNullOrEmpty(userId) || project == null) {
			logger.log(Level.WARNING, "not letting a null user cancel a proejct application or a user cancelling for a null project");
			return;
		}
		eventEmitter.emit(new ProjectApplicationCancelled(userId, project.getId()));
	}

	public void acceptApplication(String applicantId, Project project) {
		if (Strings.isNullOrEmpty(applicantId) || project == null) {
			logger.log(Level.WARNING, "not letting a null user cancel a proejct application or a user cancelling for a null project");
			return;
		}
		eventEmitter.emit(new ProjectApplicationAccepted(applicantId, project.getId()));
	}

	// //////////////////////////////////////////////////
	// project (confirmed) participants

	public void removeParticipant(String participantId, Project project) {
		if (Strings.isNullOrEmpty(participantId) || project == null) {
			logger.log(Level.WARNING, "not rejecting a null participant or on a null project");
			return;
		}
		eventEmitter.emit(new ProjectParticipantRemoved(participantId, project.getId()));
	}

	public void makeAdmin(String username, Project project) {
		if (Strings.isNullOrEmpty(username) || project == null) {
			logger.log(Level.WARNING, "not making admin a null participant or on a null project");
			return;
		}
		eventEmitter.emit(new UserProjectRoleUpdated(username, ROLE.admin, project.getId()));
	}

	public void makeMember(String username, Project project) {
		if (Strings.isNullOrEmpty(username) || project == null) {
			logger.log(Level.WARNING, "not making member a null participant or on a null project");
			return;
		}
		eventEmitter.emit(new UserProjectRoleUpdated(username, ROLE.member, project.getId()));
	}

	public void proposeOwnerShip(String username, Project project) {
		if (Strings.isNullOrEmpty(username) || project == null) {
			logger.log(Level.WARNING, "not give ownership to to a null participant or on a null project");
			return;
		}
		eventEmitter.emit(new ProjectOwnershipProposed(username, project.getId()));
	}

	public void cancelOwnershipTransfer(String username, Project project) {
		if (Strings.isNullOrEmpty(username) || project == null) {
			logger.log(Level.WARNING, "not cancelling an ownership transfer to to a null participant or on a null project");
			return;
		}

		// no event in this case: this is a single atomic change
		projectDao.updateOwnerShipProposed(project.getId(), username, false);
	}

	public void confirmOwnershipTransfer(String promotedUsername, Project project) {
		if (Strings.isNullOrEmpty(promotedUsername) || project == null) {
			logger.log(Level.WARNING, "not cancelling an ownership transfer to to a null participant or on a null project");
			return;
		}
		eventEmitter.emit(new ProjectOwnershipAccepted(promotedUsername, project.getInitiator().getUser().getUserName(), project.getId()));
	}

	public ParticpantsIdList determineRemovedParticipants(String projectId, Collection<String> knownParticipantUsernames,
			Collection<String> knownApplicationUsernames) {

		Project project = projectDao.loadProjectParticipants(projectId);
		ParticpantsIdList response = new ParticpantsIdList();

		if (project != null) {

			if (knownApplicationUsernames != null) {
				for (String applicantUsername : knownApplicationUsernames) {
					if (!Strings.isNullOrEmpty(applicantUsername) && !project.isUserApplying(applicantUsername)) {
						response.addApplicationUsername(applicantUsername);
					}
				}
			}

			if (knownParticipantUsernames != null) {
				for (String participantUsername : knownParticipantUsernames) {
					if (!Strings.isNullOrEmpty(participantUsername) && !project.isUserAlreadyMember(participantUsername)) {
						response.addParticipant(participantUsername);
					}
				}
			}
		}

		return response;
	}

	public ParticipantList determineAddedParticipants(String projectId, Set<String> knownParticipantUsernames, Set<String> knownApplicationUsernames) {

		ParticipantList response = new ParticipantList();
		Project project = projectDao.loadProjectParticipants(projectId);

		if (project != null) {
			if (knownApplicationUsernames != null) {
				for (Participant participant : project.getConfirmedParticipants()) {
					if (!knownParticipantUsernames.contains(participant.getUser().getUserName())) {
						response.addParticipant(participant);
					}
				}
			}

			if (knownParticipantUsernames != null) {
				for (Participant applicant : project.getUnconfirmedActiveParticipants()) {
					if (!knownApplicationUsernames.contains(applicant.getUser().getUserName())) {
						response.addApplication(applicant);
					}
				}
			}

		}
		return response;
	}

	// ///////////////////////////////////////////
	// popular project tags

	public List<RankedTag> getPopularTags() {
		return Utils.rankCountedTags(tagCountDao.getMostPopularProjectTags(config.getMaxNumberOfDisplayedTags()));
	}

	// ///////////////////////////////////////////////
	// project cancellation / terminations

	public void cancelProject(Project project) {
		eventEmitter.emit(new ProjectCancelled(project.getId()));
	}

	public void terminateProject(Project project) {
		eventEmitter.emit(new ProjectStatusChanged(project.getId(), STATUS.done));
	}

	public void restartProject(Project project) {
		eventEmitter.emit(new ProjectStatusChanged(project.getId(), STATUS.started));
	}

}