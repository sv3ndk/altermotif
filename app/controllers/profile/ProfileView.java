package controllers.profile;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import models.altermotif.profile.ProfileViewProfileVisibility;
import models.altermotif.profile.RemoveContactsJsonResponse;
import web.utils.Utils;

import com.svend.dab.core.beans.profile.Contact;
import com.svend.dab.core.beans.profile.UserProfile;
import com.svend.dab.core.beans.profile.UserReference;

import controllers.Application;
import controllers.BeanProvider;
import controllers.DabController;

public class ProfileView extends DabController {

	private static Logger logger = Logger.getLogger(ProfileView.class.getName());

	public static void profileView(String vuser) {

		UserProfile visitedUserProfile = BeanProvider.getUserProfileService().loadUserProfile(vuser, true);
		ProfileViewProfileVisibility visibility = new ProfileViewProfileVisibility(visitedUserProfile, getSessionWrapper());

		// all links to this profile should be disabled anyway. If a use still lands on this page, we redirect him to his own home page
		// (although an inactive user CAN see his own profile...) 
		if (visitedUserProfile == null || (!visitedUserProfile.getPrivacySettings().isProfileActive() && !visibility.isVisitingHisOwnProfile())) {
			Application.index();
		}

		renderArgs.put("visitedUserProfile", visitedUserProfile);
		visibility.putInArgsList(renderArgs);
		Utils.addAllPossibleLanguageNamesToRenderArgs(getSessionWrapper(), renderArgs);
		render();
	}

	// -------------------------------
	// references

	/**
	 * @param createdReferenceText
	 * @param vuser
	 * @return
	 */
	public static String postLeaveAReference(String createdReferenceText, String vuser) {

		if (getSessionWrapper().isLoggedIn()) {
			BeanProvider.getUserProfileService().leaveAReference(getSessionWrapper().getLoggedInUserProfileId(), vuser, createdReferenceText);
		} else {
			logger.log(Level.WARNING, "Cannot leave a reference: no session or currently logged in user is null or with null username (the link should not be clickable in that case...)");
		}
		return "success";
	}

	/**
	 * @param deletedReferenceId
	 * @param vuser
	 * @return
	 */
	public static String postRemoveAReference(String deletedReferenceId, String vuser) {

		if (getSessionWrapper().isLoggedIn()) {
			BeanProvider.getUserProfileService().removeUserReference(deletedReferenceId, getSessionWrapper().getLoggedInUserProfileId(), vuser);
		} else {
			logger.log(Level.WARNING, "Cannot leave a reference: no session or currently logged in user is null (the link should not be clickable in that case...)");
		}
		return "success";
	}

	/**
	 * @param alreadyKnownRefs
	 * @param vuser
	 */
	public static void getNewReceivedReferences(String alreadyKnownRefs, String vuser) {

		Set<String> refsToDiscard = Utils.jsonToSetOfStrings(alreadyKnownRefs);
		List<UserReference> newReferences = BeanProvider.getUserProfileService().getOtherReceivedReferencesThan(vuser, refsToDiscard);

		if (!newReferences.isEmpty()) {
			renderArgs.put("references", newReferences);
			renderArgs.put("_loggedInUserProfileId", getSessionWrapper().getLoggedInUserProfileId());
			renderTemplate("tags/profile/receivedReferenceHidden.html");
		} else {
			logger.log(Level.INFO, "no new references found!");
		}

	}

	/**
	 * renders the received references present in db and not mentioned in the list alreadyKnownRefs
	 * 
	 * @param alreadyKnownRefs
	 * @param vuser
	 */
	/**
	 * @param alreadyKnownRefs
	 * @param vuser
	 */
	public static void getRemovedReceivedReferencesJson(String alreadyKnownRefs, String vuser) {

		Set<String> knownRefs = Utils.jsonToSetOfStrings(alreadyKnownRefs);
		List<String> refsToRemove = BeanProvider.getUserProfileService().determineNonExisingReceivedRefIds(vuser, knownRefs);
		renderJSON(refsToRemove);

	}

	/**
	 * @param alreadyKnownRefs
	 * @param vuser
	 */
	public static void getNewSentReferences(String alreadyKnownRefs, String vuser) {

		Set<String> refsToDiscard = Utils.jsonToSetOfStrings(alreadyKnownRefs);

		List<UserReference> newReferences = BeanProvider.getUserProfileService().getOtherWrittentReferencesThan(vuser, refsToDiscard);

		if (!newReferences.isEmpty()) {
			renderArgs.put("references", newReferences);
			// todo: optimizatioN. we loas the profile twice here
			UserProfile visitedUserProfile = BeanProvider.getUserProfileService().loadUserProfile(vuser, false);
			new ProfileViewProfileVisibility(visitedUserProfile, getSessionWrapper()).putInArgsList(renderArgs);
			renderArgs.put("_loggedInUserProfileId", getSessionWrapper().getLoggedInUserProfileId());
			renderTemplate("tags/profile/writtenReferenceHidden.html");
		} else {
			logger.log(Level.INFO, "no new references found!");
		}

	}

	/**
	 * renders the received references present in db and not mentioned in the list alreadyKnownRefs
	 * 
	 * @param alreadyKnownRefs
	 * @param vuser
	 */
	/**
	 * @param alreadyKnownRefs
	 * @param vuser
	 */
	public static void getRemovedSentReferencesJson(String alreadyKnownRefs, String vuser) {

		Set<String> knownRefs = Utils.jsonToSetOfStrings(alreadyKnownRefs);
		List<String> refsToRemove = BeanProvider.getUserProfileService().determineNonExisingWrittenRefIds(vuser, knownRefs);
		renderJSON(refsToRemove);

	}

	// -------------------------------
	// contacts

	/**
	 * 
	 */
	public static void addToMyContacts(String invitationText, String vuser) {
		if (getSessionWrapper().isLoggedIn()) {
			BeanProvider.getUserProfileService().sendRequestToAddToContacts(getSessionWrapper().getLoggedInUserProfileId(), vuser, invitationText);
		} else {
			logger.log(Level.WARNING, "not requesting friendship: null sessoin or user not logged in");
		}
	}

	/**
	 * 
	 */
	public static void cancelContactRequest(String otherUser) {
		if (getSessionWrapper().isLoggedIn()) {
			BeanProvider.getUserProfileService().cancelRequestToAddToContacts(getSessionWrapper().getLoggedInUserProfileId(), otherUser);
		} else {
			logger.log(Level.WARNING, "not cancellingfriendship relationship: null session or user not logged in or null visited user id");
		}
	}

	/**
	 * 
	 */
	public static void rejectContactRequest(String otherUser) {
		if (getSessionWrapper().isLoggedIn()) {
			BeanProvider.getUserProfileService().rejectRequestToAddToContacts(getSessionWrapper().getLoggedInUserProfileId(), otherUser);
		} else {
			logger.log(Level.WARNING, "not rejecting friendship relationship: null session or user not logged in or null visited user id");
		}
	}

	/**
	 * 
	 * 
	 * 
	 */
	public static void acceptContactRequest(String otherUser) {
		if (getSessionWrapper().isLoggedIn()) {
			BeanProvider.getUserProfileService().acceptRequestToAddToContacts(getSessionWrapper().getLoggedInUserProfileId(), otherUser);
		} else {
			logger.log(Level.WARNING, "not accepting friendship relationship: null session or user not logged in or null visited user id");
		}
	}

	/**
	 * 
	 */
	public static void removeContact(String otherUser) {
		if (getSessionWrapper().isLoggedIn()) {
			BeanProvider.getUserProfileService().removeContactFromProfile(getSessionWrapper().getLoggedInUserProfileId(), otherUser);
		} else {
			logger.log(Level.WARNING, "not accepting friendship relationship: null session or user not logged in or null visited user id");
		}
	}

	/**
	 * @param vuser
	 * @param knownPendingReceivedIds
	 * @param knownPendingSentIds
	 * @param knownContactsIds
	 */
	public static void getRemoveContacts(String vuser, List<String> knownPendingReceivedIds, List<String> knownPendingSentIds, List<String> knownContactsIds) {

		// todo: optimization: we actually load the profile twice here!

		RemoveContactsJsonResponse response = new RemoveContactsJsonResponse();
		response.setRemovedIds(BeanProvider.getUserProfileService().determineNonExistingAnyContact(vuser, knownPendingReceivedIds, knownPendingSentIds, knownContactsIds));

		UserProfile visitedUserProfile = BeanProvider.getUserProfileService().loadUserProfile(vuser, true);
		ProfileViewProfileVisibility visibility = new ProfileViewProfileVisibility(visitedUserProfile, getSessionWrapper());

		response.setAddedToContactLinkVisible(visibility.isAddedToContactVisible());
		response.setAddToContactLinkVisible(visibility.isAddToContactLinkVisible());

		renderJSON(response);

	}

	public static void getNewReceivedContactRequests(String alreadyKnownContacts, String vuser) {

		if (vuser != null && vuser.equals(getSessionWrapper().getLoggedInUserProfileId())) {

			Set<String> contactsToDiscard = Utils.jsonToSetOfStrings(alreadyKnownContacts);
			List<Contact> newContacts = BeanProvider.getUserProfileService().getOtherReceivedPendingContactRequestsThan(vuser, contactsToDiscard);

			if (!newContacts.isEmpty()) {
				renderArgs.put("newContacts", newContacts);
				renderArgs.put("_loggedInUserProfileId", getSessionWrapper().getLoggedInUserProfileId());
				renderTemplate("tags/profile/receivedContactRequestHidden.html");

			} else {
				logger.log(Level.INFO, "no new references found!");
			}
		}

	}

	public static void getNewSentContactRequets(List<String> alreadyKnownContacts, String vuser) {

		// TODO:..
	}

	public static void getNewContacts(String alreadyKnownContacts, String vuser) {
		if (vuser != null && vuser.equals(getSessionWrapper().getLoggedInUserProfileId())) {

			Set<String> contactsToDiscard = Utils.jsonToSetOfStrings(alreadyKnownContacts);
			List<Contact> newContacts = BeanProvider.getUserProfileService().getOtherConfirmedCo1ntactsThan(vuser, contactsToDiscard);

			if (!newContacts.isEmpty()) {
				renderArgs.put("newContacts", newContacts);
				// todo: optimizatioN. we loas the profile twice here
				UserProfile visitedUserProfile = BeanProvider.getUserProfileService().loadUserProfile(vuser, false);
				new ProfileViewProfileVisibility(visitedUserProfile, getSessionWrapper()).putInArgsList(renderArgs);

				renderArgs.put("_loggedInUserProfileId", getSessionWrapper().getLoggedInUserProfileId());
				renderTemplate("tags/profile/contactHidden.html");

			} else {
				logger.log(Level.INFO, "no new references found!");
			}
		}

	}

}
