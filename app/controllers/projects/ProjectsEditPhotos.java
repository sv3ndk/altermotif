/**
 * 
 */
package controllers.projects;

import java.io.File;

import com.svend.dab.core.beans.projects.Project;
import com.svend.dab.core.beans.projects.ProjectPep;

import controllers.Application;
import controllers.BeanProvider;
import controllers.DabLoggedController;

/**
 * @author svend
 * 
 */
public class ProjectsEditPhotos extends DabLoggedController {

	public static void projectsEditPhotos(String p) {

		Project project = BeanProvider.getProjectService().loadProject(p, true);

		if (project != null) {
			ProjectPep pep = new ProjectPep(project);
			if (pep.isAllowedToEditPhotoGallery(getSessionWrapper().getLoggedInUserProfileId())) {

				renderArgs.put("editedProject", project);
				renderArgs.put("uploadPhotoLinkActive", !project.isPhotoPackFull());

				render();

			} else {
				Application.index();
			}

		} else {
			Application.index();
		}
	}

	public static void doUploadPhoto(File theFile) {
		
	}
	
	public static void doDeletePhoto(int deletedPhotoIdx) {

	}

	public static void doUpdatePhotoCaption(int photoIndex, String photoCaption) {

	}
	
	public static void doSetAsMainPhoto(int photoIndex) {
		
	}

}
