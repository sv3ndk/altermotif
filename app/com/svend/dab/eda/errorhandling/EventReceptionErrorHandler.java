package com.svend.dab.eda.errorhandling;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.util.ErrorHandler;

public class EventReceptionErrorHandler implements ErrorHandler {

	private static Logger logger = Logger.getLogger(EventReceptionErrorHandler.class.getName());
	
	public void handleError(Throwable e) {
		logger.log(Level.SEVERE, "Error while receiving an event: ", e);
	}

	
	
}
