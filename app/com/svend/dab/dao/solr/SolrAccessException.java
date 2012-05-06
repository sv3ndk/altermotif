package com.svend.dab.dao.solr;

import com.svend.dab.core.beans.DabException;

public class SolrAccessException extends DabException {

	private static final long serialVersionUID = -6889263038660844135L;

	public SolrAccessException() {
		super(true);
	}

	public SolrAccessException(String message, Throwable cause) {
		super(message, cause, true);
	}

	public SolrAccessException(String message) {
		super(message, true);
	}

	public SolrAccessException(Throwable cause) {
		super(cause, true);
	}

}
