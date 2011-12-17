package com.svend.dab.core.projects;

import java.util.Collection;
import java.util.Set;

import com.svend.dab.core.beans.projects.ParticipantList;
import com.svend.dab.core.beans.projects.ParticpantsIdList;
import com.svend.dab.core.beans.projects.Project;

/**
 * @author Svend
 *
 */
public interface IProjectService {
	
	public void createProject(Project createdProject, String creatorId);
	
	public Project loadProject(String projectId, boolean generatePhotoLinks);

	@Deprecated
	public void updateProject(Project updated);

	////////////////////////////////////////////////////
	// project applications
	
	public void applyToProject(String loggedInUserProfileId, String applicationText, Project project);
	
	public void cancelApplication(String rejectedApplicantId, Project project);

	public void acceptApplication(String applicantId, Project project);
	
	////////////////////////////////////////////////////
	// project (confirmed) participants 

	public void removeParticipant(String participant, Project project);

	public ParticpantsIdList determineRemovedParticipants(String projectId, Collection<String> knownParticipantUsernames, Collection<String> knownApplicationUsernames);

	public ParticipantList determineAddedParticipants(String projectId, Set<String> knownParticipantUsernamesSet, Set<String> knownApplicationUsernamesSet);

	public void makeAdmin(String participant, Project project);

	public void makeMember(String participant, Project project);

	public void proposeOwnerShip(String participant, Project project);

	public void cancelOwnershipTransfer(String participant, Project project);

	public void confirmOwnershipTransfer(String promotedUsername, Project project);
	
}