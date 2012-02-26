package com.svend.dab.eda.errorhandling;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.support.converter.JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;

/**
 * 
 * This makes sure that the marshalling of received events never fails (because we cannot do anything about it at this point)
 * 
 * @author svend
 *
 */
public class NonFailingJsonMessageConverter extends JsonMessageConverter {

	private static Logger logger = Logger.getLogger(NonFailingJsonMessageConverter.class.getName());
	
	
	
	
	
	public Object fromMessage(Message message) throws MessageConversionException {
		try {
			return super.fromMessage(message);
		} catch (Throwable th) {
			logger.log(Level.SEVERE, "Could not marshall received event => dropping it (there's not point in retrying later...). Message was " +message.toString(), th);
			return null;
		}
	}
	
	
	
}
