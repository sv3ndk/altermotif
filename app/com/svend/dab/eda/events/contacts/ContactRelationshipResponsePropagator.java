package com.svend.dab.eda.events.contacts;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.svend.dab.core.beans.DabException;
import com.svend.dab.core.beans.DabIllegalFormatException;
import com.svend.dab.core.beans.DabPreConditionViolationException;
import com.svend.dab.core.beans.profile.Contact;
import com.svend.dab.core.beans.profile.Contact.STATUS;
import com.svend.dab.core.beans.profile.UserProfile;
import com.svend.dab.core.dao.IContactDao;
import com.svend.dab.dao.mongo.IUserProfileDao;
import com.svend.dab.eda.IEventPropagator;
import com.svend.dab.eda.events.contacts.ContactRelationshipResponse.RESPONSE;

/**
 * 
 * TODO: actually, some of the failed preconditions here could happen because the event arrive too early, there should be a way to retry it a limited amount of
 * times...
 * 
 * @author svend
 * 
 */
@Component
public class ContactRelationshipResponsePropagator implements IEventPropagator<ContactRelationshipResponse> {

	private static Logger logger = Logger.getLogger(ContactRelationshipResponsePropagator.class.getName());

	@Autowired
	private Validator validator;

	@Autowired
	private IContactDao contactDao;

	@Autowired
	private IUserProfileDao userProfileRepo;

	// ------------------------------------------------
	// ------------------------------------------------

	@Override
	public void propagate(ContactRelationshipResponse event) throws DabException {

		if (event == null) {
			throw new DabIllegalFormatException("Cannot propagate a null request for contact.");
		}

		Set<ConstraintViolation<ContactRelationshipResponse>> violations = validator.validate(event);

		if (violations.isEmpty()) {

			UserProfile fromUser = userProfileRepo.findOne(event.getFromUser());
			if (fromUser == null) {
				throw new DabPreConditionViolationException("cannot propagate a contact response: fromUser '"+ event.getFromUser() + "' does not (or no longer) exist");
			}

			UserProfile toUser = userProfileRepo.findOne(event.getToUser());
			if (toUser == null) {
				throw new DabPreConditionViolationException("cannot propagate a contact response: toUser '" + event.getToUser() + "' does not (or no longer) exist");
			}

			List<Contact> existingContacts = contactDao.findContactByBothUsers(event.getFromUser(), event.getToUser());

			if (existingContacts == null || existingContacts.isEmpty()) {
				throw new DabPreConditionViolationException("There isn't any existing contact relationship between users " + fromUser.getUsername() + " and " + toUser.getUsername()
						+ " => cannot propagate response");
			} else if (existingContacts != null && existingContacts.size() == 1) {
				Contact existingContact = existingContacts.get(0);

				if (existingContact.getStatus() == STATUS.pending) {
					propagate_safe(existingContact, event, fromUser, toUser);
				} else {
					// TODO: there should be a way to limit the number of such retries...
					logger.log(Level.WARNING, "exiting contact relationship between  " + fromUser + " and " + toUser
							+ " is not in pending state... maybe there has been a previous attempt that failed to propagate... let's try again then... Status is " + existingContact.getStatus());
					propagate_safe(existingContact, event, fromUser, toUser);
				}

			} else {
				throw new DabPreConditionViolationException("There exist several a contact relationships between users " + fromUser.getUsername() + " and " + toUser.getUsername()
						+ " => cannot propagate response");
			}

		} else {

			StringBuffer errorMsg = new StringBuffer();
			for (ConstraintViolation<ContactRelationshipResponse> violation : violations) {
				errorMsg.append(violation.getMessage());
			}

			throw new DabIllegalFormatException("Cannot propagate a response for contact request: Invalid: " + errorMsg);
		}

	}

	/**
	 * safe version of the "propagate": this assumes all preconditions have been successfully checked by the caller => no NPTR check here
	 * 
	 * @param event
	 * @param fromUser
	 * @param toUser
	 */
	protected void propagate_safe(Contact existingContact, ContactRelationshipResponse event, UserProfile fromUser, UserProfile toUser) {
		if (event.getResponse() == RESPONSE.cancelledByRequestor || event.getResponse() == RESPONSE.rejectedByRecipient) {
			removeContact_safe(existingContact, fromUser, toUser);
		} else {
			confirmContact_safe(existingContact, fromUser, toUser);
		}
	}

	/**
	 * @param existingContact
	 * @param fromUser
	 * @param toUser
	 */
	protected void confirmContact_safe(Contact existingContact, UserProfile fromUser, UserProfile toUser) {
		userProfileRepo.removePendingSentRelationship(fromUser, toUser.getUsername());
		userProfileRepo.removedPendingReceivedRelationship(toUser, fromUser.getUsername());
		
		existingContact.setStatus(STATUS.accepted);
		userProfileRepo.addConfirmedContact(fromUser, existingContact);
		userProfileRepo.addConfirmedContact(toUser, existingContact);
		
		// saving the contact last, as this is what determines the real status => at the end of the non atomic sent of update operations
		contactDao.save(existingContact);
	}

	/**
	 * @param existingContact
	 * @param fromUser
	 * @param toUser
	 */
	protected void removeContact_safe(Contact existingContact, UserProfile fromUser, UserProfile toUser) {
		userProfileRepo.removePendingSentRelationship(fromUser, toUser.getUsername());
		userProfileRepo.removedPendingReceivedRelationship(toUser, fromUser.getUsername());
		contactDao.delete(existingContact);
	}

}
