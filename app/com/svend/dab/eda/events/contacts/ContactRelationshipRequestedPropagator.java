package com.svend.dab.eda.events.contacts;

import java.util.List;
import java.util.Set;
import java.util.UUID;
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
import com.svend.dab.core.beans.profile.UserSummary;
import com.svend.dab.core.dao.IContactDao;
import com.svend.dab.core.dao.IUserProfileDao;
import com.svend.dab.eda.IEventPropagator;

@Component
public class ContactRelationshipRequestedPropagator implements IEventPropagator<ContactRelationshipRequested> {

	private static Logger logger = Logger.getLogger(ContactRelationshipRequestedPropagator.class.getName());

	@Autowired
	private Validator validator;

	@Autowired
	private IContactDao contactDao;

	@Autowired
	private IUserProfileDao userProfileRepo;

	public void propagate(ContactRelationshipRequested event) throws DabException {
		
		if (event == null) {
			throw new DabIllegalFormatException("Cannot propagate a null request for contact.");
		}

		Set<ConstraintViolation<ContactRelationshipRequested>> violations = validator.validate(event);

		if (violations.isEmpty()) {
			
			logger.log(Level.INFO, "validation ok " );

			UserProfile fromUser = userProfileRepo.retrieveUserProfileById(event.getFromUser());
			UserProfile toUser = userProfileRepo.retrieveUserProfileById(event.getToUser());

			logger.log(Level.INFO, "fromUser: "  + fromUser);
			logger.log(Level.INFO, "toUser: "  + toUser);
			
			
			if (fromUser == null) {
				throw new DabPreConditionViolationException("cannot propagate a contact request: fromUser does not (or no longer) exist: " +event.getFromUser());
			}

			if (toUser == null) {
				throw new DabPreConditionViolationException("cannot propagate a contact request: toUser does not (or no longer) exist: " + event.getToUser());
			}

			List<Contact> existingContacts = contactDao.findContactByBothUsers(event.getFromUser(), event.getToUser());

			if (existingContacts == null || existingContacts.isEmpty()) {
				
				logger.log(Level.INFO, "contacts " );
				
				Contact createdPendingContact = new Contact();
				createdPendingContact.setContactId(UUID.randomUUID().toString());
				createdPendingContact.setRequestDate(event.getRequestDate());
				createdPendingContact.setInvitationText(event.getIntroductionText());
				createdPendingContact.setStatus(STATUS.pending);
				createdPendingContact.setRequestedByUser(new UserSummary(fromUser));
				createdPendingContact.setRequestedToUser(new UserSummary(toUser));

				contactDao.save(createdPendingContact);
				userProfileRepo.addPendingSentContactRequest(fromUser, createdPendingContact);
				userProfileRepo.addPendingReceivedContactRequest(toUser, createdPendingContact);

			} else if (existingContacts != null && existingContacts.size() == 1) {
				logger.log(Level.WARNING, "found one existing contact relationship between users " + fromUser + " and " + toUser
						+ " => not added a request");
				
				Contact existingContact = existingContacts.get(0);
				if (existingContact.isPending() ) {
					logger.log(Level.WARNING, "...But it's in pending status => at least this is coherent. Let's update the profiles just in case that part failed during the last attempts...");

					if (!fromUser.hasSentAContactRequestTo(toUser.getUsername())) {
						userProfileRepo.addPendingSentContactRequest(fromUser, existingContact);
					}

					if (!toUser.hasReceivedAContactRequestFrom(fromUser.getUsername())) {
						userProfileRepo.addPendingReceivedContactRequest(toUser, existingContact);
					}
					
					
					logger.log(Level.WARNING, "update of the profiles "  + fromUser + " and " + toUser + "done.");
				} else {
					throw new DabPreConditionViolationException("There already exists exactly one contact relationship between users " + fromUser + " and " + toUser
							+ " and its status is not pending => not added a request");
					
				}
			} else {

				throw new DabPreConditionViolationException("There already exists a contact relationship between users " + fromUser + " and " + toUser
						+ " => not added a request");
			}

		} else {

			StringBuffer errorMsg = new StringBuffer();
			for (ConstraintViolation<ContactRelationshipRequested> violation : violations) {
				errorMsg.append(violation.getMessage());
			}

			throw new DabIllegalFormatException("Cannot propagate a request for contact: Invalid: " + errorMsg);
		}

	}

}
