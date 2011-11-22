package com.svend.dab.web.servlets;



import java.io.InputStream;

import com.svend.dab.web.servlets.DabUploadFailedException.failureReason;


/**
 * @author Svend
 *
 */
public class UploadRequest {
	

	// name of the upload parts, as expected in a received upload POST request
	enum FORM_ITEM_NAME{
		username, 
		permkey,
		uploadtype,
		
		theFile,
	}
	
	
	private String username;
	private String uploadPermKey;
	
	
	// default value here but should never be user: just in case cannot be found in request (should only occur in case of bug or client side state manipulation
	private InputStream stream;

	// either CV or image 
	private UPLOAD_TYPE uploadType = UPLOAD_TYPE.UNKNOWN;
	
	// -------------------------------------------
	// -------------------------------------------
	
	
	/**
	 *  throws an exception if this upload request is not complet (i.e. does not contain enough info in order to proceed with the upload) 
	 */
	public void validateIsComplete() {
		if (username == null || "".equals(username) || uploadPermKey == null || "".equals(uploadPermKey) || uploadType == null || stream == null) {
			throw new DabUploadFailedException("Cannot proceed with upload: upload request in not complete", failureReason.fileFormatIncorrectError);
		}
	}
	
	// -------------------------------------------
	// -------------------------------------------
	
	//

	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public InputStream getStream() {
		return stream;
	}
	public void setStream(InputStream stream) {
		this.stream = stream;
	}
	public String getUploadPermKey() {
		return uploadPermKey;
	}
	public void setUploadPermKey(String uploadPermKey) {
		this.uploadPermKey = uploadPermKey;
	}
	public UPLOAD_TYPE getUploadType() {
		return uploadType;
	}
	public void setUploadType(UPLOAD_TYPE uploadType) {
		this.uploadType = uploadType;
	}



}
