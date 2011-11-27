/**
 * 
 */
package models.altermotif.projects;

import play.data.validation.Required;
import play.data.validation.Valid;

import com.svend.dab.core.beans.projects.Project;
import com.svend.dab.core.beans.projects.ProjectData;

/**
 * @author svend
 * 
 */
public class CreatedProjectData extends CommonProjectData{

	@Required
	private String name;

	@Required
	private String goal;
	
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGoal() {
		return goal;
	}

	public void setGoal(String goal) {
		this.goal = goal;
	}

	// ---------------------
	// business logic
	
	

	@Override
	public void applyToProjectData(ProjectData pdata, String userLanguage) {
		if (pdata != null) {
			pdata.setName(name);
			pdata.setGoal(goal);
			super.applyToProjectData(pdata, userLanguage);
		}
	}

}
