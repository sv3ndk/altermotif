package com.svend.dab.core.beans;

/**
 * @author svend
 *
 */
public class DabIllegalFormatException extends DabException {

	/**
	 * 
	 * 
	 */
	private static final long serialVersionUID = 2022026122645160990L;

	/**
	 * 
	 */
	public DabIllegalFormatException() {
		super(false);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DabIllegalFormatException(String message, Throwable cause) {
		super(message, cause, false);
	}

	/**
	 * @param message
	 */
	public DabIllegalFormatException(String message) {
		super(message, false);
	}

	/**
	 * @param cause
	 */
	public DabIllegalFormatException(Throwable cause) {
		super(cause, false);
	}

}
