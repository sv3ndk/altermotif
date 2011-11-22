package com.svend.dab.eda;

import com.svend.dab.core.beans.DabException;

/**
 * @author svend
 *
 */
public interface IEventPropagator<K extends Event> {
	
	
	
	public void propagate(K event) throws DabException;

}
