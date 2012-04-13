package com.svend.dab.web;

import org.apache.commons.lang.NotImplementedException;
import org.springframework.stereotype.Service;
import static com.svend.dab.core.beans.Config.DEPLOYMENT_MODE.*;

import controllers.BeanProvider;

@Service
public class ResourceLocator {

	public static String LOCAL_PUBLIC_RESOURCES = "/public";
	
	// TODO: using Router.reverse() would be better for all local resources
	// , but does not seem to work with static resources
	
	public String getStylesheetPath() {
    	if (BeanProvider.getConfig().getDeploymentMode() == devLocal) {
    		return  LOCAL_PUBLIC_RESOURCES + "/stylesheets";
    	} else {
    		throw new NotImplementedException("");
    	}
	}
	
	

}
