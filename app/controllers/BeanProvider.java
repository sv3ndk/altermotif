package controllers;

import play.modules.spring.Spring;

import com.svend.dab.core.AdminService;
import com.svend.dab.core.IMessagesServices;
import com.svend.dab.core.IProfilePhotoService;
import com.svend.dab.core.IUserProfileService;
import com.svend.dab.core.UserProfileService;
import com.svend.dab.core.beans.Config;
import com.svend.dab.core.projects.IProjectPhotoService;
import com.svend.dab.core.projects.IProjectService;
import com.svend.dab.eda.errorhandling.NonFailingJsonMessageConverter;
import com.svend.dab.web.upload.IUploadProcessor;

public class BeanProvider {
	
	public static IUserProfileService getUserProfileService() {
		return (IUserProfileService) Spring.getBeanOfType(UserProfileService.class);
	}
	
	public static Config getConfig() {
		return (Config) Spring.getBeanOfType(Config.class);
	}

	public static NonFailingJsonMessageConverter getNonFailingJsonMessageConverter() {
		return (NonFailingJsonMessageConverter) Spring.getBeanOfType(NonFailingJsonMessageConverter.class);
	}

	public static IUploadProcessor  getUploadProcessor() {
		return (IUploadProcessor) Spring.getBeanOfType(IUploadProcessor.class);
	}
	
	public static IProfilePhotoService  getProfilePhotoService() {
		return (IProfilePhotoService) Spring.getBeanOfType(IProfilePhotoService.class);
	}

	public static IMessagesServices  getMessagesService() {
		return (IMessagesServices) Spring.getBeanOfType(IMessagesServices.class);
		
	}
	
	public static IProjectService  getProjectService() {
		return (IProjectService) Spring.getBeanOfType(IProjectService.class);
	}
	
	public static IProjectPhotoService  getProjectPhotoService() {
		return (IProjectPhotoService) Spring.getBeanOfType(IProjectPhotoService.class);
	}

	public static AdminService getAdminService() {
		return (AdminService) Spring.getBeanOfType(AdminService.class);
	}

}
