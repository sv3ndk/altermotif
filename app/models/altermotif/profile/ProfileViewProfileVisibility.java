package models.altermotif.profile;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.svend.dab.core.beans.profile.UserProfile;

import models.altermotif.AbstractRenderableModel;
import models.altermotif.SessionWrapper;

public class ProfileViewProfileVisibility extends AbstractRenderableModel{
	
	private static Logger logger = Logger.getLogger(ProfileViewProfileVisibility.class.getName());
	
	// name of this javabean, as visible from the HTML views
	public static String RENDER_PARAM_NAME = "profileVisibility";
	
	private final UserProfile visitedUserProfile;
	private final SessionWrapper userSession;
	
	
	public ProfileViewProfileVisibility(UserProfile visitedUserProfile, SessionWrapper session) {
		this.visitedUserProfile = visitedUserProfile;
		this.userSession = session;
	}


	@Override
	protected String getRenderParamName() {
		return RENDER_PARAM_NAME;
	}
	
	
	public boolean isFirstNameVisible() {
		return isVisitingHisOwnProfile() || visitedUserProfile.isFirstNameVisibleTo(userSession.getLoggedInUserProfileId());	
	}
	
	public boolean isLastNameVisible() {
		return isVisitingHisOwnProfile() || visitedUserProfile.isLastNameVisibleTo(userSession.getLoggedInUserProfileId());	}
	
	public boolean isNeitherFirstNameNorlastNameIsVisible() {
		return !isFirstNameVisible() && !isLastNameVisible();
	}
	
	public boolean isAgeVisible() {
		return isVisitingHisOwnProfile() || visitedUserProfile.isAgeVisibleTo(userSession.getLoggedInUserProfileId());
	}
	
	public boolean isUserPhotoGalleryVisible() {
		return visitedUserProfile.hasMoreThanOnePhoto() && (isVisitingHisOwnProfile() || visitedUserProfile.isPhotoGalleryVisibleTo(userSession.getLoggedInUserProfileId()));
	}

	public boolean isUserProfileGenderVisible() {
		if (visitedUserProfile == null || visitedUserProfile.getPdata() == null || visitedUserProfile.getPdata().getGender() == null || visitedUserProfile.getPdata().getGenderLabel() == null) {
			return false;
		}
		
		return !"U".equals(visitedUserProfile.getPdata().getGender());
	}

	public boolean isCvLinkVisible() {
		return visitedUserProfile.hasCv() && (isVisitingHisOwnProfile() || visitedUserProfile.isCvVisibleTo(userSession.getLoggedInUserProfileId()));

	}

	public boolean isMayContactThisUser() {
		return userSession.isLoggedIn() && ! isVisitingHisOwnProfile();
	}
	
	// -----------------------------------------
	// references visibility
	
	public boolean isLeaveAReferenceLinkVisible() {
		return userSession != null && userSession.isLoggedIn() && !isVisitingHisOwnProfile();
	}
	
	public boolean isPendingInvitationTextVisible() {
		
		if (userSession == null || !userSession.isLoggedIn()) {
			return false;
		}

		if (isVisitingHisOwnProfile() && visitedUserProfile.hasAtLeastOnePendingReceivedContactsRequest()) {
			return true;
		}
		return false;
		
	}

	
	// -----------------------------------------
	// contacts visibility 
	// 
	
	public boolean isAddToContactLinkVisible() {
		if (userSession == null || !userSession.isLoggedIn()) {
			return false;
		}

		if (isVisitingHisOwnProfile()) {
			return false;
		}

		// if the text saying that a request to be added to the contacts has already been sent is visible, then the link to send this request is not visible
		if (isAddedToContactVisible()) {
			return false;
		}

		if (visitedUserProfile.hasSentAContactRequestTo(userSession.getLoggedInUserProfileId())) {
			return false;
		}

		if (visitedUserProfile.hasUserInContacts(userSession.getLoggedInUserProfileId())) {
			return false;
		}
		return true;

	}
	
	public boolean isAddedToContactVisible() {
		if (userSession == null || !userSession.isLoggedIn()) {
			return false;
		}

		if (isVisitingHisOwnProfile()) {
			// we cannot send a request to ourself !
			return false;
		}

		return visitedUserProfile.hasReceivedAContactRequestFrom(userSession.getLoggedInUserProfileId());
	}

	
	
	// -----------------------------------------
	//  
	
	/**
	 * @return true if the user is currently viewing his own profile (which implies he may edit it...)
	 */
	public boolean isVisitingHisOwnProfile() {
		if ( visitedUserProfile == null ) {
			// no "vuser" parameter found in the request => not visiting his own
			// profile!
			return false;
		}

		if (userSession == null || ! userSession.isLoggedIn()) {
			// not logged in => not visiting his own profile!
			return false;
		}
		
		if (visitedUserProfile.getUsername() == null) {
			// this should never happen...
			return false;
		}
		

		return visitedUserProfile.getUsername().equals(userSession.getLoggedInUserProfileId());
	}
	
	
	
	
}
