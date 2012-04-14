package com.svend.dab.core.groups;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import web.utils.Utils;

import com.google.common.base.Strings;
import com.svend.dab.core.beans.Config;
import com.svend.dab.core.beans.groups.GroupParticipant.ROLE;
import com.svend.dab.core.beans.groups.ProjectGroup;
import com.svend.dab.core.beans.profile.UserProfile;
import com.svend.dab.core.beans.profile.UserSummary;
import com.svend.dab.core.beans.projects.Project;
import com.svend.dab.core.beans.projects.ProjectSummary;
import com.svend.dab.core.beans.projects.RankedTag;
import com.svend.dab.core.dao.IGroupDao;
import com.svend.dab.core.dao.ITagCountDao;
import com.svend.dab.core.dao.IUserProfileDao;
import com.svend.dab.core.projects.IProjectService;
import com.svend.dab.eda.EventEmitter;
import com.svend.dab.eda.events.groups.GroupClosed;
import com.svend.dab.eda.events.groups.GroupCreated;
import com.svend.dab.eda.events.groups.GroupProjectApplicationAccepted;
import com.svend.dab.eda.events.groups.GroupProjectRemoved;
import com.svend.dab.eda.events.groups.GroupUpdatedEvent;
import com.svend.dab.eda.events.groups.GroupUserRemoved;
import com.svend.dab.eda.events.groups.GroupUserRoleUpdated;
import com.svend.dab.eda.events.groups.GroupsUserApplicationAccepted;

/**
 * @author svend
 * 
 */
@Service
public class GroupService implements IGroupService {

	private static Logger logger = Logger.getLogger(GroupService.class.getName());

	@Autowired
	private EventEmitter eventEmitter;

	@Autowired
	private IProjectService projectService;

	@Autowired
	private IGroupDao groupDao;

	@Autowired
	private IUserProfileDao userProfileRepo;

	@Autowired
	private Config config;

	@Autowired
	private ITagCountDao tagCountDao;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.svend.dab.core.IGroupService#createNewGroup(com.svend.dab.core.beans.groups.Group, java.lang.String)
	 */
	public void createNewGroup(ProjectGroup createdGroup, String creatorId) {
		if (createdGroup != null && !Strings.isNullOrEmpty(creatorId)) {
			createdGroup.setId(UUID.randomUUID().toString().replace("-", ""));
			createdGroup.setCreationDate(new Date());
			eventEmitter.emit(new GroupCreated(createdGroup, creatorId));
		}
	}

	public ProjectGroup loadGroupById(String groupId, boolean preparePresignedLinks) {

		ProjectGroup group = groupDao.retrieveGroupById(groupId);

		if (group != null && preparePresignedLinks) {
			Date expirationdate = new Date();
			expirationdate.setTime(expirationdate.getTime() + config.getPhotoExpirationDelayInMillis());
			group.generatePhotoLinks(expirationdate);
		}

		return group;
	}

	public void updateGroupData(ProjectGroup editedGroup) {
		if (editedGroup != null) {
			eventEmitter.emit(new GroupUpdatedEvent(editedGroup));
		}
	}

	public void closeGroup(String groupId) {
		if (!Strings.isNullOrEmpty(groupId)) {
			eventEmitter.emit(new GroupClosed(groupId));
		}
	}

	public void applyToGroup(String groupId, String userId) {

		ProjectGroup group = groupDao.retrieveGroupById(groupId);
		UserProfile user = userProfileRepo.retrieveUserProfileById(userId);

		if (group != null && group.isActive() && user != null && user.getPrivacySettings().isProfileActive()) {
			if (!group.isMemberOrHasALreadyApplied(userId)) {
				groupDao.addUserApplication(groupId, new UserSummary(user));
			}
		}
	}

	public void cancelUserApplicationToGroup(String groupId, String userId) {
		groupDao.removeUserParticipant(groupId, userId);
	}

	public void acceptUserApplicationToGroup(String groupId, String applicantId) {
		if (!Strings.isNullOrEmpty(groupId) && !Strings.isNullOrEmpty(applicantId)) {
			eventEmitter.emit(new GroupsUserApplicationAccepted(groupId, applicantId));
		}
	}

	public void removeUserFromGroup(String groupId, String userId) {
		if (!Strings.isNullOrEmpty(groupId) && !Strings.isNullOrEmpty(userId)) {
			eventEmitter.emit(new GroupUserRemoved(groupId, userId));
		}
	}

	public void updateUserParticipantRole(String groupId, String userId, ROLE role) {
		if (!Strings.isNullOrEmpty(groupId) && !Strings.isNullOrEmpty(userId)) {
			eventEmitter.emit(new GroupUserRoleUpdated(groupId, userId, role));
		}
	}

	public void applyToGroupWithProject(String userId, String groupId, String projectId, String applicationText) {

		if (!Strings.isNullOrEmpty(userId) && !Strings.isNullOrEmpty(groupId) && !Strings.isNullOrEmpty(projectId)) {

			ProjectGroup group = loadGroupById(groupId, true);
			UserProfile profile = userProfileRepo.retrieveUserProfileById(userId);
			Project project = projectService.loadProject(projectId, false);

			if (group != null && profile != null) {

				if (!group.isProjectMemberOfGroupOrHasAlreadyApplied(projectId)) {
					com.svend.dab.core.beans.projects.Participant.ROLE role = profile.getRoleInProject(projectId);
					if (role == com.svend.dab.core.beans.projects.Participant.ROLE.admin
							|| role == com.svend.dab.core.beans.projects.Participant.ROLE.initiator) {
						groupDao.addProjectApplication(groupId, new ProjectSummary(project), applicationText);
					}
				}
			} else {
				// todo: lag warning here
			}
		}
	}

	public void rejectProjectApplication(String groupId, String projectId) {
		if (!Strings.isNullOrEmpty(groupId) && !Strings.isNullOrEmpty(projectId)) {
			groupDao.removeProjectParticipant(groupId, projectId);
		}
	}

	public void removeProjectFromGroup(String groupId, String projectId) {
		if (!Strings.isNullOrEmpty(projectId) && !Strings.isNullOrEmpty(projectId)) {
			eventEmitter.emit(new GroupProjectRemoved(groupId, projectId));
		}
	}

	public void acceptProjectApplication(String groupId, String projectId) {
		if (!Strings.isNullOrEmpty(groupId) && !Strings.isNullOrEmpty(projectId)) {
			eventEmitter.emit(new GroupProjectApplicationAccepted(groupId, projectId));
		}
	}

	public List<RankedTag> getPopularTags() {
		return Utils.rankCountedTags(tagCountDao.getMostPopularGroupTags(config.getMaxNumberOfDisplayedTags()));
	}

}
