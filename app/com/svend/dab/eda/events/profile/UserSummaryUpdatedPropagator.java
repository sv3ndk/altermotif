/**
 * 
 */
package com.svend.dab.eda.events.profile;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.svend.dab.core.beans.DabException;
import com.svend.dab.core.beans.DabPreConditionViolationException;
import com.svend.dab.core.beans.profile.Contact;
import com.svend.dab.core.beans.profile.UserProfile;
import com.svend.dab.core.beans.profile.UserReference;
import com.svend.dab.core.beans.profile.UserSummary;
import com.svend.dab.core.beans.projects.Participation;
import com.svend.dab.core.dao.IContactDao;
import com.svend.dab.core.dao.IForumPostDao;
import com.svend.dab.core.dao.IProjectDao;
import com.svend.dab.core.dao.IUserProfileDao;
import com.svend.dab.eda.IEventPropagator;

/**
 * @author Svend
 * 
 */
@Component
public class UserSummaryUpdatedPropagator implements IEventPropagator<UserSummaryUpdated> {

	@Autowired
	private IUserProfileDao userProfileRepo;

	@Autowired
	private IContactDao contactDao;

	@Autowired
	private IProjectDao projectDao;

	@Autowired
	private IForumPostDao forumPostDao;

	private static Logger logger = Logger.getLogger(UserSummaryUpdatedPropagator.class.getName());

	public void propagate(UserSummaryUpdated event) throws DabException {

		logger.log(Level.INFO, "propagating user summary updated");

		UserProfile profile = userProfileRepo.retrieveUserProfileById(event.getUpdatedSummary().getUserName());

		if (profile == null) {
			throw new DabPreConditionViolationException("cannot propagate a UserSummaryUpdated event: no profile found in database for username " + event.getUpdatedSummary().getUserName());
		}

		// TODO. all those "replace" stuff can be re-written with a single call to the DAO (see example with propagateUserSummaryToForumPosts)
		propagateUserSummaryToWrittenReferenceds(profile, event.getUpdatedSummary());
		propagateUserSummaryToReceivedReferences(profile, event.getUpdatedSummary());
		propagateUserSummaryToContacts(profile, event.getUpdatedSummary());
		propagateUserSummaryToProject(profile, event.getUpdatedSummary());

		propagateUserSummaryToForumPosts(event.getUpdatedSummary());

	}

	private void propagateUserSummaryToForumPosts(UserSummary updatedSummary) {
		forumPostDao.updateAuthorOfAllPostsFrom(updatedSummary);
	}

	/**
	 * @param profile
	 * @param updatedSummary
	 */
	private void propagateUserSummaryToProject(UserProfile profile, UserSummary updatedSummary) {
		if (profile.getProjects() != null) {
			for (Participation participation : profile.getProjects()) {
				projectDao.updateProjectParticipation(participation.getProjectSummary().getProjectId(), updatedSummary);
			}
		}
	}

	/**
	 * @param newPersonalData
	 * @param profile
	 */
	private void propagateUserSummaryToContacts(UserProfile profile, UserSummary updatedUserSummary) {
		propagateNewPersonalDataToConfirmedContacts(profile, updatedUserSummary);
		propagateNewPersonalDataToPendingRequestedContacts(profile, updatedUserSummary);
		propagateNewPersonalDataToPendingReceivedContacts(profile, updatedUserSummary);
	}

	private void propagateNewPersonalDataToPendingReceivedContacts(UserProfile profile, UserSummary updatedUserSummary) {
		if (profile.getPendingReceivedContactRequests() != null) {
			for (Contact contact : profile.getPendingReceivedContactRequests()) {
				contactDao.updateRequestedToUser(contact, updatedUserSummary);
				userProfileRepo.updatePendingReceivedContactRequests(profile.getUsername(), contact, updatedUserSummary, false);
				userProfileRepo.updatePendingSentContactRequests(contact.getRequestedByUser().getUserName(), contact, updatedUserSummary, false);
			}
		}
	}

	private void propagateNewPersonalDataToPendingRequestedContacts(UserProfile profile, UserSummary updatedUserSummary) {
		if (profile.getPendingSentContactRequests() != null) {
			for (Contact contact : profile.getPendingSentContactRequests()) {
				contactDao.updateRequestedByUser(contact, updatedUserSummary);
				userProfileRepo.updatePendingSentContactRequests(profile.getUsername(), contact, updatedUserSummary, true);
				userProfileRepo.updatePendingReceivedContactRequests(contact.getRequestedToUser().getUserName(), contact, updatedUserSummary, true);
			}
		}
	}

	private void propagateNewPersonalDataToConfirmedContacts(UserProfile profile, UserSummary updatedUserSummary) {
		if (profile.getContacts() != null) {
			for (Contact contact : profile.getContacts()) {
				if (contact.isRequestedBy(profile.getUsername())) {
					contactDao.updateRequestedByUser(contact, updatedUserSummary);
					userProfileRepo.updateContact(profile.getUsername(), contact, updatedUserSummary, true);
					userProfileRepo.updateContact(contact.getRequestedToUser().getUserName(), contact, updatedUserSummary, true);
				} else {
					contactDao.updateRequestedToUser(contact, updatedUserSummary);
					userProfileRepo.updateContact(profile.getUsername(), contact, updatedUserSummary, false);
					userProfileRepo.updateContact(contact.getRequestedByUser().getUserName(), contact, updatedUserSummary, false);
				}
			}
		}
	}

	/**
	 * @param event
	 * @param profile
	 */
	private void propagateUserSummaryToReceivedReferences(UserProfile profile, UserSummary updatedUserSummary) {
		if (profile.getReceivedReferences() != null) {
			for (UserReference ref : profile.getReceivedReferences()) {
				userProfileRepo.updateReceivedReferenceToUser(profile.getUsername(), ref, updatedUserSummary);
				userProfileRepo.updateWrittenReferenceToUser(ref.getFromUser().getUserName(), ref, updatedUserSummary);
			}
		}
	}

	/**
	 * @param newLocation
	 * @param profile
	 */
	private void propagateUserSummaryToWrittenReferenceds(UserProfile profile, UserSummary updatedUserSummary) {

		if (profile.getWrittenReferences() != null) {
			for (UserReference ref : profile.getWrittenReferences()) {
				userProfileRepo.updateWrittenReferenceFromUser(profile.getUsername(), ref, updatedUserSummary);
				userProfileRepo.updateReceivedReferenceFromUser(ref.getToUser().getUserName(), ref, updatedUserSummary);
			}
		}
	}

}
