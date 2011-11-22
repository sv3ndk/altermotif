/**
 * 
 */
package com.svend.dab.dao.aws.sdb;

import com.svend.dab.core.beans.DabException;

/**
 * @author Svend
 *
 */
public class DabAwsAccessException extends DabException {

	private static final long serialVersionUID = 8263980925834977531L;

	public DabAwsAccessException() {
		super(true);
	}

	public DabAwsAccessException(String message, Throwable cause) {
		super(message, cause, true);
	}

	public DabAwsAccessException(String message) {
		super(message, true);
	}

	public DabAwsAccessException(Throwable cause) {
		super(cause,true);
	}
	
	

}
