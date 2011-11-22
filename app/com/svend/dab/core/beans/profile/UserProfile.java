package com.svend.dab.core.beans.profile;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import com.svend.dab.core.beans.aws.S3Link;
import com.svend.dab.core.beans.profile.PrivacySettings.VISIBILITY;
import com.svend.dab.core.beans.projects.Participation;
import com.svend.dab.dao.aws.s3.AwsS3CvDao;

/**
 * @author Svend
 * 
 */
public class UserProfile implements Serializable {

	private static final long serialVersionUID = -6374495407099516286L;

	private static Logger logger = Logger.getLogger(UserProfile.class.getName());

	// ------------------------------------------------
	// ------------------------------------------------

	@Id
	private String username;

	private PersonalData pdata;

	private PrivacySettings privacySettings;

	private List<Photo> photos;

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
	private List<String> pendingReceivedContactRequestsIds ;
	
	@Transient
	private List<String> pendingSentContactRequestsIds ;
	
	@Transient
	private List<String> contactIds ;

	private List<Contact> contacts = new LinkedList<Contact>();

	private List<Participation> projects = new LinkedList<Participation>();

	@Transient
	private List<OwnerAwareContact> myActiveContacts;

	private Date dateOfLatestLogin;

	// -----------------------------------
	//

	// this is re-initialized on each login: this is communicated to and checked by the upload servlet
	// => this should prevent malicious upload of binaries to other people's profile
	private String uploadPermKey;

	// ----------------------------------
	// Constructors

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "[profile: username= " + username + "]";
	}

	public UserProfile() {
		super();
	}

	/**
	 * Copy constructore (not coying photos nor contact nor references)
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

	/**
	 * @param userName
	 * @param password
	 * @param firstName
	 * @param email
	 * @param gender
	 * @param lastName
	 * @param location
	 * @param locationLat
	 * @param locationLong
	 * @param website
	 * @param personalObjective
	 * @param personalDescription
	 * @param personalPhilosophy
	 * @param personalAssets
	 */
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

	public Long getNumberOfProjects() {
		
		if (projects == null) {
			return 0L;
		} else {
			return Long.valueOf(getProjects().size());
		}
		
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

	/**
	 * @param photoContentStream
	 * @param detectedMimeType
	 * @param length
	 * @return
	 */
	public Photo createOnePhotoPlaceholder() {

		String photoId = UUID.randomUUID().toString().replace("-", "");

		S3Link normalPhotoLink = new S3Link();
		normalPhotoLink.setS3Key("/profiles/" + username + "/photos/" + photoId);
		normalPhotoLink.setS3BucketName(AwsS3CvDao.DEFAULT_S3_BUCKET);

		S3Link thumbPhotoLink = new S3Link();
		thumbPhotoLink.setS3Key("/profiles/" + username + "/thumbs/" + photoId);
		thumbPhotoLink.setS3BucketName(AwsS3CvDao.DEFAULT_S3_BUCKET);

		Photo added = new Photo("", normalPhotoLink, thumbPhotoLink);

		if (photos == null) {
			photos = new LinkedList<Photo>();
		}

		photos.add(added);

		return added;
	}

	/**
	 * @param expirationdate
	 */
	public void generatephotoLinks(Date expirationdate) {
		if (photos != null) {
			for (Photo photo : photos) {
				photo.generatePresignedLinks(expirationdate, true, true);
			}
		}

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

	}
	

	/**
	 * @param deletedPhotoIdx
	 */
	public Photo removePhoto(int photoIndex) {
		if (photoIndex >= 0 && photos != null && photoIndex < photos.size()) {
			return photos.remove(photoIndex);
		} else {
			logger.log(Level.WARNING, "Not removing photo from profile: invalid index: " + photoIndex);
			return null;
		}
	}

	/**
	 * @return true if the link "more photos" should be displayed for this profile
	 */
	public boolean hasMoreThanOnePhoto() {
		return photos != null && photos.size() > 1;
	}

	/**
	 * @return
	 */
	public Photo getMainPhoto() {

		if (photos != null && !photos.isEmpty()) {
			return photos.get(0);
		}

		return new Photo();
	}

	/**
	 * @param photoIndex
	 */
	public void movePhotoToFirstPosition(int photoIndex) {
		if (photoIndex >= 0 && photos != null && photoIndex < photos.size()) {

			if (photoIndex != 0) {
				Photo moved = photos.remove(photoIndex);
				photos.add(0, moved);
			}

		} else {
			logger.log(Level.WARNING, "Not moving photo: index is out of bound of we have null photo list. index was: " + photoIndex);
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

		logger.log(Level.INFO, "retrieveReceivedReference " + referenceId);

		if (receivedReferences != null && referenceId != null) {

			for (UserReference ref : receivedReferences) {
				if (referenceId.equals(ref.getId())) {
					return ref;
				} else {
					logger.log(Level.INFO, "different: " + ref.getId());
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

	public List<Photo> getPhotos() {
		return photos;
	}

	public void setPhotos(List<Photo> photos) {
		this.photos = photos;
	}

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


}