package com.svend.dab.core;

import java.util.List;
import java.util.Set;

import com.svend.dab.core.beans.profile.Contact;
import com.svend.dab.core.beans.profile.PrivacySettings;
import com.svend.dab.core.beans.profile.UserProfile;
import com.svend.dab.core.beans.profile.UserReference;

/**
 * @author Svend
 * 
 */
public interface IUserProfileService {

	/**
	 * @param username
	 * @param password
	 * @return
	 */

	public abstract UserProfile loadUserProfile(String userName, boolean shouldPrepareLinks);

	public abstract boolean doesUsernameExists(String username);
	/**
	 * Adds this new user to persistence
	 * 
	 * @param createdUserProfile
	 */
	public abstract UserProfile registerUser(UserProfile createdUserProfile);

	/**
	 * 
	 * registers that this user has logged in
	 * @param userProfile
	 */
	public abstract void loggedIn(UserProfile userProfile);
	
	public void updateProfilePersonalData(UserProfile userProfile);
	
	public abstract void updatePrivacySettings(String loggedInUserProfileId, PrivacySettings settings);
	
	public abstract void updateCv(UserProfile profile, byte[] cvContent);
	
	public abstract void removeCv(UserProfile editedUserProfile);
	
	//----------------------
	// photos

	public abstract void updatePhotoGallery(UserProfile profile, boolean hasMainPhotoChanged);
	
	// ----------------------------------
	// references
	
	
	public abstract void leaveAReference(String fromUserId, String toUserId, String referenceText);

	public abstract void removeUserReference(String removedReferenceId, String fromUserId, String toUserId);

	public abstract List<UserReference> getOtherReceivedReferencesThan(String userId, Set<String> refsToDiscard);

	public abstract List<String> determineNonExisingReceivedRefIds(String userId, Set<String> refsToDiscard);

	public abstract List<UserReference> getOtherWrittentReferencesThan(String userId, Set<String> refsToDiscard);

	public abstract List<String> determineNonExisingWrittenRefIds(String userId, Set<String> refsToDiscard);
	
	

	// ----------------------------------
	// contacts
	
	
	public abstract void sendRequestToAddToContacts(String loggedInUserProfileId, String visitedUserId, String text);

	public abstract void cancelRequestToAddToContacts(String loggedInUserProfileId, String visitedUserId);

	public abstract void rejectRequestToAddToContacts(String loggedInUserProfileId, String rejectedContactUserName);

	public abstract void acceptRequestToAddToContacts(String loggedInUserProfileId, String acceptedContactUserName);

	public abstract void removeContactFromProfile(String editedProfileUsername, String removedUserName);

	public List<String> determineNonExistingAnyContact(String vuser, List<String> knownPendingReceivedIds, List<String> knownPendingSentIds, List<String> knownContactsIds);

	public abstract List<Contact> getOtherReceivedPendingContactRequestsThan(String vuser, Set<String> contactsToDiscard);

	public abstract List<Contact> getOtherConfirmedCo1ntactsThan(String vuser, Set<String> contactsToDiscard);

	



	

}