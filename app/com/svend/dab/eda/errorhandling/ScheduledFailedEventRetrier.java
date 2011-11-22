package com.svend.dab.eda.errorhandling;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * This constantly listens to the "retry" queue when failed events are supposed to arrive after their first execution attempt.
 * 
 * It's job is simply to retry to execute those jobs
 * 
 * @author svend
 * 
 */
@Component
public class ScheduledFailedEventRetrier {

	@Autowired
	private IRetrier retrier;

	private static Logger logger = Logger.getLogger(ScheduledFailedEventRetrier.class.getName());

	// ------------------------------------------------------------
	// ------------------------------------------------------------

	/**
	 * process once all pending events in the retry queue, then stops
	 */
	@Scheduled(fixedDelay = 120000)
	public void retryAllPresentRetriedEvents() {

		final Set<String> allRetriedIds = new HashSet<String>();
		// as long as we have more messages to receive

		while (tryToPropagate(allRetriedIds)) {
			// nothing to do here: everything is in the tryToPropagate above
		}

	}

	/**
	 * @param allRetriedIds
	 * @return
	 */
	@Transactional(propagation = Propagation.NEVER)
	public boolean tryToPropagate(final Set<String> allRetriedIds) {
		try {
			return retrier.propagate(allRetriedIds);

		} catch (Throwable exc) {
			logger.log(Level.WARNING, "error while retrying a received event,", exc);
			// not stopping here: the subsequent events might succeed
			return true;
		} finally {
			retrier.finalizeTr();
		}
	}

}
