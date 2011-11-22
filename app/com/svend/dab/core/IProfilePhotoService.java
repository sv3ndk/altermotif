package com.svend.dab.core;

import com.svend.dab.core.beans.profile.UserProfile;

/**
 * @author Svend
 *
 */
public interface IProfilePhotoService {

	public abstract void addOnePhoto(UserProfile updatedProfile, byte[] photoContent);
	
	
	/**
	 * Removes this photos (number starts from 0) from this {@link UserProfile} (directly in this java object instance) + removes the photo from persistence
	 * 
	 * @param loggedInUserProfile
	 * @param deletedPhotoIdx
	 */
	public abstract void removeProfilePhoto(UserProfile profile, int deletedPhotoIdx);

	/**
	 * 
	 * Take the "index"th photo of this user and sets it as profile photo
	 * 
	 * @param editedUserProfile
	 * @param index
	 */
	public abstract void movePhotoToFirstPosition(UserProfile profile, int index);

	/**
	 * Updates just a few fields of the profile
	 * 
	 * @param userName
	 * @param dateOfLatestLogin
	 * @param uploadPermKey
	 */

	public abstract void updatePhotoCaption(UserProfile profile, int photoIndex, String profilePhotoCaption);
}
