package com.svend.dab.core.dao;

import com.svend.dab.core.beans.profile.UserProfile;

/**
 * @author Svend
 *
 */
public interface ICvBinaryDao {
	
	/**
	 * uploads this CV into the blob store, in a location appropriate for this {@link UserProfile} + updates this in memory {@link UserProfile}  with the new Cv information
	 * 
	 * @param profile
	 * @param cvContent
	 */
	public abstract void uploadCvPdf(UserProfile profile, byte[] cvContent);
	

	
	

}
