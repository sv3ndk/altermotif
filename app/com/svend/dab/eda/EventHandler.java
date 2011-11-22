package com.svend.dab.eda;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.svend.dab.core.beans.DabException;

/**
 * @author svend
 * 
 */
@Service("eventHandler")
public class EventHandler {

	private static Logger logger = Logger.getLogger(EventHandler.class.getName());

//	@Autowired
//	@Qualifier("dabRetryEventTemplate")
//	protected RabbitTemplate amqpTemplate;
	
	
	// alreay commented before migration
//	protected AmqpTemplate amqpTemplate;
	
	// retried message expire after one hour
	private String RETRY_EXPIRATION_DELAY = Long.toString(1000l * 60 * 60);
	
	
	@Autowired
	@Qualifier("delegatingPropagator")
	private IEventPropagator<Event> propagator;
	

	/**
	 * reception of an event
	 * 
	 * @param event
	 */
	public void handle(Event event) {
		try {
			logger.log(Level.INFO, "event received: " + event);
			propagator.propagate(event);
		} catch (DabException exc) {
			if (exc.isRecoverable()) {
				logger.log(Level.WARNING, "Caught a recoverable error while trying to propagate an event => posting back to retry queue", exc);
				
				logger.log(Level.SEVERE, "TODO: re-implement this retry mechanism...");
//				
//				Message message = amqpTemplate.getMessageConverter().toMessage(event, new MessageProperties());
//				message.getMessageProperties().setMessageId(UUID.randomUUID().toString());
//				message.getMessageProperties().setMessageCount(10);
//				message.getMessageProperties().setExpiration(RETRY_EXPIRATION_DELAY);
//				message.getMessageProperties().setTimestamp(new GregorianCalendar().getTime());
//				
//				amqpTemplate.send(message);
//				
			} else {
				logger.log(Level.SEVERE, "Caught a  NON recoverable error while trying to propagate an event", exc);
			}
		} catch (Throwable exc) {
			logger.log(Level.SEVERE, "Caught a  NON recoverable error while trying to propagate an event", exc);
		}

	}

}
