package com.svend.dab.dao.mongo;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.mongodb.WriteResult;
import com.svend.dab.core.beans.profile.Contact;
import com.svend.dab.core.beans.profile.PersonalData;
import com.svend.dab.core.beans.profile.PrivacySettings;
import com.svend.dab.core.beans.profile.UserProfile;
import com.svend.dab.core.beans.profile.UserReference;
import com.svend.dab.core.beans.profile.UserSummary;
import com.svend.dab.core.beans.projects.Participation;
import com.svend.dab.core.beans.projects.Project;

@Service
public class UserProfileRepoImpl implements IUserProfileDao {

	@Autowired
	private MongoTemplate mongoTemplate;

	private static Logger logger = Logger.getLogger(UserProfileRepoImpl.class.getName());

	@Override
	public List<UserProfile> retrieveUserProfilesById(List<String> ids) {

		// for some reason this does not work...
		// Query theQuery = query(where("id").in(ids));

		List<Criteria> criterias = new LinkedList<Criteria>();

		for (String id : ids) {
			criterias.add(where("username").is(id));
		}

		Query theQuery = query(new Criteria().orOperator(criterias.toArray(new Criteria[] {})));
		return mongoTemplate.find(theQuery, UserProfile.class);
	}

	@Override
	public void replacePrivacySettings(String updatedUserId, PrivacySettings newPrivacySettings) {
		genericUpdateUser(updatedUserId, new Update().set("privacySettings", newPrivacySettings));
	}

	@Override
	public void replaceLatestLoginAndPermKey(String updatedUserId, String uploadPermKey, Date dateOfLoggin) {
		genericUpdateUser(updatedUserId, new Update().set("uploadPermKey", uploadPermKey).set("dateOfLatestLogin", dateOfLoggin));
	}

	@Override
	public void replacePersonalData(UserProfile updatedUser, PersonalData pData) {
		genericUpdateUser(updatedUser.getUsername(), new Update().set("pdata", pData));
	}

	@Override
	public void updatePhotoGallery(UserProfile userProfile) {
		genericUpdateUser(userProfile.getUsername(), new Update().set("photos", userProfile.getPhotos()));
	}

	@Override
	public void updateCv(UserProfile userProfile) {
		genericUpdateUser(userProfile.getUsername(), new Update().set("cvLink", userProfile.getCvLink()));
	}

	@Override
	public void addPendingSentContactRequest(UserProfile userProfile, Contact pendingContact) {
		Contact existingSentContact = userProfile.retrieveSentContactRequestTo(pendingContact.getOtherUser(userProfile.getUsername()).getUserName());
		if (existingSentContact == null) {
			genericUpdateUser(userProfile.getUsername(), new Update().addToSet("pendingSentContactRequests", pendingContact));
		}
	}

	@Override
	public void addPendingReceivedContactRequest(UserProfile userProfile, Contact pendingContact) {
		Contact existingReceivedContact = userProfile.retrieveReceivedContactRequestFrom(pendingContact.getOtherUser(userProfile.getUsername()).getUserName());
		if (existingReceivedContact == null) {
			genericUpdateUser(userProfile.getUsername(), new Update().addToSet("pendingReceivedContactRequests", pendingContact));
		}
	}

	@Override
	public void addConfirmedContact(UserProfile userProfile, Contact contact) {
		Contact existingContact = userProfile.retrieveConfirmedContactWith(contact.getOtherUser(userProfile.getUsername()).getUserName());
		if (existingContact == null) {
			genericUpdateUser(userProfile.getUsername(), new Update().addToSet("contacts", contact));
		}
	}

	@Override
	public void removePendingSentRelationship(UserProfile userProfile, String otherUsername) {
		Contact pendingSentReContact = userProfile.retrieveSentContactRequestTo(otherUsername);
		if (pendingSentReContact != null) {
			genericUpdateUser(userProfile.getUsername(), new Update().pull("pendingSentContactRequests", new Contact(pendingSentReContact.getContactId())));
		}
	}

	@Override
	public void removedPendingReceivedRelationship(UserProfile userProfile, String otherUsername) {
		Contact pendingReceivedReContact = userProfile.retrieveReceivedContactRequestFrom(otherUsername);
		if (pendingReceivedReContact != null) {
			genericUpdateUser(userProfile.getUsername(), new Update().pull("pendingReceivedContactRequests", new Contact(pendingReceivedReContact.getContactId())));
		} 
	}

	@Override
	public void removeConfirmedContact(UserProfile userProfile, String otherUsername) {
		Contact confirmedContact = userProfile.retrieveConfirmedContactWith(otherUsername);
		if (confirmedContact != null) {
			genericUpdateUser(userProfile.getUsername(), new Update().pull("contacts", new Contact(confirmedContact.getContactId())));
		}
	}

	@Override
	public void updateContact(String updatedUserId, Contact contact, UserSummary updatedUserSummary, boolean updateRequestor) {
		Query query = query(where("username").is(updatedUserId).and("contacts._id").is(contact.getContactId()));
		if (updateRequestor) {
			mongoTemplate.updateFirst(query, new Update().set("contacts.$.requestedByUser", updatedUserSummary), UserProfile.class);
		} else {
			mongoTemplate.updateFirst(query, new Update().set("contacts.$.requestedToUser", updatedUserSummary), UserProfile.class);
		}
	}
	
	
	@Override
	public void updatePendingSentContactRequests(String updatedUserId, Contact contact, UserSummary updatedUserSummary, boolean updateRequestor) {
		logger.log(Level.INFO, "updatePendingSentContactRequests: updatedUserId=" + updatedUserId + " summary: " + updatedUserSummary + " updaterequestor: " + updateRequestor + " contact id: " + contact.getContactId());
		
		Query query = query(where("username").is(updatedUserId).and("pendingSentContactRequests._id").is(contact.getContactId()));
		if (updateRequestor) {
			mongoTemplate.updateFirst(query, new Update().set("pendingSentContactRequests.$.requestedByUser", updatedUserSummary), UserProfile.class);
		} else {
			mongoTemplate.updateFirst(query, new Update().set("pendingSentContactRequests.$.requestedToUser", updatedUserSummary), UserProfile.class);
		}
	}

	@Override
	public void updatePendingReceivedContactRequests(String updatedUserId, Contact contact, UserSummary updatedUserSummary, boolean updateRequestor) {
		
		logger.log(Level.INFO, "updatePendingReceivedContactRequests: updatedUserId=" + updatedUserId + " summary: " + updatedUserSummary + " updaterequestor: " + updateRequestor + " contact id: " + contact.getContactId());
		
		Query query = query(where("username").is(updatedUserId).and("pendingReceivedContactRequests._id").is(contact.getContactId()));
		if (updateRequestor) {
			mongoTemplate.updateFirst(query, new Update().set("pendingReceivedContactRequests.$.requestedByUser", updatedUserSummary), UserProfile.class);
		} else {
			mongoTemplate.updateFirst(query, new Update().set("pendingReceivedContactRequests.$.requestedToUser", updatedUserSummary), UserProfile.class);
		}
	}


	// -------------------------------
	// references

	@Override
	public void addReceivedReference(UserProfile userProfile, UserReference createdReference) {
		genericUpdateUser(userProfile.getUsername(), new Update().addToSet("receivedReferences", createdReference));
	}

	@Override
	public void addWrittenReference(UserProfile userProfile, UserReference createdReference) {
		genericUpdateUser(userProfile.getUsername(), new Update().addToSet("writtenReferences", createdReference));
	}

	@Override
	public void removeWrittenReference(UserProfile userProfile, String referenceId) {
		UserReference reference = userProfile.retrieveWrittenReference(referenceId);
		logger.log(Level.INFO, "removeWrittenReference, reference is " + reference);
		if (reference != null) {
			genericUpdateUser(userProfile.getUsername(), new Update().pull("writtenReferences", new UserReference(reference.getId())));
		}
	}

	@Override
	public void removeReceivedReference(UserProfile userProfile, String referenceId) {
		UserReference reference = userProfile.retrieveReceivedReference(referenceId);
		logger.log(Level.INFO, "removeReceivedReference, reference is " + reference);
		if (reference != null) {
			genericUpdateUser(userProfile.getUsername(), new Update().pull("receivedReferences", new UserReference(reference.getId())));
		}
	}

	@Override
	public void updateWrittenReferenceFromUser(String updatedUserId, UserReference ref, UserSummary updatedUserSummary) {
		Query query = query(where("username").is(updatedUserId).and("writtenReferences._id").is(ref.getId()));
		Update update = new Update().set("writtenReferences.$.fromUser", updatedUserSummary);
		mongoTemplate.updateFirst(query, update, UserProfile.class);
	}

	@Override
	public void updateReceivedReferenceFromUser(String updatedUserId, UserReference ref, UserSummary updatedUserSummary) {
		Query query = query(where("username").is(updatedUserId).and("receivedReferences._id").is(ref.getId()));
		Update update = new Update().set("receivedReferences.$.fromUser", updatedUserSummary);
		mongoTemplate.updateFirst(query, update, UserProfile.class);
	}

	@Override
	public void updateReceivedReferenceToUser(String updatedUserId, UserReference ref, UserSummary updatedUserSummary) {
		Query query = query(where("username").is(updatedUserId).and("receivedReferences._id").is(ref.getId()));
		Update update = new Update().set("receivedReferences.$.toUser", updatedUserSummary);
		mongoTemplate.updateFirst(query, update, UserProfile.class);
	}

	@Override
	public void updateWrittenReferenceToUser(String updatedUserId, UserReference ref, UserSummary updatedUserSummary) {
		Query query = query(where("username").is(updatedUserId).and("writtenReferences._id").is(ref.getId()));
		Update update = new Update().set("writtenReferences.$.toUser", updatedUserSummary);
		mongoTemplate.updateFirst(query, update, UserProfile.class);
	}
	
	


	
	// ----------------------------------------
	// projects
	
	@Override
	public void addProjectParticipation(UserProfile userProfile, Participation part) {
		Participation existingParticipation = userProfile.retrieveParticipation(part);
		if (existingParticipation == null) {
			genericUpdateUser(userProfile.getUsername(), new Update().addToSet("projects", part));
		}		
		
	}

	
	
	// -------------------------------------
	// -------------------------------------

	/**
	 * Applies this update operation to this user
	 * 
	 * @param username
	 * @param update
	 */
	protected void genericUpdateUser(String username, Update update) {
		Query query = query(where("username").is(username));
		mongoTemplate.updateFirst(query, update, UserProfile.class);
	}

	@Override
	public UserProfile findOne(String userId) {
		// TODO Auto-generated method stub
		return mongoTemplate.findOne(query(where("username").is(userId)), UserProfile.class);
	}

	@Override
	public void save(UserProfile createdUserProfile) {
		mongoTemplate.save(createdUserProfile);
	}

	


}
