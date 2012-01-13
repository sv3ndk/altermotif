package controllers.projects;

import java.util.logging.Logger;

import com.svend.dab.core.beans.projects.ForumThread;
import com.svend.dab.core.beans.projects.Project;
import com.svend.dab.core.beans.projects.ProjectPep;

import controllers.Application;
import controllers.BeanProvider;
import controllers.DabController;

/**
 * @author svend
 * 
 */
public class ProjectForumThread extends DabController {

	private static Logger logger = Logger.getLogger(ProjectForumThread.class.getName());

	public static void projectForumThread(String t) {

		ForumThread thread = BeanProvider.getForumThreadDao().getThreadById(t);

		if (thread != null) {
			Project project = BeanProvider.getProjectService().loadProject(thread.getProjectId(), false);

			if (project != null) {
				ProjectPep pep = new ProjectPep(project);
				if (pep.isAllowedSeeThisForumThread(getSessionWrapper().getLoggedInUserProfileId(), thread.isThreadPublic())) {
					renderArgs.put("thread", thread);
					renderArgs.put("project", project);
					render();
				} else {
					Application.index();
				}
			} else {
				Application.index();
			}
		} else {
			Application.index();
		}
	}

}
