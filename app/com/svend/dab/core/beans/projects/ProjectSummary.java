package com.svend.dab.core.beans.projects;

import java.util.Date;

import com.svend.dab.core.beans.profile.Photo;
import com.svend.dab.core.beans.projects.Project.STATUS;

/**
 * The {@link ProjectSummary} is what is propated into the profile of the project members
 * 
 * 
 * @author Svend
 *
 */
public class ProjectSummary {
	
	private String projectId;
	private String name;
	private Photo mainPhoto;
	
	private Date creationDate ;
	
	private STATUS status;
	

	public ProjectSummary() {
		super();
	}

	public ProjectSummary(Project project) {
		super();
		this.projectId = project.getId();
		this.name = project.getPdata().getName();
		this.mainPhoto = project.getMainPhoto();
		this.status = project.getStatus();
		this.creationDate = project.getPdata().getCreationDate();
	}
	
	public String getMainPhotoThumbLink() {
		if (mainPhoto == null) {
			return "";
		} else {
			return mainPhoto.getThumbAddress();
		}
	}
	
	
	public void generatePhotoLink(Date expirationdate) {
		if (mainPhoto != null) {
			mainPhoto.generatePresignedLinks(expirationdate, false, true);
		}
	}
	
	public boolean hasAThumbPhoto() {
		return mainPhoto != null && getMainPhotoThumbLink() != null;
	}
	
	
	public boolean isOf(Project project) {
		return projectId.equals(project.getId());
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Photo getMainPhoto() {
		return mainPhoto;
	}

	public void setMainPhoto(Photo mainPhoto) {
		this.mainPhoto = mainPhoto;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public STATUS getStatus() {
		return status;
	}

	public void setStatus(STATUS status) {
		this.status = status;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}



	

}
