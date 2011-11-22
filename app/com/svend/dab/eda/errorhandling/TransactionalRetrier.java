package com.svend.dab.eda.errorhandling;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.amqp.core.Message;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.svend.dab.eda.Event;
import com.svend.dab.eda.IEventPropagator;

@Service
public class TransactionalRetrier implements IRetrier {

//	@Autowired
//	@Qualifier("dabRetryEventTemplate")
//	protected RabbitTemplate retryQueueTemplate;
//
//	@Autowired()
//	@Qualifier("delegatingPropagator")
//	private IEventPropagator<Event> propagator;
	
	
	private static Logger logger = Logger.getLogger(TransactionalRetrier.class.getName());

	// --------------------------------------------------
	// --------------------------------------------------
	// --------------------------------------------------
	
	/**
	 * @param event
	 */
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public boolean propagate(final Set<String> allRetriedIds) {
//		Message msg = retryQueueTemplate.receive();
//		
//		if (msg == null) {
//			return false;
//		} else {
//			
//			// making sure we have not yet retried this message
//			if (allRetriedIds.contains(msg.getMessageProperties().getMessageId())) {
//				logger.log(Level.INFO, "Finished to retry everything => stopping");
//				return false;
//			}
//			allRetriedIds.add(msg.getMessageProperties().getMessageId());
//			propagator.propagate((Event) retryQueueTemplate.getMessageConverter().fromMessage(msg));
//
//		}
		return true;
	}
	
	
	@Transactional(propagation=Propagation.NEVER)
	public void finalizeTr() {
	}
}
