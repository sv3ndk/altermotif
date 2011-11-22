package com.svend.dab.web.servlets;

import com.svend.dab.core.beans.DabException;

/**
 * Exception description while an upload has failed
 * 
 * @author Svend
 * 
 */
public class DabUploadFailedException extends DabException {

	private static final long serialVersionUID = 5697404963723384636L;

	private failureReason reason;
	

	public DabUploadFailedException(failureReason reason) {
		super(false);
		this.reason = reason;
	}

	public DabUploadFailedException(String message, failureReason reason) {
		super(message, false);
		this.reason = reason;
	}

	public DabUploadFailedException(String message, failureReason reason, Throwable e) {
		super(message, e, false);
		this.reason = reason;
	}

	public DabUploadFailedException(String message, Exception e) {
		super(message, e, false);
		this.reason = failureReason.technicalError;
	}

	public enum failureReason {

		// the String passed as argument is the detail provided to the javascript client: very often we just say "uploadGenericError", unless we could tell to
		// the user to do something else to avoid the exception
		technicalError("uploadGenericError"), 
		fileTooBig("uploadedFileTooBigError"), 
		userNotFound("uploadGenericError"), 
		securityError("uploadGenericError"), 
		fileFormatIncorrectError(
				"profileUploadCvNotAPdf"), 
		fileFormatNotAnImageError(
				"profileUploadPhotoNotAnImage"), 
	partNotFound("uploadGenericError"), ;

		private failureReason(String key) {
			this.errorMessageKey = key;
		}

		// localized key for the error message corresponding to this upload error (several error might have the same key)
		private String errorMessageKey;

		public String getErrorMessageKey() {
			return errorMessageKey;
		}

	}

	public failureReason getReason() {
		return reason;
	}

}
