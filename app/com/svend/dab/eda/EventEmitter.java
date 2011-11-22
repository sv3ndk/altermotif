package com.svend.dab.eda;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author svend
 * 
 */
@Component
public class EventEmitter implements IEventEmitter, Serializable {

	private static final long serialVersionUID = -9040986204094484192L;

	private static Logger logger = Logger.getLogger(EventEmitter.class.getName());

	// this is plugged to dab.events in the Spring config
//	@Autowired
//	@Qualifier("dabEventTemplate")
//	AmqpTemplate amqpTemplate;

	@Autowired
	private EventHandler eventHandler;
	
	public void emit(final Event event) {

		logger.log(Level.INFO, "emitting event: " + event);
		
		new Thread() {
			
			public void run() {
				
				eventHandler.handle(event);
				
			};
				
			
		}.start();
		
		
//		ObjectMapper mapper = new ObjectMapper();
//		mapper.enableDefaultTyping(DefaultTyping.NON_FINAL);
//		try {
//			Message message = new Message(mapper.writeValueAsBytes(event), new MessageProperties());
//			amqpTemplate.send(message);
//		} catch (Exception e) {
//			System.out.println("coult not send event" + e.getMessage());
//			logger.log(Level.WARNING, "Could not send envent", e);
//			
//			throw new RuntimeException("could not send event", e);
//			
//		}
	}

}
