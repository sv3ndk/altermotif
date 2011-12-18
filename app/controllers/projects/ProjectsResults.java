/**
 * 
 */
package controllers.projects;

import java.util.logging.Level;
import java.util.logging.Logger;

import models.altermotif.projects.SearchRequest;
import controllers.DabController;

/**
 * @author svend
 *
 */
public class ProjectsResults extends DabController {

	private static Logger logger = Logger.getLogger(ProjectsResults.class.getName());
	
	public static void projectsResults(SearchRequest r) {
		
		logger.log(Level.INFO, "searchedTerm:" + r.getTerm());
		
		
		logger.log(Level.INFO, "tags:" + r.getTag());

		if (r.getTag() != null) {
			for (String tag: r.getTag()) {
				logger.log(Level.INFO, "ag: " + tag);
			}
		}
		
		render();
	}

}
