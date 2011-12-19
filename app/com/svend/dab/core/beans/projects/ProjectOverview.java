package com.svend.dab.core.beans.projects;

import java.util.Date;

import com.svend.dab.core.beans.profile.Photo;

/**
 * A {@link ProjectOverview} is not stored anywhere. It is computed by the DAO when searching for project: it contains just the subset of project information we are retrieving when searching for
 * projects
 * 
 * @author svend
 * 
 */
public class ProjectOverview {

	private String projectId;
	private String name;
	private String goal;
	private String description;

	private Date dueDate;
	private Date creationDate;

	private int numberOfMembers;

	private Photo mainThumb;
	
	public ProjectOverview(Project project) {
		projectId = project.getId();
		name = project.getPdata().getName();
		goal = project.getPdata().getGoal();
		description = project.getPdata().getDescription();
		
		dueDate = project.getPdata().getDueDate();
		creationDate = project.getPdata().getCreationDate();
		
		numberOfMembers = project.getConfirmedActiveParticipants().size();
		
		mainThumb = project.getMainPhoto();
		
	}
	
	
	public void generatePhotoLinks(Date expirationdate) {
		if (mainThumb != null) {
			mainThumb.generatePresignedLinks(expirationdate, false, true);
		}
	}
	
	public boolean hasAThumbPhoto() {
		return mainThumb != null && !mainThumb.isPhotoEmpty();
	}
	
	public String getMainPhotoThumbLink() {
		if (hasAThumbPhoto()) {
			return mainThumb.getThumbAddress();
		} else {
			return "";
		}
	}


	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public int getNumberOfMembers() {
		return numberOfMembers;
	}

	public void setNumberOfMembers(int numberOfMembers) {
		this.numberOfMembers = numberOfMembers;
	}

	public Photo getMainThumb() {
		return mainThumb;
	}

	public void setMainThumb(Photo mainThumb) {
		this.mainThumb = mainThumb;
	}


}
