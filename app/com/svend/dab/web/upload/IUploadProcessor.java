package com.svend.dab.web.upload;

import java.io.File;


/**
 * @author Svend
 *
 * 
 *
 */
public interface IUploadProcessor {
	
	public void processUploadCvRequest(File theFile, String loggedInUserProfileId);
	

	public void processUploadPhotoRequest(File theFile, String username);

	
}
