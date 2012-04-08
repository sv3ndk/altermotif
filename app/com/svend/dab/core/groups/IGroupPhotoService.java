package com.svend.dab.core.groups;

import java.io.File;

import com.svend.dab.core.beans.groups.ProjectGroup;

public interface IGroupPhotoService {
	
	public abstract void addOnePhoto(String groupId, File photoContent);

	public abstract void removePhoto(ProjectGroup group, int deletedPhotoIdx);

	public abstract void replacePhotoCaption(ProjectGroup group, int photoIndex, String photoCaption);

	public abstract void putPhotoInFirstPositio(ProjectGroup group, int photoIndex);

}
