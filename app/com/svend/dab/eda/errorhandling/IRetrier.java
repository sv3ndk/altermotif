package com.svend.dab.eda.errorhandling;

import java.util.Set;

public interface IRetrier {

	
	public boolean propagate(final Set<String> allRetriedIds);
	
	public void finalizeTr();
	
}