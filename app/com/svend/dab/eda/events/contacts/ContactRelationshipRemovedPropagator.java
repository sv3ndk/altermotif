package com.svend.dab.eda.events.contacts;

import java.util.List;
import java.util.Set;

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

/**
 * @author svend
 *
 */
@Component
public class ContactRelationshipRemovedPropagator implements IEventPropagator<ContactRelationshipRemoved> {

	@Autowired
	private Validator validator;

	@Autowired
	private IContactDao  contactDao;

	@Autowired
	private IUserProfileDao userProfileRepo;

	
	/* (non-Javadoc)
	 * @see com.svend.dab.eda.IEventPropagator#propagate(com.svend.dab.eda.Event)
	 */
	@Override
	public void propagate(ContactRelationshipRemoved event) throws DabException {
		if (event == null) {
			throw new DabIllegalFormatException("Cannot propagate a null request for removal of contact.");
		}

		Set<ConstraintViolation<ContactRelationshipRemoved>> violations = validator.validate(event);

		if (violations.isEmpty()) {

			UserProfile cancellingUser = userProfileRepo.retrieveUserProfileById(event.getCancellingUser());
			UserProfile otherUser = userProfileRepo.retrieveUserProfileById(event.getOtherUser());

			if (cancellingUser == null) {
				throw new DabPreConditionViolationException("cannot propagate a request for for removal of contact: fromUser does not (or no longer) exist");
			}

			if (otherUser == null) {
				throw new DabPreConditionViolationException("cannot propagate a request for removal of contact: toUser does not (or no longer) exist");
			}

			List<Contact> existingContacts = contactDao.findContactByBothUsers(event.getCancellingUser(), event.getOtherUser());

			if (existingContacts == null || existingContacts.isEmpty()) {
				throw new DabPreConditionViolationException("There isn't any existing contact relationship between users " + cancellingUser.getUsername() + " and " + otherUser.getUsername()
						+ " => cannot propagate removal of contact");
			} else if (existingContacts != null && existingContacts.size() == 1) {
				Contact existingContact = existingContacts.get(0);

				if (existingContact.getStatus() == STATUS.accepted) {
					propagate_safe(existingContact, event, cancellingUser, otherUser);
				} else {
					// TODO: if the retries arrive and succeed out of order, this 
					throw new DabPreConditionViolationException("The existing contact relationship between users " + cancellingUser.getUsername() + " and " + otherUser.getUsername()
							+ " is not in accepted state");
				}

			} else {
				throw new DabPreConditionViolationException("There exist several a contact relationships between users " + cancellingUser.getUsername()  + " and " + otherUser.getUsername()
						+ " => cannot propagate response");
			}

		} else {

			StringBuffer errorMsg = new StringBuffer();
			for (ConstraintViolation<ContactRelationshipRemoved> violation : violations) {
				errorMsg.append(violation.getMessage());
			}

			throw new DabIllegalFormatException("cannot propagate a request for for removal of contact: Invalid: " + errorMsg);
		}
	}


	/**
	 * @param existingContact
	 * @param event
	 * @param cancellingUser
	 * @param otherUser
	 */
	private void propagate_safe(Contact existingContact, ContactRelationshipRemoved event, UserProfile cancellingUser, UserProfile otherUser) {
		
		userProfileRepo.removeConfirmedContact(cancellingUser, otherUser.getUsername());
		userProfileRepo.removeConfirmedContact(otherUser, cancellingUser.getUsername());
		contactDao.delete(existingContact);
	}

}
