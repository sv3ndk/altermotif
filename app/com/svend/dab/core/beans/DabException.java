package com.svend.dab.core.beans;

/**
 * @author Svend
 *
 */
public class DabException extends RuntimeException{

	
	// states if simply waiting a bit might automatically resolve the issue or not 
	protected final boolean recoverable;
	

	public DabException(boolean recoverable) {
		super();
		this.recoverable = recoverable;
	}

	public DabException(String message,  Throwable cause, boolean recoverable) {
		super(message, cause);
		this.recoverable = recoverable;
	}

	public DabException(String message, boolean recoverable) {
		super(message);
		this.recoverable = recoverable;
	}

	public DabException(Throwable cause, boolean recoverable) {
		super(cause);
		this.recoverable = recoverable;
	}

	private static final long serialVersionUID = 6241170465885380793L;

	public boolean isRecoverable() {
		return recoverable;
	}
}
