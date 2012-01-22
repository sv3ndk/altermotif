package com.svend.dab.eda;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import com.svend.dab.core.beans.DabException;

/**
 * This is a simple container/dispatcher: all event propagation pass here, but the event has then the possibility to choose the propagator 
 * which is actually going to process it.
 * 
 * @author svend
 * 
 */
@Service
public class DelegatingPropagator implements IEventPropagator<Event>, IEventPropagatorsContainer, ApplicationContextAware {

	private static Logger logger = Logger.getLogger(DelegatingPropagator.class.getName());

	
	private ApplicationContext ctx;
	
	// -----------------------------------------------------------------
	// -----------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.svend.dab.eda.IEventPropagator#propagate(com.svend.dab.eda.events.Event)
	 */
	
	public void propagate(Event event) throws DabException {
		if (event == null) {
			logger.log(Level.WARNING, "Refusing to propagate a null event => not doing anything");
		} else {
			event.selectEventProcessor(this).propagate(event);
		}
	}

	
	public IEventPropagator getPropagatorByName(String name) {
		return (IEventPropagator) ctx.getBean(name);
	}

	
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		this.ctx = ctx;
	}

}
