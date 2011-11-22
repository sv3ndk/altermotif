package controllers;

import play.modules.spring.Spring;

import com.svend.dab.core.IUserProfileService;
import com.svend.dab.core.UserProfileService;
import com.svend.dab.core.beans.Config;
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
	

}
