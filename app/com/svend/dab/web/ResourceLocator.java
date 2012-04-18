package com.svend.dab.web;

import static com.svend.dab.core.beans.Config.DEPLOYMENT_MODE.devLocal;

import org.springframework.stereotype.Service;

import controllers.BeanProvider;

@Service
public class ResourceLocator {

	// TODO: using Router.reverse() would be better for all local resources
	// , but does not seem to work with static resources
	public static String LOCAL_PUBLIC_RESOURCES = "/public";

	public static String ONLINE_PUBLIC_RESOURCES = "https://altermotif.s3.amazonaws.com/static";
	
	
	
	public String getDabStylesheetPath() {
    	if (BeanProvider.getConfig().getDeploymentMode() == devLocal) {
    		return  LOCAL_PUBLIC_RESOURCES + "/stylesheets";
    	} else {
    		return  ONLINE_PUBLIC_RESOURCES + "/stylesheets";
    	}
	}

	public String getDabImagesPath() {
		if (BeanProvider.getConfig().getDeploymentMode() == devLocal) {
			return  LOCAL_PUBLIC_RESOURCES + "/images";
		} else {
			return  ONLINE_PUBLIC_RESOURCES + "/images";
		}
	}
	
	

}
