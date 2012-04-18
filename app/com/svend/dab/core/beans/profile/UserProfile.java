package com.svend.dab.core.beans.profile;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import com.google.common.base.Strings;
import com.svend.dab.core.beans.PhotoAlbum;
import com.svend.dab.core.beans.aws.S3Link;
import com.svend.dab.core.beans.groups.GroupParticipation;
import com.svend.dab.core.beans.profile.PrivacySettings.VISIBILITY;
import com.svend.dab.core.beans.projects.Participant.ROLE;
import com.svend.dab.core.beans.projects.Participation;
import com.svend.dab.core.beans.projects.Project.STATUS;

import controllers.BeanProvider;

/**
 * @author Svend
 * 
 */
public class UserProfile implements Serializable {

	private static final long serialVersionUID = -6374495407099516286L;

	private static final String DEFAULT_PROFILE_IMAGE = "/defaultProfilePicture.png";

	private static Logger logger = Logger.getLogger(UserProfile.class.getName());

	// ------------------------------------------------
	// ------------------------------------------------

	@Id
	private String username;

	private PersonalData pdata;

	private PrivacySettings privacySettings;

	// TODO: remove this (after deployments...)
	private List<Photo> photos;

	private PhotoAlbum photoAlbum;

	private List<UserReference> writtenReferences = new LinkedList<UserReference>();

	private List<UserReference> receivedReferences = new LinkedList<UserReference>();

	// no idea why, but if I set a super type here, the members of the sub types are not marshalled
	private S3Link cvLink;

	// the single source of truth here is the contact mongo collection. The three list below are just a replication of this
	// the Contac instances here always contain a list of only one user, which is the "other" side of the relationship (no need to repeat the current profile
	// here)
	private List<Contact> pendingSentContactRequests = new LinkedList<Contact>();
	private List<Contact> pendingReceivedContactRequests = new LinkedList<Contact>();

	@Transient
	private List<String> pendingReceivedContactRequestsIds;

	@Transient
	private List<String> pendingSentContactRequestsIds;

	@Transient
	private List<String> contactIds;

	private List<Contact> contacts = new LinkedList<Contact>();

	private List<Participation> projects = new LinkedList<Participation>();

	private List<GroupParticipation> groups = new LinkedList<GroupParticipation>();

	@Transient
	private List<Participation> cachedConfirmedProjects;

	@Transient
	private List<Participation> cachedApplication;

	@Transient
	private List<OwnerAwareContact> myActiveContacts;

	@Transient
	private List<UserSummary> myActiveContactsSummaries;

	private Date dateOfLatestLogin;

	// -----------------------------------
	//

	// this is re-initialized on each login: this is communicated to and checked by the upload servlet
	// => this should prevent malicious upload of binaries to other people's profile
	// TODO: I think this can be removed now...
	private String uploadPermKey;

	// ----------------------------------
	// Constructors

	@Override
	public String toString() {
		return "[profile: username= " + username + "]";
	}

	public UserProfile() {
		super();
	}

	/**
	 * Copy constructor (not coying photos nor contact nor references)
	 * 
	 * @param copied
	 */
	public UserProfile(UserProfile copied) {
		if (copied != null) {

			if (copied.pdata == null) {
				this.pdata = new PersonalData();
			} else {

				this.pdata = new PersonalData(copied.getPdata());
			}

			// String are immutable in Java => shallow copy is ok for now
			this.uploadPermKey = copied.getUploadPermKey();

		}
	}

	/**
	 * @param copied
	 *            copies the values from "copied" into this
	 */
	public void copyFrom(UserProfile copied) {
		if (copied.pdata == null) {
			this.pdata = new PersonalData();
		} else {
			this.pdata = new PersonalData(copied.getPdata());
		}

		this.uploadPermKey = copied.getUploadPermKey();
	}

	public UserProfile(String userName, String password, String firstName, String email, String gender, String lastName, String location, String locationLat,
			String locationLong, String website, String personalObjective, String personalDescription, String personalPhilosophy, String personalAssets) {

		this.pdata = new PersonalData(location, password, firstName, lastName, email, gender, null, null, website, locationLat, locationLong,
				personalObjective, personalDescription, personalPhilosophy, personalAssets);
		this.username = userName;

	}

	// ----------------------------------
	// business logic
	// ----------------------------------

	/**
	 * @return a boolean stating if the user has filled in all the mandatory nifor: firstname, lastname, date of birth and at least one language
	 */
	public boolean isComplete() {
		return pdata != null && pdata.isComplete();
	}

	// -----------------------------------------------------
	// CV

	/**
	 * @param cvExpirationDelayInMillis
	 */
	public void generateCvLink(Date cvExpirationDate) {
		if (cvLink != null) {
			cvLink.generateUrl(cvExpirationDate);
		}
	}

	/**
	 * @return
	 */
	public boolean hasCv() {
		return cvLink != null;
	}

	// -----------------------------------------------------
	// Photos

	public PhotoAlbum getPhotoAlbum() {
		if (photoAlbum == null) {
			synchronized (this) {
				if (photoAlbum == null) {
					photoAlbum = new PhotoAlbum();
				}
			}
		}
		
		// setting the transient properties of the photo album every time
		photoAlbum.setPhotoS3RootFolder("/profiles/" + username + "/photos/");
		photoAlbum.setThumbS3RootFolder("/profiles/" + username + "/thumbs/");
		photoAlbum.setMaxNumberOfPhotos(BeanProvider.getConfig().getMaxNumberOfPhotosInProfile());
		photoAlbum.setDefaultMainPhoto(BeanProvider.getResourceLocator().getDabImagesPath() + DEFAULT_PROFILE_IMAGE);

		return photoAlbum;
	}

	/**
	 * @param expirationdate
	 */
	public void generatephotoLinks(Date expirationdate) {

		getPhotoAlbum().generatePhotoLinks(expirationdate);
		
		if (contacts != null) {
			for (Contact contact : contacts) {
				contact.generatePhotoLink(expirationdate);
			}
		}

		if (pendingSentContactRequests != null) {
			for (Contact sentRequest : pendingSentContactRequests) {
				sentRequest.generatePhotoLink(expirationdate);
			}
		}

		if (pendingReceivedContactRequests != null) {
			for (Contact receivedRequest : pendingReceivedContactRequests) {
				receivedRequest.generatePhotoLink(expirationdate);
			}
		}

		if (receivedReferences != null) {
			for (UserReference reference : receivedReferences) {
				reference.generatePhotoLink(expirationdate);
			}
		}

		if (writtenReferences != null) {
			for (UserReference reference : writtenReferences) {
				reference.generatePhotoLink(expirationdate);
			}
		}

		if (projects != null) {
			for (Participation participation : projects) {
				participation.generatePhotoLink(expirationdate);
			}
		}
		
		if (groups != null) {
			for (GroupParticipation participation : groups) {
				participation.generatePhotoLink(expirationdate);
			}
		}

	}

	// ------------------------------------------------------------
	// references

	/**
	 * This is called from the JSF page, to display the number of written references between parentheses
	 * 
	 * @return
	 */
	public String getNumberOfWrittenReferences() {
		if (writtenReferences == null) {
			return "0";
		}
		return Integer.toString((writtenReferences.size()));
	}

	/**
	 * This is called from the JSF page, to display the number of received references between parentheses
	 * 
	 * @return
	 */
	public String getNumberOfReceivedReferences() {
		if (receivedReferences == null) {
			return "0";
		}
		return Integer.toString((receivedReferences.size()));
	}

	/**
	 * checks if the reference is already in the list of written reference and if not, adds it
	 * 
	 * @param ref
	 */
	public void addWrittenReference(UserReference ref) {
		if (ref != null && ref.getId() != null) {

			boolean alreadyExists = false;

			if (writtenReferences == null) {
				writtenReferences = new LinkedList<UserReference>();
			} else {
				for (UserReference existingRef : writtenReferences) {
					if (ref.getId().equals(existingRef.getId())) {
						alreadyExists = true;
						break;
					}
				}
			}

			if (!alreadyExists) {
				writtenReferences.add(ref);
			}
		}
	}

	public void removeWrittenReference(String removedReferenceId) {
		UserReference removedRef = retrieveWrittenReference(removedReferenceId);
		if (removedRef != null) {
			writtenReferences.remove(removedRef);
		}
	}

	/**
	 * @param referenceId
	 * @return
	 */
	public UserReference retrieveWrittenReference(String referenceId) {
		if (writtenReferences != null && writtenReferences.size() > 0 && referenceId != null) {

			for (UserReference ref : writtenReferences) {
				if (referenceId.equals(ref.getId())) {
					return ref;
				}
			}
		}
		return null;
	}

	/**
	 * @param username2
	 * @return
	 */
	public List<UserReference> findWrittenReferencesToUserName(String toUserName) {
		List<UserReference> response = new LinkedList<UserReference>();

		if (writtenReferences != null && toUserName != null) {
			for (UserReference ref : writtenReferences) {
				if (ref.isWrittenTo(toUserName)) {
					response.add(ref);
				}
			}
		}

		return response;
	}

	/**
	 * checks if this reference is already present in the list of received references and if not, adds it
	 * 
	 * @param ref
	 */
	public void addReceivedReference(UserReference ref) {
		if (ref != null && ref.getId() != null) {

			boolean alreadyExists = false;

			if (receivedReferences == null) {
				receivedReferences = new LinkedList<UserReference>();
			} else {
				for (UserReference existingRef : receivedReferences) {
					if (ref.getId().equals(existingRef.getId())) {
						alreadyExists = true;
						break;
					}
				}
			}

			if (!alreadyExists) {
				receivedReferences.add(ref);
			}
		}
	}

	/**
	 * @param removedReferenceId
	 */
	public void removeReceivedReference(String removedReferenceId) {
		UserReference removedRef = retrieveReceivedReference(removedReferenceId);
		if (removedRef != null) {
			receivedReferences.remove(removedRef);
		}
	}

	/**
	 * @param referenceId
	 * @return
	 */
	public UserReference retrieveReceivedReference(String referenceId) {

		if (receivedReferences != null && referenceId != null) {
			for (UserReference ref : receivedReferences) {
				if (referenceId.equals(ref.getId())) {
					return ref;
				}
			}
		}
		return null;
	}

	/**
	 * @param username2
	 * @return
	 */
	public List<UserReference> findReceivedReferencesFromUserName(String fromUserName) {
		List<UserReference> response = new LinkedList<UserReference>();
		if (receivedReferences != null && fromUserName != null) {
			for (UserReference ref : receivedReferences) {
				if (ref.isWrittenBy(fromUserName)) {
					response.add(ref);
				}
			}
		}
		return response;
	}

	// ------------------------------------------------------------
	// groups

	public GroupParticipation retrieveParticipationInGroup(String groupId) {
		if (groupId == null) {
			return null;
		}

		for (GroupParticipation participation : groups) {
			if (groupId.equals(participation.getGroupSummary().getGroupId())) {
				return participation;
			}
		}
		return null;
	}

	public int getNumberOfGroups() {
		if (groups == null || groups.isEmpty()) {
			return 0;
		} else {
			return groups.size();
		}
	}

	// ------------------------------------------------------------
	// projects

	public Participation retrieveParticipation(Participation part) {
		if (projects != null) {
			for (Participation participation : projects) {
				if (participation.isIn(part.getProjectSummary())) {
					return participation;
				}
			}
		}
		return null;
	}

	// ------------------------------------------------------------
	// contacts

	/**
	 * @return
	 */
	public boolean hasAtLeastOnePendingReceivedContactsRequest() {
		if (pendingReceivedContactRequests == null) {
			return false;
		}

		return !pendingReceivedContactRequests.isEmpty();

	}

	/**
	 * @return
	 */
	public boolean hasAtLeastOnePendingSentContactsRequest() {
		if (pendingSentContactRequests == null) {
			return false;
		}
		return !pendingSentContactRequests.isEmpty();

	}

	/**
	 * @return
	 */
	public boolean hasAtLeastOneContact() {
		if (contacts == null) {
			return false;
		}

		return !contacts.isEmpty();
	}

	/**
	 * @param username
	 * @return
	 */
	public boolean hasUserInContacts(String otherUsername) {
		return retrieveConfirmedContactWith(otherUsername) != null;
	}

	/**
	 * @param otherUsername
	 * @return
	 */
	public Contact retrieveConfirmedContactWith(String otherUsername) {

		if (username == null) {
			return null;
		}

		if (contacts == null || contacts.isEmpty()) {
			return null;
		} else {
			for (Contact contact : contacts) {
				if (contact != null && contact.isInvolving(otherUsername)) {
					return contact;
				}
			}
			return null;
		}
	}

	/**
	 * @param loggedInUserProfileId
	 * @return
	 */
	public boolean hasReceivedAContactRequestFrom(String username) {
		return retrieveReceivedContactRequestFrom(username) != null;
	}

	/**
	 * @param username
	 * @return
	 */
	public Contact retrieveReceivedContactRequestFrom(String username) {
		if (username == null) {
			return null;
		}

		if (pendingReceivedContactRequests == null || pendingReceivedContactRequests.isEmpty()) {
			return null;
		} else {
			for (Contact pendingReceivedContactRequest : pendingReceivedContactRequests) {
				if (pendingReceivedContactRequest != null && pendingReceivedContactRequest.isRequestedBy(username)) {
					return pendingReceivedContactRequest;
				}
			}
			return null;
		}

	}

	/**
	 * @param loggedInUserProfileId
	 * @return
	 */
	public boolean hasSentAContactRequestTo(String otherUsername) {
		return retrieveSentContactRequestTo(otherUsername) != null;
	}

	/**
	 * @param otherUsername
	 * @return
	 */
	public Contact retrieveSentContactRequestTo(String otherUsername) {
		if (otherUsername == null) {
			return null;
		}

		if (pendingSentContactRequests == null || pendingSentContactRequests.isEmpty()) {
			return null;
		} else {
			for (Contact pendingSentContactRequest : pendingSentContactRequests) {
				if (pendingSentContactRequest != null && pendingSentContactRequest.isRequestedTo(otherUsername)) {
					return pendingSentContactRequest;
				}
			}
			return null;
		}

	}

	/**
	 * @param otherUsername
	 */
	public void markPendingRequestedRelationshipAsApproved(String otherUsername) {
		markPendingRelationshipAsApproved(otherUsername, pendingSentContactRequests);
	}

	/**
	 * @param otherUsername
	 */
	public void markPendingReceivedRelationshipAsApproved(String otherUsername) {
		markPendingRelationshipAsApproved(otherUsername, pendingReceivedContactRequests);
	}

	/**
	 * internal logic: marks a pending requested or received invitation as approved
	 * 
	 * 
	 * @param otherUsername
	 * @param pendingContactList
	 */
	protected void markPendingRelationshipAsApproved(String otherUsername, List<Contact> pendingContactList) {
		if (pendingContactList != null && otherUsername != null) {

			Contact matchingContact = null;
			for (Contact pendingSentRequest : pendingContactList) {
				if (pendingSentRequest.isInvolving(otherUsername)) {
					matchingContact = pendingSentRequest;
					break;
				}
			}

			if (matchingContact == null) {
				logger.log(Level.WARNING, "Could not mark relationship as approved: no pending sent relationship found with user " + otherUsername);
			} else {
				pendingContactList.remove(matchingContact);
				contacts.add(matchingContact);
			}
		}
	}

	/**
	 * @param otherUsername
	 */
	public void removePendingSentRelationship(String otherUsername) {
		removeRelationship(otherUsername, pendingSentContactRequests);
	}

	/**
	 * @param otherUsername
	 */
	public void removedPendingReceivedRelationship(String otherUsername) {
		removeRelationship(otherUsername, pendingReceivedContactRequests);
	}

	/**
	 * @param otherUsername
	 */
	public void removeContact(String otherUsername) {
		removeRelationship(otherUsername, contacts);
	}

	/**
	 * 
	 * internal mechanics: remove the relationship with this user from one of the list of relationship
	 * 
	 * 
	 * @param otherUsername
	 * @param pendingContactList
	 */
	protected void removeRelationship(String otherUsername, List<Contact> pendingContactList) {
		if (pendingContactList != null && otherUsername != null) {
			Contact matchingContact = findPendingContactRequestWith(otherUsername, pendingContactList);
			if (matchingContact == null) {
				logger.log(Level.WARNING, "Could not mark remove relationship: no pending relationship found with user " + otherUsername);
			} else {
				pendingContactList.remove(matchingContact);
			}

		}
	}

	/**
	 * @param otherUsername
	 * @return
	 */
	public Contact findContactOrContactRequestWith(String otherUsername) {

		Contact foundContact = findContactWith(otherUsername);
		if (foundContact != null) {
			return foundContact;
		}

		foundContact = findPendingReceivedContactRequestWith(otherUsername);
		if (foundContact != null) {
			return foundContact;
		}
		return findPendingSentContactRequestWith(otherUsername);

	}

	/**
	 * @param otherUser
	 * @return
	 */
	public Contact findPendingReceivedContactRequestWith(String otherUsername) {
		return findPendingContactRequestWith(otherUsername, pendingReceivedContactRequests);
	}

	/**
	 * @param otherUser
	 * @return
	 */
	public Contact findPendingSentContactRequestWith(String otherUsername) {
		return findPendingContactRequestWith(otherUsername, pendingSentContactRequests);
	}

	/**
	 * @param otherUsername
	 * @return
	 */
	public Contact findContactWith(String otherUsername) {
		return findPendingContactRequestWith(otherUsername, contacts);
	}

	/**
	 * @param otherUser
	 * @return
	 */
	protected Contact findPendingContactRequestWith(String otherUsername, List<Contact> contactList) {

		if (otherUsername == null) {
			return null;
		}

		for (Contact contact : contactList) {
			if (contact.isInvolving(otherUsername)) {
				return contact;
			}
		}

		return null;
	}

	// ------------------------------------------
	// privacy settings

	// TODO: move this to a "profile PEP" or somthing
	
	/**
	 * @param loggedInUserProfileId
	 */
	public boolean isAgeVisibleTo(String userId) {
		return allows(getPrivacySettings().getAgeVisibility(), userId);
	}

	public boolean isFirstNameVisibleTo(String userId) {
		return allows(getPrivacySettings().getFirstNameVisibility(), userId);
	}

	public boolean isLastNameVisibleTo(String userId) {
		return allows(getPrivacySettings().getLastNameVisibility(), userId);
	}

	public boolean isCvVisibleTo(String userId) {
		return allows(getPrivacySettings().getCvVisibility(), userId);
	}

	public boolean isPhotoGalleryVisibleTo(String userId) {
		return allows(getPrivacySettings().getPhotosVisibility(), userId);
	}

	protected boolean allows(VISIBILITY visibility, String accessingUserId) {
		switch (visibility) {
		case everybody:
			return true;
		case loggedin:
			return accessingUserId != null;
		case mycontacts:
			return hasUserInContacts(accessingUserId);
		case nobody:
			return false;
		}
		return false;
	}

	// ----------------------------------
	// getters and setters

	public PersonalData getPdata() {
		if (pdata == null) {
			pdata = new PersonalData();
		}
		return pdata;
	}

	public void setPdata(PersonalData pdata) {
		this.pdata = pdata;
	}

	public Date getDateOfLatestLogin() {
		return dateOfLatestLogin;
	}

//	public List<Photo> getPhotos() {
//		return photos;
//	}
//
//	public void setPhotos(List<Photo> photos) {
//		this.photos = photos;
//	}

	public List<UserReference> getWrittenReferences() {
		return writtenReferences;
	}

	public void setWrittenReferences(List<UserReference> writtenReferences) {
		this.writtenReferences = writtenReferences;
	}

	public List<UserReference> getReceivedReferences() {
		return receivedReferences;
	}

	public void setReceivedReferences(List<UserReference> receivedReferences) {
		this.receivedReferences = receivedReferences;
	}

	public String getUploadPermKey() {
		return uploadPermKey;
	}

	public void setUploadPermKey(String uploadPermKey) {
		this.uploadPermKey = uploadPermKey;
	}

	public S3Link getCvLink() {
		return cvLink;
	}

	public void setCvLink(S3Link cvLink) {
		this.cvLink = cvLink;
	}

	public void setDateOfLatestLogin(Date time) {
		this.dateOfLatestLogin = time;

	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String userName) {
		this.username = userName;
	}

	public List<Contact> getContacts() {
		return contacts;
	}

	public List<String> getContactIds() {
		if (contactIds == null) {
			synchronized (this) {
				if (contactIds == null) {
					contactIds = new LinkedList<String>();
					for (Contact contact : getContacts()) {
						contactIds.add(contact.getContactId());
					}
				}

			}
		}
		return contactIds;
	}

	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}

	public List<Contact> getPendingSentContactRequests() {
		return pendingSentContactRequests;
	}

	public List<String> getPendingSentContactRequestIds() {
		if (pendingSentContactRequestsIds == null) {
			synchronized (this) {
				if (pendingSentContactRequestsIds == null) {
					pendingSentContactRequestsIds = new LinkedList<String>();
					for (Contact contact : getPendingSentContactRequests()) {
						pendingSentContactRequestsIds.add(contact.getContactId());
					}
				}

			}
		}
		return pendingSentContactRequestsIds;
	}

	public void setPendingSentContactRequests(List<Contact> pendingSentContactRequests) {
		this.pendingSentContactRequests = pendingSentContactRequests;
	}

	public List<Contact> getPendingReceivedContactRequests() {
		return pendingReceivedContactRequests;
	}

	public List<String> getPendingReceivedContactRequestIds() {
		if (pendingReceivedContactRequestsIds == null) {
			synchronized (this) {
				if (pendingReceivedContactRequestsIds == null) {
					pendingReceivedContactRequestsIds = new LinkedList<String>();
					for (Contact contact : getPendingReceivedContactRequests()) {
						pendingReceivedContactRequestsIds.add(contact.getContactId());
					}
				}

			}
		}
		return pendingReceivedContactRequestsIds;

	}

	public void setPendingReceivedContactRequests(List<Contact> pendingReceivedContactRequests) {
		this.pendingReceivedContactRequests = pendingReceivedContactRequests;
	}

	public PrivacySettings getPrivacySettings() {
		if (privacySettings == null) {
			privacySettings = new PrivacySettings();
		}

		return privacySettings;
	}

	public void setPrivacySettings(PrivacySettings privacySettings) {
		this.privacySettings = privacySettings;
	}

	public List<OwnerAwareContact> getMyActiveContacts() {

		if (myActiveContacts == null) {
			myActiveContacts = new LinkedList<OwnerAwareContact>();
			for (Contact contact : contacts) {
				if (contact.getOtherUser(username).isProfileActive()) {
					myActiveContacts.add(new OwnerAwareContact(contact, getUsername()));
				}
			}
		}

		return myActiveContacts;
	}

	public List<UserSummary> getMyActiveContactsSummaries() {

		if (myActiveContactsSummaries == null) {
			myActiveContactsSummaries = new LinkedList<UserSummary>();
			for (OwnerAwareContact contact : getMyActiveContacts()) {
				myActiveContactsSummaries.add(contact.getOtherUser());
			}
		}

		return myActiveContactsSummaries;

	}

	/**
	 * @return
	 */
	public int getNumberOfActiveContacts() {
		return getMyActiveContacts().size();
	}

	public List<Participation> getProjects() {
		return projects;
	}

	public void setProjects(List<Participation> project) {
		this.projects = project;
	}

	public List<Participation> getConfirmedProjects() {
		if (cachedConfirmedProjects == null) {
			synchronized (this) {
				if (cachedConfirmedProjects == null) {
					cachedConfirmedProjects = new LinkedList<Participation>();
					for (Participation participation : projects) {
						if (participation.isAccepted()) {
							cachedConfirmedProjects.add(participation);
						}
					}
				}
			}
		}
		return cachedConfirmedProjects;
	}

	public List<Participation> getAllActiveProjectsWhereUserIsAdmin(String loggedInUserProfileId) {

		List<Participation> projectsWhereIamAdmin = new LinkedList<Participation>();

		for (Participation participation : projects) {
			if (participation.isAccepted() && participation.getProjectSummary().getStatus() == STATUS.started
					&& (participation.getRole() == ROLE.admin || participation.getRole() == ROLE.initiator)) {
				projectsWhereIamAdmin.add(participation);
			}
		}

		return projectsWhereIamAdmin;
	}

	public List<Participation> getApplications() {
		if (cachedApplication == null) {
			synchronized (this) {
				if (cachedApplication == null) {
					cachedApplication = new LinkedList<Participation>();
					for (Participation participation : projects) {
						if (!participation.isAccepted()) {
							cachedApplication.add(participation);
						}
					}
				}
			}
		}
		return cachedApplication;
	}

	public boolean isMemberOfOrHasAppliedTo(String projectsId) {

		if (Strings.isNullOrEmpty(projectsId)) {
			return false;
		}

		if (projects == null) {
			return false;
		}

		for (Participation participation : projects) {
			if (projectsId.equals(participation.getProjectSummary().getProjectId())) {
				return true;
			}
		}
		return false;
	}

	public boolean isOwnerOfAtLeastOneProject() {

		if (getConfirmedProjects() == null) {
			return false;
		}

		for (Participation participation : getConfirmedProjects()) {
			if (participation.getRole() == ROLE.initiator) {
				return true;
			}
		}

		return false;
	}

	public Long getNumberOfConfirmedProjects() {

		if (getConfirmedProjects() == null) {
			return 0L;
		} else {
			return Long.valueOf(getConfirmedProjects().size());
		}

	}

	public Long getNumberOfApplications() {

		if (getApplications() == null) {
			return 0L;
		} else {
			return Long.valueOf(getApplications().size());
		}

	}

	public Participation getApplication(String projectId) {
		if (Strings.isNullOrEmpty(projectId)) {
			return null;
		}
		for (Participation participation : getApplications()) {
			if (projectId.equals(participation.getProjectSummary().getProjectId())) {
				return participation;
			}
		}
		return null;
	}

	public Participation getProject(String projectId) {
		if (Strings.isNullOrEmpty(projectId)) {
			return null;
		}
		for (Participation project : getProjects()) {
			if (projectId.equals(project.getProjectSummary().getProjectId())) {
				return project;
			}
		}
		return null;
	}

	public ROLE getRoleInProject(String projectId) {

		Participation participation = getProject(projectId);
		if (participation != null) {
			return participation.getRole();
		}

		return null;
	}

	public List<GroupParticipation> getGroups() {
		return groups;
	}

	public void setGroups(List<GroupParticipation> groups) {
		this.groups = groups;
	}

	public List<Photo> getPhotos() {
		return photos;
	}

	public void setPhotos(List<Photo> photos) {
		this.photos = photos;
	}
}