package web.jobs;

import java.util.logging.Level;
import java.util.logging.Logger;

import controllers.BeanProvider;
import play.jobs.Every;
import play.jobs.Job;

/**
 * @author svend
 *
 */
@Every("5min")
public class CountTagsMapReduceJob extends Job<Object>{

	private static Logger logger = Logger.getLogger(CountTagsMapReduceJob.class.getName());
	
	@Override
	public void doJob() throws Exception {
		logger.log(Level.INFO, "launching tag count job");
		BeanProvider.getProjectDao().launchCountProjectTagsJob();
	}

}
