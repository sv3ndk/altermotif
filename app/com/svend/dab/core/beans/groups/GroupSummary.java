package com.svend.dab.core.beans.groups;

/**
 * @author svend
 * 
 */
public class GroupSummary {

	private String groupId;
	
	private String name;
	
	public GroupSummary() {
		super();
	}

	public GroupSummary(ProjectGroup group) {
		this.groupId = group.getId();
		this.name = group.getName();
	}

	////////////////////
	
	public boolean hasAThumbPhoto() {
		return false;
	}
	
	///
	

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

}