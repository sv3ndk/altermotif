package com.svend.dab.core.projects;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.svend.dab.core.beans.projects.Asset;
import com.svend.dab.core.beans.projects.ForumDiff;
import com.svend.dab.core.beans.projects.ForumThread;
import com.svend.dab.core.beans.projects.ParticipantList;
import com.svend.dab.core.beans.projects.ParticpantsIdList;
import com.svend.dab.core.beans.projects.Project;
import com.svend.dab.core.beans.projects.ProjectData;
import com.svend.dab.core.beans.projects.RankedTag;
import com.svend.dab.core.beans.projects.Task;

/**
 * @author Svend
 * 
 */
public interface IProjectService {

	public void createProject(Project createdProject, String creatorId);

	public Project loadProject(String projectId, boolean generatePhotoLinks);

	/**
	 * Triggers an update of the project {@link ProjectData}, links and tags (not the whole project)
	 * 
	 * @param updated
	 * @param updatedTasks
	 * @param removedTasksIds
	 * @param removedAssetsIds
	 * @param updatedAssets
	 */
	public void updateProjectCore(Project updated, Set<Task> updatedTasks, Set<String> removedTasksIds, Set<Asset> updatedAssets, Set<String> removedAssetsIds);

	// //////////////////////////////////////////////////
	// project applications

	public void applyToProject(String loggedInUserProfileId, String applicationText, Project project);

	public void cancelApplication(String rejectedApplicantId, Project project);

	public void acceptApplication(String applicantId, Project project);

	// //////////////////////////////////////////////////
	// project (confirmed) participants

	public void removeParticipant(String participant, Project project);

	public ParticpantsIdList determineRemovedParticipants(String projectId, Collection<String> knownParticipantUsernames,
			Collection<String> knownApplicationUsernames);

	public ParticipantList determineAddedParticipants(String projectId, Set<String> knownParticipantUsernamesSet, Set<String> knownApplicationUsernamesSet);

	public void makeAdmin(String participant, Project project);

	public void makeMember(String participant, Project project);

	public void proposeOwnerShip(String participant, Project project);

	public void cancelOwnershipTransfer(String participant, Project project);

	public void confirmOwnershipTransfer(String promotedUsername, Project project);

	// //////////////////////////////////////////
	// project status

	public void cancelProject(Project project);

	public void terminateProject(Project project);

	public void restartProject(Project project);

	// ///////////////////////////////////////////
	// popular project tags

	public List<RankedTag> getPopularTags();

	// ///////////////////////////////////////////
	// project forum

	public void postNewForumMessage(String authorId, ForumThread thread, String messageContent);

	public ForumDiff computeThreadDiff(String threadId, Set<String> knownPostIds);

	public void movePostToThread(String originalThreadId, String postId, String targetThreadId, String username);

}