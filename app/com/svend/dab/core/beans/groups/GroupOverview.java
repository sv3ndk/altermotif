package com.svend.dab.core.beans.groups;

import java.util.Date;

import com.svend.dab.core.beans.profile.Photo;

public class GroupOverview {

	private String groupId;
	private String name;
	private String goal;
	private String description;

	private Date creationDate;

	private int numberOfUserMembers;
	private int numberOfGroupMembers;

	private Photo mainThumb;

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
		return numberOfGroupMembers;
	}

	public void setNumberOfGroupMembers(int numberOfGroupMembers) {
		this.numberOfGroupMembers = numberOfGroupMembers;
	}

	public Photo getMainThumb() {
		return mainThumb;
	}

	public void setMainThumb(Photo mainThumb) {
		this.mainThumb = mainThumb;
	}

}
