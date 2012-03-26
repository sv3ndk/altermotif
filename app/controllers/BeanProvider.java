package controllers;

import play.modules.spring.Spring;

import com.svend.dab.core.AdminService;
import com.svend.dab.core.IForumService;
import com.svend.dab.core.IGroupService;
import com.svend.dab.core.ISocialService;
import com.svend.dab.core.IUserMessagesServices;
import com.svend.dab.core.IProfilePhotoService;
import com.svend.dab.core.IUserProfileService;
import com.svend.dab.core.UserProfileService;
import com.svend.dab.core.beans.Config;
import com.svend.dab.core.dao.IForumPostDao;
import com.svend.dab.core.dao.IForumThreadDao;
import com.svend.dab.core.dao.IProjectDao;
import com.svend.dab.core.dao.IUserProfileDao;
import com.svend.dab.core.projects.IProjectFTSService;
import com.svend.dab.core.projects.IProjectPhotoService;
import com.svend.dab.core.projects.IProjectService;
import com.svend.dab.eda.errorhandling.NonFailingJsonMessageConverter;
import com.svend.dab.web.upload.IUploadProcessor;

public class BeanProvider {
	
	public static IUserProfileService getUserProfileService() {
		return (IUserProfileService) Spring.getBeanOfType(UserProfileService.class);
	}
	
	public static IUserProfileDao getUserProfileDao() {
		return (IUserProfileDao) Spring.getBeanOfType(IUserProfileDao.class);
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

	public static IUserMessagesServices  getMessagesService() {
		return (IUserMessagesServices) Spring.getBeanOfType(IUserMessagesServices.class);
		
	}
	
	public static IProjectService  getProjectService() {
		return (IProjectService) Spring.getBeanOfType(IProjectService.class);
	}

	public static IProjectFTSService  getProjectFullTextSearchService() {
		return (IProjectFTSService) Spring.getBeanOfType(IProjectFTSService.class);
	}

	public static IProjectDao  getProjectDao() {
		return (IProjectDao) Spring.getBeanOfType(IProjectDao.class);
	}
	
	public static IProjectPhotoService  getProjectPhotoService() {
		return (IProjectPhotoService) Spring.getBeanOfType(IProjectPhotoService.class);
	}

	public static AdminService getAdminService() {
		return (AdminService) Spring.getBeanOfType(AdminService.class);
	}
	
	public static IGroupService getGroupService() {
		return (IGroupService) Spring.getBeanOfType(IGroupService.class);
	}

	public static ISocialService getSocialService() {
		return (ISocialService) Spring.getBeanOfType(ISocialService.class);
	}

	public static IForumPostDao getForumPostDao() {
		return (IForumPostDao) Spring.getBeanOfType(IForumPostDao.class);
		
	}
	
	public static IForumThreadDao getForumThreadDao() {
		return (IForumThreadDao) Spring.getBeanOfType(IForumThreadDao.class);
		
	}
	public static IForumService getForumService() {
		return (IForumService) Spring.getBeanOfType(IForumService.class);
		
	}

}
