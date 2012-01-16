package com.svend.dab.core.projects;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.svend.dab.core.beans.Config;
import com.svend.dab.core.beans.profile.UserProfile;
import com.svend.dab.core.beans.profile.UserSummary;
import com.svend.dab.core.beans.projects.Asset;
import com.svend.dab.core.beans.projects.ForumDiff;
import com.svend.dab.core.beans.projects.ForumPost;
import com.svend.dab.core.beans.projects.ForumThread;
import com.svend.dab.core.beans.projects.Participant;
import com.svend.dab.core.beans.projects.Participant.ROLE;
import com.svend.dab.core.beans.projects.ParticipantList;
import com.svend.dab.core.beans.projects.ParticpantsIdList;
import com.svend.dab.core.beans.projects.Project;
import com.svend.dab.core.beans.projects.Project.STATUS;
import com.svend.dab.core.beans.projects.RankedTag;
import com.svend.dab.core.beans.projects.TagCount;
import com.svend.dab.core.beans.projects.Task;
import com.svend.dab.core.dao.IForumPostDao;
import com.svend.dab.core.dao.IForumThreadDao;
import com.svend.dab.core.dao.ITagCountDao;
import com.svend.dab.dao.mongo.IProjectDao;
import com.svend.dab.dao.mongo.IUserProfileDao;
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
	private IForumThreadDao forumThreadDao;

	@Autowired
	private IForumPostDao forumPostDao;
	
	@Autowired
	private IUserProfileDao userProfileRepo;
	
	@Autowired
	private Config config;
	
	// -------------------
	//

	@Override
	public void createProject(Project createdProject, String creatorId) {
		createdProject.setId(UUID.randomUUID().toString().replace("-", ""));
		createdProject.getPdata().setCreationDate(new Date());
		createdProject.setStatus(STATUS.started);
		eventEmitter.emit(new ProjectCreated(createdProject, creatorId));
	}

	@Override
	public void updateProjectCore(Project updated, Set<Task> updatedTasks, Set<String> removedTasksIds, Set<Asset> updatedAssets, Set<String> removedAssetsIds) {

		// any id starting with "new" has been created by the browser for any new Task
		// replacing here with a cleaner id, more "unique"
		if (updatedTasks != null) {
			for (Task task: updatedTasks) {
				if (Strings.isNullOrEmpty(task.getId()) || task.getId().startsWith("new") ) {
					task.setId(UUID.randomUUID().toString());
				}
				task.applyAssigneeSummraiesToAssigneeUsernames();
			}
		}

		if (updatedAssets != null) {
			for (Asset asset: updatedAssets) {
				if (Strings.isNullOrEmpty(asset.getId()) || asset.getId().startsWith("new") ) {
					asset.setId(UUID.randomUUID().toString());
				}
				asset.applyAssigneeSummraiesToAssigneeUsernames();
			}
		}
		
		eventEmitter.emit(new ProjectUpdated(updated, updatedTasks, removedTasksIds, updatedAssets, removedAssetsIds));
	}

	@Override
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

	@Override
	public void applyToProject(String userId, String applicationText, Project project) {
		if (Strings.isNullOrEmpty(userId) || project == null) {
			logger.log(Level.WARNING, "not letting a null user applying to a project or a user applying to a null project");
			return;
		}
		eventEmitter.emit(new ProjectApplicationEvent(userId, project.getId(), applicationText));
	}

	@Override
	public void cancelApplication(String userId, Project project) {
		if (Strings.isNullOrEmpty(userId) || project == null) {
			logger.log(Level.WARNING, "not letting a null user cancel a proejct application or a user cancelling for a null project");
			return;
		}
		eventEmitter.emit(new ProjectApplicationCancelled(userId, project.getId()));
	}

	@Override
	public void acceptApplication(String applicantId, Project project) {
		if (Strings.isNullOrEmpty(applicantId) || project == null) {
			logger.log(Level.WARNING, "not letting a null user cancel a proejct application or a user cancelling for a null project");
			return;
		}
		eventEmitter.emit(new ProjectApplicationAccepted(applicantId, project.getId()));
	}

	// //////////////////////////////////////////////////
	// project (confirmed) participants

	@Override
	public void removeParticipant(String participantId, Project project) {
		if (Strings.isNullOrEmpty(participantId) || project == null) {
			logger.log(Level.WARNING, "not rejecting a null participant or on a null project");
			return;
		}
		eventEmitter.emit(new ProjectParticipantRemoved(participantId, project.getId()));
	}
	
	
	
	@Override
	public void makeAdmin(String username, Project project) {
		if (Strings.isNullOrEmpty(username) || project == null) {
			logger.log(Level.WARNING, "not making admin a null participant or on a null project");
			return;
		}
		eventEmitter.emit(new UserProjectRoleUpdated(username, ROLE.admin, project.getId()));
	}

	@Override
	public void makeMember(String username, Project project) {
		if (Strings.isNullOrEmpty(username) || project == null) {
			logger.log(Level.WARNING, "not making member a null participant or on a null project");
			return;
		}
		eventEmitter.emit(new UserProjectRoleUpdated(username, ROLE.member, project.getId()));
	}
	
	
	@Override
	public void proposeOwnerShip(String username, Project project) {
		if (Strings.isNullOrEmpty(username) || project == null) {
			logger.log(Level.WARNING, "not give ownership to to a null participant or on a null project");
			return;
		}
		eventEmitter.emit(new ProjectOwnershipProposed(username, project.getId()));
	}

	
	@Override
	public void cancelOwnershipTransfer(String username, Project project) {
		if (Strings.isNullOrEmpty(username) || project == null) {
			logger.log(Level.WARNING, "not cancelling an ownership transfer to to a null participant or on a null project");
			return;
		}
		
		// no event in this case: this is a single atomic change
		projectDao.updateOwnerShipProposed(project.getId(), username, false);
	}

	@Override
	public void confirmOwnershipTransfer(String promotedUsername, Project project) {
		if (Strings.isNullOrEmpty(promotedUsername) || project == null) {
			logger.log(Level.WARNING, "not cancelling an ownership transfer to to a null participant or on a null project");
			return;
		}
		eventEmitter.emit(new ProjectOwnershipAccepted(promotedUsername, project.getInitiator().getUser().getUserName(), project.getId()));
	}

	

	@Override
	public ParticpantsIdList determineRemovedParticipants(String projectId, Collection<String> knownParticipantUsernames, Collection<String> knownApplicationUsernames) {

		ParticpantsIdList response = new ParticpantsIdList();

		Project project = projectDao.loadProjectParticipants(projectId);

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

	@Override
	public ParticipantList determineAddedParticipants(String projectId, Set<String> knownParticipantUsernames, Set<String> knownApplicationUsernames) {

		ParticipantList response = new ParticipantList();
		Project project = projectDao.loadProjectParticipants(projectId);

		if (project != null) {
			if (knownApplicationUsernames != null) {
				for (Participant participant: project.getConfirmedParticipants()) {
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
	
	/////////////////////////////////////////////
	// popular project tags

	
	@Override
	public List<RankedTag> getPopularTags() {
		
		List<RankedTag> tags = new LinkedList<RankedTag>();
		
		List<TagCount> rawTags= tagCountDao.getMostPopularTags(config.getMaxNumberOfDisplayedProjectTags());
		
		if (rawTags != null && !rawTags.isEmpty()) {
			
			if (rawTags.size() == 1) {
				tags.add(new RankedTag(rawTags.get(0).getTag(), 0));
			} else {
				int highestFreq = rawTags.get(0).getValue();
				int lowestFreq = rawTags.get(rawTags.size()-1).getValue();
				
				float rankStep = (highestFreq - lowestFreq) / 5;
				
				for (TagCount rawTag : rawTags) {
					if (rawTag.getValue() > lowestFreq + 4*rankStep) {
						tags.add(new RankedTag(rawTag.getTag(), 0));
					} else if (rawTag.getValue() > lowestFreq + 3*rankStep) {
						tags.add(new RankedTag(rawTag.getTag(), 1));
					} else if (rawTag.getValue() > lowestFreq + 2*rankStep) {
						tags.add(new RankedTag(rawTag.getTag(), 2));
					} else if (rawTag.getValue() > lowestFreq + rankStep) {
						tags.add(new RankedTag(rawTag.getTag(), 3));
					} else {
						tags.add(new RankedTag(rawTag.getTag(), 4));
					} 
				}
			}
		}
		
		Collections.sort(tags, new Comparator<RankedTag>() {
			
			@Override
			public int compare(RankedTag tag1, RankedTag tag2) {
				
				if ((tag1 == null || tag1.getTag() == null) && (tag2 == null || tag2.getTag() == null)) {
					return 0;
				}
				
				if (tag1 == null || tag1.getTag() == null) {
					return -1;
				}
				
				if (tag2 == null || tag2.getTag() == null) {
					return 1;
				}
				
				return tag1.getTag().compareTo(tag2.getTag());
			}});
		
		
		return tags;
	}
	
	/////////////////////////////////////////////////
	// project cancellation / terminations

	@Override
	public void cancelProject(Project project) {
		eventEmitter.emit(new ProjectCancelled(project.getId()));
	}

	@Override
	public void terminateProject(Project project) {
		eventEmitter.emit(new ProjectStatusChanged(project.getId(), STATUS.done));
	}

	@Override
	public void restartProject(Project project) {
		eventEmitter.emit(new ProjectStatusChanged(project.getId(), STATUS.started));
	}


	
	/////////////////////////////////////////////
	// project forum

	@Override
	public ForumThread createdNewForumThread(String projectId, String threadTitle, boolean isThreadPublic) {
		return forumThreadDao.createNewThread(new ForumThread(projectId, threadTitle, new Date(), 0, isThreadPublic));
	}

	@Override
	public void postNewForumMessage(String authorId, ForumThread thread, String messageContent) {
		
		UserProfile author = userProfileRepo.retrieveUserProfileById(authorId);
		if (author == null) {
			logger.log(Level.WARNING, "User with id " + authorId + " is trying to post a message but has not registered profile! This is impossible! Not doing anything");
		} else {
			ForumPost createdPost = new ForumPost(thread.getId(), thread.getProjectId(), new Date(), new UserSummary(author), messageContent);
			forumPostDao.saveNewPost(createdPost);
			updateNumberOfPostsOfThread(thread.getId());
		}
	}

	@Override
	public ForumDiff computeThreadDiff(String threadId, Set<String> knownPostIds) {
		
		ForumDiff response = new ForumDiff();
		
		// removed threads
		Set<String> allPostIds = forumPostDao.findAllPostIdsOfThread(threadId);
		response.setDeletedPostIds(Sets.difference(knownPostIds, allPostIds).immutableCopy());
		
		// new threads
		response.setNewPosts(forumPostDao.findThreadPostsExcluding(threadId, knownPostIds));
		
		
		return response;
	}

	@Override
	public void movePostToThread(String originalThreadId, String postId, String targetThreadId, String username) {
		
		ForumPost post = forumPostDao.loadPost(postId);
		
		if (post != null && post.getThreadId().equals(originalThreadId)) {
			// TODO: consider using an event here
			
			StringBuffer updatedContent = new StringBuffer();
			updatedContent.append("===============\n");
			updatedContent.append("Forwarded by: ").append(username).append("\n");
			updatedContent.append("Original date: ").append(post.getCreationDateStr()).append("\n");
			updatedContent.append("Original message:\n\n ").append(post.getContent());
			
			forumPostDao.updateThreadIdOfPost(postId, originalThreadId, targetThreadId, new Date(), updatedContent.toString());
			updateNumberOfPostsOfThread(originalThreadId);
			updateNumberOfPostsOfThread(targetThreadId);
		}
		
	}
	
	
	/////////////////////////////////
	//
	
	protected void updateNumberOfPostsOfThread(String threadId) {
		forumThreadDao.updateNumberOfPosts(threadId, forumPostDao.countPostOfThread(threadId));
	}

}
