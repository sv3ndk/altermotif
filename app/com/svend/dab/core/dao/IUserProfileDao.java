package com.svend.dab.core.dao;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.svend.dab.core.beans.groups.GroupParticipation;
import com.svend.dab.core.beans.groups.GroupSummary;
import com.svend.dab.core.beans.profile.Contact;
import com.svend.dab.core.beans.profile.PersonalData;
import com.svend.dab.core.beans.profile.Photo;
import com.svend.dab.core.beans.profile.PrivacySettings;
import com.svend.dab.core.beans.profile.UserProfile;
import com.svend.dab.core.beans.profile.UserReference;
import com.svend.dab.core.beans.profile.UserSummary;
import com.svend.dab.core.beans.projects.Participant.ROLE;
import com.svend.dab.core.beans.projects.Participation;
import com.svend.dab.core.beans.projects.Project.STATUS;

public interface IUserProfileDao {

	// -----------------------------
	// core profile data
	
	public Set<String> getAllUsernames();
	
	public List<UserProfile> retrieveUserProfilesByIds(List<String> ids);
	
	public UserProfile retrieveUserProfileById(String userId);
	
	public void replacePrivacySettings(String updatedUserId, PrivacySettings newPrivacySettings);
	
	public void replaceLatestLoginAndPermKey(String updatedUserId, String uploadPermKey, Date dateOfLoggin);

	public void replacePersonalData(UserProfile updatedUser, PersonalData pData);
	
	public void save(UserProfile createdUserProfile);

	// ------------------------------------------
	// photos
	
	public void updatePhotoGallery(UserProfile userProfile);
	
	public void addOnePhoto(String username, Photo photo);
	
	public void removeOnePhoto(String username, Photo photo);
	
	public void removeOnePhotoAndResetMainPhotoIndex(String username, Photo removed);
	
	public void removeOnePhotoAndDecrementMainPhotoIndex(String username, Photo removed);

	public void movePhotoToFirstPosition(String username, int photoIndex);
	
	public void updateCv(UserProfile editedUserProfile);
	
	// ---------------------------------
	// contacts
	
	
	public void addPendingSentContactRequest(UserProfile fromUser, Contact createdPendingContact);

	public void addPendingReceivedContactRequest(UserProfile fromUser, Contact createdPendingContact);

	public void addConfirmedContact(UserProfile toUser, Contact existingContact);
	
	public void removePendingSentRelationship(UserProfile fromUser, String username);

	public void removedPendingReceivedRelationship(UserProfile toUser, String username);
	
	public void removeConfirmedContact(UserProfile cancellingUser, String username);
	
	public void updateContact(String updatedUserId, Contact contact, UserSummary updatedUserSummary, boolean updateRequestor);
	
	public void updatePendingSentContactRequests(String username, Contact contact, UserSummary updatedUserSummary, boolean updateRequestor);

	public void updatePendingReceivedContactRequests(String userName, Contact contact, UserSummary updatedUserSummary, boolean updateRequestor);


	
	// ---------------------------------------
	// references
	
	public void addReceivedReference(UserProfile userProfile, UserReference createdReference);

	public void addWrittenReference(UserProfile userProfile, UserReference createdReference);
	
	public void removeWrittenReference(UserProfile toProfile, String referenceId);

	public void removeReceivedReference(UserProfile fromProfile, String referenceId);
	
	/**
	 * UPdates the "fromUser" field of this Written reference, in this {@link UserProfile} (NOT ON THE {@link UserProfile} of the "other" user)
	 * 
	 * @param profile
	 * @param ref
	 * @param updatedUserSummary
	 */
	public void updateWrittenReferenceFromUser(String updatedUserId, UserReference ref, UserSummary updatedUserSummary);
	
	public void updateReceivedReferenceFromUser(String userName, UserReference ref, UserSummary updatedUserSummary);

	public void updateReceivedReferenceToUser(String username, UserReference ref, UserSummary updatedUserSummary);

	public void updateWrittenReferenceToUser(String userName, UserReference ref, UserSummary updatedUserSummary);
	
	
	// ------------------------------------------------------
	// projects
	
	
	public void addProjectParticipation(UserProfile userProfile, Participation participation);

	public void removeParticipation(String username, String projectId);

	public void updateProjectMainPhoto(String userName, String projectId, Photo mainPhoto);

	public void markParticipationHasAccepted(String userId, String projectId);

	public void updateProjectRole(String userId, String projectId, ROLE role);

	public void updateProjectStatus(String userName, String projectId, STATUS newStatus);


	// ------------------------------------------------------
	// groups

	public void addParticipationInGroup(String username, GroupParticipation groupParticipation);
	
	public void removeParticipationInGroup(String participantId, String groupId);

	public void updateGroupSummaryOfAllUsersPartOf(GroupSummary updatedSummary);


}
