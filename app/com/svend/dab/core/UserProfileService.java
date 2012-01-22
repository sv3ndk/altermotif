package com.svend.dab.core;

import java.io.Serializable;
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
import com.svend.dab.core.beans.Config;
import com.svend.dab.core.beans.DabException;
import com.svend.dab.core.beans.DabUploadFailedException;
import com.svend.dab.core.beans.DabUploadFailedException.failureReason;
import com.svend.dab.core.beans.profile.Contact;
import com.svend.dab.core.beans.profile.PrivacySettings;
import com.svend.dab.core.beans.profile.UserProfile;
import com.svend.dab.core.beans.profile.UserReference;
import com.svend.dab.core.beans.profile.UserSummary;
import com.svend.dab.core.dao.ICvBinaryDao;
import com.svend.dab.dao.mongo.IUserProfileDao;
import com.svend.dab.eda.EventEmitter;
import com.svend.dab.eda.events.contacts.ContactRelationshipRemoved;
import com.svend.dab.eda.events.contacts.ContactRelationshipRequested;
import com.svend.dab.eda.events.contacts.ContactRelationshipResponse;
import com.svend.dab.eda.events.contacts.ContactRelationshipResponse.RESPONSE;
import com.svend.dab.eda.events.profile.UserLoggedInEvent;
import com.svend.dab.eda.events.profile.UserPrivacySettingsUpdatedEvent;
import com.svend.dab.eda.events.profile.UserProfilePersonalDataUpdatedEvent;
import com.svend.dab.eda.events.profile.UserReferenceRemovedEvent;
import com.svend.dab.eda.events.profile.UserReferenceWritten;
import com.svend.dab.eda.events.profile.UserSummaryUpdated;
import com.svend.dab.eda.events.s3.BinaryNoLongerRequiredEvent;

/**
 * @author Svend
 * 
 */

@Component("userProfileService")
public class UserProfileService implements IUserProfileService, Serializable {

	private static final long serialVersionUID = 5489450661284795928L;

	// -----------------------------------
	//

	@Autowired
	private EventEmitter emitter;

	@Autowired
	private IUserProfileDao userProfileRepo;

	@Autowired
	private ICvBinaryDao cvDao;

	@Autowired
	private Config config;

	private static Logger logger = Logger.getLogger(UserProfileService.class.getName());

	// -----------------------------------
	//

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.svend.dab.core.dummy.IUserProfile#getUserProfile(java.lang.String)
	 */
	public UserProfile loadUserProfile(String userName, boolean shouldPrepareLinks) {
		UserProfile profile = userProfileRepo.retrieveUserProfileById(userName);

		if (profile != null && shouldPrepareLinks) {
			Date expirationdate = new Date();
			expirationdate.setTime(expirationdate.getTime() + config.getCvExpirationDelayInMillis());

			// TODO: optimize this: sometimes only some of those links required
			// (when viewing the CV, there is no need to prepare the photos
			// links)
			profile.generateCvLink(expirationdate);
			profile.generatephotoLinks(expirationdate);
		}

		return profile;
	}

	
	public boolean doesUsernameExists(String username) {
		// TODO: optimization: not necessary to load the full user here
		return loadUserProfile(username, false) != null;
	}

	
	public UserProfile registerUser(UserProfile createdUserProfile) {
		// no event here: we want to make sure the whole operation is done
		// synchronously (and this should be fast...)
		createdUserProfile.setUploadPermKey(UUID.randomUUID().toString());
		createdUserProfile.setDateOfLatestLogin(new Date());
		userProfileRepo.save(createdUserProfile);
		return createdUserProfile;
	}

	
	public void loggedIn(UserProfile userProfile) {
		emitter.emit(new UserLoggedInEvent(userProfile));
	}

	
	public void updateProfilePersonalData(UserProfile userProfile) {
		if (userProfile == null || Strings.isNullOrEmpty(userProfile.getUsername())) {
			logger.log(Level.WARNING, "Refusing to save a null profile or a profile with null username");
		} else {
			emitter.emit(new UserProfilePersonalDataUpdatedEvent(userProfile));
		}
	}

	
	
	public void updatePrivacySettings(String username, PrivacySettings settings) {
		
		if (Strings.isNullOrEmpty(username) || settings == null) {
			logger.log(Level.WARNING, "Refusing to save a null privacy settings or a setting with null username: " + username);
		} else {
			emitter.emit(new UserPrivacySettingsUpdatedEvent(username, settings));
		}
		
	}

	

	
	public void leaveAReference(String fromUserId, String toUserId, String referenceText) {
		if (Strings.isNullOrEmpty(fromUserId)) {
			logger.log(Level.WARNING, "Not adding a reference with a null from user");
		} else if (Strings.isNullOrEmpty(toUserId)) {
			logger.log(Level.WARNING, "Not adding a reference with a null to user");
		} else {
			UserReferenceWritten event = new UserReferenceWritten();
			event.setReferenceId(UUID.randomUUID().toString());
			event.setFromUserName(fromUserId);
			event.setToUserName(toUserId);
			event.setCreationDate(new Date());
			event.setText(referenceText);
			emitter.emit(event);
		}
	}

	
	public void removeUserReference(String removedReferenceId, String fromUserId, String toUserId) {
		if (Strings.isNullOrEmpty(removedReferenceId)) {
			logger.log(Level.WARNING, "Not removing a reference with a null id");
		} else if (Strings.isNullOrEmpty(fromUserId)) {
			logger.log(Level.WARNING, "Not removing a reference with a null from user");
		} else if (Strings.isNullOrEmpty(toUserId)) {
			logger.log(Level.WARNING, "Not removing a reference with a null to user");
		} else {
			UserReferenceRemovedEvent event = new UserReferenceRemovedEvent();
			event.setReferenceId(removedReferenceId);
			event.setToProfileId(toUserId);
			event.setFromProfileId(fromUserId);
			emitter.emit(event);
		}
	}

	
	public List<UserReference> getOtherReceivedReferencesThan(String userId, java.util.Set<String> refsToDiscard) {

		// TODO: optimize this: only look for received references ids

		UserProfile userProfile = loadUserProfile(userId, false);

		if (userProfile == null) {
			logger.log(Level.WARNING, "Could not reload received references of this user: does not exist: " + userId);
			return new LinkedList<UserReference>();
		}

		List<UserReference> newReferences = new LinkedList<UserReference>();
		for (UserReference ref : userProfile.getReceivedReferences()) {
			if (!refsToDiscard.contains(ref.getId())) {
				newReferences.add(ref);
			}
		}

		return newReferences;
	}

	
	public List<String> determineNonExisingReceivedRefIds(String userId, Set<String> refsToDiscard) {
		UserProfile userProfile = loadUserProfile(userId, false);
		
		if (userProfile == null) {
			logger.log(Level.WARNING, "Could not reload written references of this user: does not exist: " + userId);
			return new LinkedList<String>();
		}
		
		List<String> refsIdsFromProfile = new LinkedList<String>();
		for (UserReference ref : userProfile.getReceivedReferences()) {
			refsIdsFromProfile.add(ref.getId());
		}
		
		List<String> refsNoLongerPresent = new LinkedList<String>();
		
		for (String refId : refsToDiscard) {
			if (!refsIdsFromProfile.contains(refId)) {
				refsNoLongerPresent.add(refId);
			}
		}
		
		return refsNoLongerPresent;
	}

	
	
	
	public List<UserReference> getOtherWrittentReferencesThan(String userId, Set<String> refsToDiscard) {
		// TODO: optimize this: only look for received references ids
		
		UserProfile userProfile = loadUserProfile(userId, false);
		
		if (userProfile == null) {
			logger.log(Level.WARNING, "Could not reload sent references of this user: does not exist: " + userId);
			return new LinkedList<UserReference>();
		}
		
		List<UserReference> newReferences = new LinkedList<UserReference>();
		for (UserReference ref : userProfile.getWrittenReferences()) {
			if (!refsToDiscard.contains(ref.getId())) {
				newReferences.add(ref);
			}
		}
		
		return newReferences;
	}

	
	
	
	
	public List<String> determineNonExisingWrittenRefIds(String userId, Set<String> knownRefs) {
		// TODO: optimize this: only look for received references ids
		UserProfile userProfile = loadUserProfile(userId, false);
		
		if (userProfile == null) {
			logger.log(Level.WARNING, "Could not reload written references of this user: does not exist: " + userId);
			return new LinkedList<String>();
		}
		
		List<String> refsIdsFromProfile = new LinkedList<String>();
		for (UserReference ref : userProfile.getWrittenReferences()) {
			refsIdsFromProfile.add(ref.getId());
		}
		
		List<String> refsNoLongerPresent = new LinkedList<String>();
		
		for (String refId : knownRefs) {
			if (!refsIdsFromProfile.contains(refId)) {
				refsNoLongerPresent.add(refId);
			}
		}
		
		return refsNoLongerPresent;
	}
	
	
	
	// ----------------------------------------
	// handling of the profile CV
	// ----------------------------------------
	
	public void updateCv(UserProfile profile, byte[] cvContent) {

		if (cvContent == null || cvContent.length == 0 || cvContent.length > config.getMaxUploadedCVSizeInBytes()) {
			throw new DabUploadFailedException("CV size is o or bigger than maximum authorized", failureReason.fileTooBig);
		}

		// uploads a CV to S3
		cvDao.uploadCvPdf(profile, cvContent);

		userProfileRepo.updateCv(profile);

	}

	
	public void removeCv(UserProfile editedUserProfile) {
		editedUserProfile.setCvLink(null);
		userProfileRepo.updateCv(editedUserProfile);

		// actual removal of the file from s3 is done asynchronously, in order
		// to improve gui response time
		try {
			BinaryNoLongerRequiredEvent event = new BinaryNoLongerRequiredEvent(editedUserProfile.getCvLink());
			emitter.emit(event);
		} catch (DabException e) {
			logger.log(Level.WARNING, "Could not emit event for removing cv of profile " + editedUserProfile.getUsername() + " => this might lead to dead space in s3 storage", e);
		}
	}

	// ------------------------------------------------
	// contacts
	// ------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.svend.dab.core.IUserProfileService#sendRequestToAddToContacts(java .lang.String, java.lang.String, java.lang.String)
	 */
	
	public void sendRequestToAddToContacts(String fromUser, String toUser, String text) {
		if (Strings.isNullOrEmpty(fromUser) || Strings.isNullOrEmpty(toUser)) {
			logger.log(Level.WARNING, "refusing to send a requets to add a null user to contact or add a contact to a null user. FromUser=" + fromUser + ", toUser=" + toUser);
		} else if (fromUser.equals(toUser)) {
			logger.log(Level.WARNING, "refusing to send a requets to add a user to contacts: from and to user are identical! user is " + fromUser);
		} else {
			ContactRelationshipRequested event = new ContactRelationshipRequested();
			event.setFromUser(fromUser);
			event.setToUser(toUser);
			event.setIntroductionText(text);
			event.setRequestDate(new Date());
			emitter.emit(event);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.svend.dab.core.IUserProfileService#cancelRequestToAddToContacts(java .lang.String, java.lang.String)
	 */
	
	public void cancelRequestToAddToContacts(String cancellingUser, String otherUser) {
		if (Strings.isNullOrEmpty(cancellingUser) || Strings.isNullOrEmpty(otherUser)) {
			logger.log(Level.WARNING, "refusing to cancel a relationship requets with a null user. cancellingUser=" + cancellingUser + ", otherUser=" + otherUser);
		} else if (cancellingUser.equals(otherUser)) {
			logger.log(Level.WARNING, "refusing to cancel a relationship request: users are identical. cancellingUser=" + cancellingUser + ", otherUser=" + otherUser);
		} else {
			emitter.emit(new ContactRelationshipResponse(cancellingUser, otherUser, RESPONSE.cancelledByRequestor));
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.svend.dab.core.IUserProfileService#rejectRequestToAddToContacts(java .lang.String, java.lang.String)
	 */
	
	public void rejectRequestToAddToContacts(String rejectingUserName, String requestingContactUserName) {
		if (Strings.isNullOrEmpty(rejectingUserName) || Strings.isNullOrEmpty(requestingContactUserName)) {
			logger.log(Level.WARNING, "refusing to reject a relationship requets with a null user. rejectingUserName=" + rejectingUserName + ", requestingContactUserName=" + requestingContactUserName);
		} else if (rejectingUserName.equals(requestingContactUserName)) {
			logger.log(Level.WARNING, "refusing to reject a relationship request: users are identical. rejectingUserName=" + rejectingUserName + ", requestingContactUserName="
					+ requestingContactUserName);
		} else {
			emitter.emit(new ContactRelationshipResponse(requestingContactUserName, rejectingUserName, RESPONSE.rejectedByRecipient));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.svend.dab.core.IUserProfileService#acceptRequestToAddToContacts(java .lang.String, java.lang.String)
	 */
	
	public void acceptRequestToAddToContacts(String accepting, String acceptedContactUserName) {
		if (Strings.isNullOrEmpty(accepting) || Strings.isNullOrEmpty(acceptedContactUserName)) {
			logger.log(Level.WARNING, "refusing to accept a relationship requets with a null user. accepting=" + accepting + ", acceptedContactUserName=" + acceptedContactUserName);
		} else if (accepting.equals(acceptedContactUserName)) {
			logger.log(Level.WARNING, "refusing to accept a relationship request: users are identical. accepting=" + accepting + ", acceptedContactUserName=" + acceptedContactUserName);
		} else {
			emitter.emit(new ContactRelationshipResponse(acceptedContactUserName, accepting, RESPONSE.approvedByRecipient));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.svend.dab.core.IUserProfileService#removeContactFromProfile(java. lang.String, java.lang.String)
	 */
	
	public void removeContactFromProfile(String editedProfileUsername, String removedUserName) {
		if (Strings.isNullOrEmpty(editedProfileUsername) || Strings.isNullOrEmpty(removedUserName)) {
			logger.log(Level.WARNING, "refusing to remove a contact with a null user. editedProfileUsername=" + editedProfileUsername + ", removedUserName=" + removedUserName);
		} else if (editedProfileUsername.equals(removedUserName)) {
			logger.log(Level.WARNING, "refusing to remove a contact: users are identical. editedProfileUsername=" + editedProfileUsername + ", removedUserName=" + removedUserName);
		} else {
			emitter.emit(new ContactRelationshipRemoved(editedProfileUsername, removedUserName));
		}

	}

	
	public void updatePhotoGallery(UserProfile profile, boolean hasMainPhotoChanged) {
		userProfileRepo.updatePhotoGallery(profile);
		if (hasMainPhotoChanged) {
			emitter.emit(new UserSummaryUpdated(new UserSummary(profile)));
		}
	}
	
	

	
	public List<String> determineNonExistingAnyContact(String userId, List<String> knownPendingReceivedIds, List<String> knownPendingSentIds, List<String> knownContactsIds) {

		List<String> removedRefs = new LinkedList<String>();
		UserProfile userProfile = loadUserProfile(userId, false);

		if (userProfile == null) {
			logger.log(Level.WARNING, "Could determing removed contact of non existing user: " + removedRefs);
		} else {
			for (String knownRef : knownPendingReceivedIds) {
				if (!userProfile.getPendingReceivedContactRequestIds().contains(knownRef)) {
					removedRefs.add(knownRef);
				}
			}
			for (String knownRef : knownPendingSentIds) {
				if (!userProfile.getPendingSentContactRequestIds().contains(knownRef)) {
					removedRefs.add(knownRef);
				}
			}
			for (String knownRef : knownContactsIds) {
				if (!userProfile.getContactIds().contains(knownRef)) {
					removedRefs.add(knownRef);
				}
			}
			
		}
		
		return removedRefs;
		
		
	}

	
	public List<Contact> getOtherReceivedPendingContactRequestsThan(String userId, Set<String> knownContacts) {
		
		List<Contact> newContacts = new LinkedList<Contact>();
		
		UserProfile userProfile = loadUserProfile(userId, false);
		
		if (userProfile == null) {
			logger.log(Level.WARNING, "Could determing new contact of non existing user: " + userId);
		} else {

			for (Contact contact : userProfile.getPendingReceivedContactRequests()) {
				if (!knownContacts.contains(contact.getContactId())) {
					newContacts.add(contact);
				}
			}
		}
		
		return newContacts;
	}

	
	public List<Contact> getOtherConfirmedCo1ntactsThan(String userId, Set<String> contactsToDiscard) {
		List<Contact> newContacts = new LinkedList<Contact>();
		
		UserProfile userProfile = loadUserProfile(userId, false);
		
		if (userProfile == null) {
			logger.log(Level.WARNING, "Could determing new contact of non existing user: " + userId);
		} else {
			
			for (Contact contact : userProfile.getMyActiveContacts()) {
				if (!contactsToDiscard.contains(contact.getContactId())) {
					newContacts.add(contact);
				}
			}
		}
		
		return newContacts;
	}

}