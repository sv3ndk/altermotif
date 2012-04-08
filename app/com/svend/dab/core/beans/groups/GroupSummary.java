package com.svend.dab.core.beans.groups;

import java.util.Date;

import com.svend.dab.core.beans.profile.Photo;

/**
 * @author svend
 * 
 */
public class GroupSummary {

	private String groupId;
	
	private String name;
	
	private Photo mainPhoto;

	
	public GroupSummary() {
		super();
	}

	public GroupSummary(ProjectGroup group) {
		this.groupId = group.getId();
		this.name = group.getName();
		this.mainPhoto = group.getPhotoAlbum().getMainPhoto();
	}

	////////////////////
	
	public boolean hasAThumbPhoto() {
		return mainPhoto != null && !mainPhoto.isPhotoEmpty();
	}
	
	public void generatePhotoLink(Date expirationdate) {
		if (mainPhoto != null) {
			mainPhoto.generatePresignedLinks(expirationdate, false, true);
		}
	}
	
	public String getMainPhotoThumbLink() {
		if (mainPhoto == null) {
			return "";
		} else {
			return mainPhoto.getThumbAddress();
		}
	}

	

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

	public Photo getMainPhoto() {
		return mainPhoto;
	}

	public void setMainPhoto(Photo mainPhoto) {
		this.mainPhoto = mainPhoto;
	}

}
