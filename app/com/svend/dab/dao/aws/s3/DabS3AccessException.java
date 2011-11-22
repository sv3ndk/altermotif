package com.svend.dab.dao.aws.s3;

import com.svend.dab.core.beans.DabException;

/**
 * @author Svend
 *
 */
public class DabS3AccessException extends DabException {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1601921938657165229L;
	
	
	public DabS3AccessException() {
		super(true);
	}
	
	public DabS3AccessException(String message, Throwable cause) {
		super(message, cause, true);
	}
	
	public DabS3AccessException(String message) {
		super(message, true);
	}
	
	public DabS3AccessException(Throwable cause) {
		super(cause,true);
	}

}
