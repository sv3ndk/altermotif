package com.svend.dab.core.projects;

import java.io.File;

/**
 * @author svend
 *
 */
public interface IProjectPhotoService {

	
	public abstract void addOnePhoto(String projectId, File photoContent);

}
