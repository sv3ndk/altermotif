package com.svend.dab.web.upload;

import java.io.File;


/**
 * @author Svend
 *
 * 
 *
 */
public interface IUploadProcessor {
	
//	public void processUploadRequest(UploadRequest uploadRequest);

	public void processUploadRequest(File theFile, String uploadtype, String username);
	

}
