package models.altermotif.projects;

import com.svend.dab.core.beans.projects.Project;


public class CreatedProject extends EditedProject{

	private CreatedProjectData pdata;

	public CreatedProjectData getPdata() {
		if (pdata == null) {
			return new CreatedProjectData();
		}
		return pdata;
	}

	public void setPdata(CreatedProjectData pdata) {
		this.pdata = pdata;
	}

	
	// ------------------------------
	// conversion into a stored project
	
	public Project toProject(String userLanguage) {
		
		Project createdProject = new Project();
		getPdata().applyToProjectData(createdProject.getPdata(), userLanguage);
		super.applyToProject(createdProject);
		
		return createdProject;
	}
	
	

}
