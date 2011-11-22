package com.svend.dab.core.beans.profile;

import java.util.Date;

import org.springframework.data.annotation.Transient;

/**
 * @author Svend
 * 
 * 
 * Bean containing a simple URL link to a resource availble externaly (like a photo on S3 system)
 *
 */
public class ExternalLink {
	
	// we never store the HTTP link itself, which is temporary anyway (pre-signed URLs with expiration dates on S3 system)
	@Transient
	private String url;
	
	// ---------------------------
	// ---------------------
	

	public ExternalLink() {
		super();
	}

	public ExternalLink(String url) {
		super();
		this.url = url;
	}

	// ---------------------------
	// ---------------------

	public void generateUrl(Date cvExpirationDate) {
		// NOP: this super class only contains a "dumb" link, there is nothing to prepare here (see also specific storage logic in subclasses)
	}
	
	// ---------------------------
	// ---------------------
	
	
	public String getAddress() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}


}
