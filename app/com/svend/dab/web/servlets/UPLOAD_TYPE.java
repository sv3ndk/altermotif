package com.svend.dab.web.servlets;

public enum UPLOAD_TYPE {

	// CV: 
	CV( "/profile/managecv", "pretty:profilemanageCV"),

	// CV: 
	PHOTO("/profile/managephotos", "pretty:profilemanagephotos"),

	UNKNOWN("/profile/managephotos", "pretty:profilehome");

	private UPLOAD_TYPE(String sucessfullUploadNavigationOutcome, String failedUploadNavigationSuggestedOutcome) {
		this.sucessfullUploadNavigationOutcome = sucessfullUploadNavigationOutcome;
		this.failedUploadNavigationSuggestedOutcome = failedUploadNavigationSuggestedOutcome;
	}

	// where to navigate after a sucess for this kind of upload
	final String sucessfullUploadNavigationOutcome;

	// the URL shown to the user in the error page displayed if the upload of this kind is not successful
	final String failedUploadNavigationSuggestedOutcome;

}