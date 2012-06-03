package controllers;

import play.modules.spring.Spring;

import com.svend.dab.core.AdminService;
import com.svend.dab.core.IForumService;
import com.svend.dab.core.IProfilePhotoService;
import com.svend.dab.core.ISocialService;
import com.svend.dab.core.IUserMessagesServices;
import com.svend.dab.core.IUserProfileService;
import com.svend.dab.core.UserProfileService;
import com.svend.dab.core.beans.Config;
import com.svend.dab.core.dao.IForumPostDao;
import com.svend.dab.core.dao.IForumThreadDao;
import com.svend.dab.core.dao.IGroupDao;
import com.svend.dab.core.dao.IGroupIndexDao;
import com.svend.dab.core.dao.IProjectDao;
import com.svend.dab.core.dao.IProjectIndexDao;
import com.svend.dab.core.dao.IUserProfileDao;
import com.svend.dab.core.groups.IGroupPhotoService;
import com.svend.dab.core.groups.IGroupService;
import com.svend.dab.core.projects.IProjectPhotoService;
import com.svend.dab.core.projects.IProjectService;
import com.svend.dab.eda.errorhandling.NonFailingJsonMessageConverter;
import com.svend.dab.web.ResourceLocator;
import com.svend.dab.web.upload.IUploadProcessor;

public class BeanProvider {

	public static IUserProfileService getUserProfileService() {
		return Spring.getBeanOfType(UserProfileService.class);
	}

	public static IUserProfileDao getUserProfileDao() {
		return Spring.getBeanOfType(IUserProfileDao.class);
	}

	public static Config getConfig() {
		return Spring.getBeanOfType(Config.class);
	}
	
	public static ResourceLocator getResourceLocator() {
		return Spring.getBeanOfType(ResourceLocator.class);
	}

	public static NonFailingJsonMessageConverter getNonFailingJsonMessageConverter() {
		return Spring.getBeanOfType(NonFailingJsonMessageConverter.class);
	}

	public static IUploadProcessor getUploadProcessor() {
		return Spring.getBeanOfType(IUploadProcessor.class);
	}

	public static IProfilePhotoService getProfilePhotoService() {
		return Spring.getBeanOfType(IProfilePhotoService.class);
	}

	public static IUserMessagesServices getMessagesService() {
		return Spring.getBeanOfType(IUserMessagesServices.class);
	}

	public static IProjectService getProjectService() {
		return Spring.getBeanOfType(IProjectService.class);
	}

	public static IProjectIndexDao getProjectIndexDao() {
		return Spring.getBeanOfType(IProjectIndexDao.class);
	}

	public static IProjectDao getProjectDao() {
		return Spring.getBeanOfType(IProjectDao.class);
	}

	public static IProjectPhotoService getProjectPhotoService() {
		return Spring.getBeanOfType(IProjectPhotoService.class);
	}

	public static AdminService getAdminService() {
		return Spring.getBeanOfType(AdminService.class);
	}

	public static IGroupService getGroupService() {
		return Spring.getBeanOfType(IGroupService.class);
	}

	public static ISocialService getSocialService() {
		return Spring.getBeanOfType(ISocialService.class);
	}

	public static IForumPostDao getForumPostDao() {
		return Spring.getBeanOfType(IForumPostDao.class);
	}

	public static IForumThreadDao getForumThreadDao() {
		return Spring.getBeanOfType(IForumThreadDao.class);
	}

	public static IForumService getForumService() {
		return Spring.getBeanOfType(IForumService.class);
	}

	public static IGroupDao getGroupDao() {
		return Spring.getBeanOfType(IGroupDao.class);
	}

	public static IGroupPhotoService getGroupPhotoService() {
		return Spring.getBeanOfType(IGroupPhotoService.class);
	}
	
	public static IGroupIndexDao getGroupIndexDao() {
		return Spring.getBeanOfType(IGroupIndexDao.class);
	}

}
