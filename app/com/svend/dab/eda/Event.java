package com.svend.dab.eda;

import java.util.logging.Logger;


/**
 * parent of any other event
 * 
 * @author svend
 *
 */
public abstract class Event {
	// each sub-type must return a generic IEventPropagator specific to its own type
	public abstract IEventPropagator selectEventProcessor(IEventPropagatorsContainer container);

	private static Logger logger = Logger.getLogger(Event.class.getName());
	
	

}
