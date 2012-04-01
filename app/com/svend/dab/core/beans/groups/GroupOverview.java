package com.svend.dab.core.beans.groups;

import java.util.Date;

import com.svend.dab.core.beans.profile.Photo;

public class GroupOverview {


	private String groupId;
	private String name;
	private String description;

	private Date creationDate;

	private int numberOfUserMembers;
	private int numberOfProjectMembers;

	private Photo mainThumb;

	
	public GroupOverview() {
		super();
	}
	
	public GroupOverview(ProjectGroup group) {
		super();
		this.groupId = group.getId();
		this.name = group.getName();
		this.description = group.getDescription();
		this.creationDate = group.getCreationDate();
		
		if (group.getParticipants() == null) {
			numberOfUserMembers = 0;
		} else {
			numberOfUserMembers = group.getParticipants().size();
		}
		
		if (group.getProjectParticipants() == null) {
			numberOfProjectMembers = 0;
		} else {
			numberOfProjectMembers = group.getProjectParticipants().size();
		}
	}
	
	public boolean hasAThumbPhoto() {
		return mainThumb != null && !mainThumb.isPhotoEmpty();
	}

	
	//////////////////////
	//
	
	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public int getNumberOfUserMembers() {
		return numberOfUserMembers;
	}

	public void setNumberOfUserMembers(int numberOfUserMembers) {
		this.numberOfUserMembers = numberOfUserMembers;
	}

	public int getNumberOfGroupMembers() {
		return numberOfProjectMembers;
	}

	public void setNumberOfGroupMembers(int numberOfGroupMembers) {
		this.numberOfProjectMembers = numberOfGroupMembers;
	}

	public Photo getMainThumb() {
		return mainThumb;
	}

	public void setMainThumb(Photo mainThumb) {
		this.mainThumb = mainThumb;
	}

}
