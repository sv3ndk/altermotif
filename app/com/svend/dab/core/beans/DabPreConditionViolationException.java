package com.svend.dab.core.beans;

/**
 * 
 * Thrown in case the contract of a method is not fullfilled, preventing it to run (typically an illegal arguement passed) 
 * 
 * @author Svend
 *
 */
public class DabPreConditionViolationException extends DabException {


	/**
	 * 
	 */
	private static final long serialVersionUID = -2331104899122502378L;

	public DabPreConditionViolationException() {
		super(false);
	}
	
	public DabPreConditionViolationException(String message, Throwable cause) {
		super(message, cause, false);
	}
	
	public DabPreConditionViolationException(String message) {
		super(message, false);
	}
	
	public DabPreConditionViolationException(Throwable cause) {
		super(cause, false);
	}
}
