package com.svend.dab.core.beans;


import javax.servlet.ServletContext;

import org.springframework.stereotype.Component;

import controllers.BeanProvider;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

@Component("config")
public class Config {

	private static Config staticConfig;
	
	DEPLOY_MODE deploy_mode = DEPLOY_MODE.PROD;

	// when generating the S3 presigned links, what delay do we set, in millis
	private long cvExpirationDelayInMillis = 1000 * 60 * 60;

	private long photoExpirationDelayInMillis = 1000 * 60 * 60;

	private String uploadServletPath = "/upl";

	private long maxUploadedPhotoSizeInBytes = 5l * 1024l * 1024;

	private long maxUploadedCVSizeInBytes = 3l * 1024 * 1024;

	// some pages polls for boolean values to the server to know if some data is outdate (and if so, refresh whatever is approapriate). This is the period, in
	// millis, of the polling
	private long freshnessPollingPeriodMillis = 5000;

	private String cachedFullUploadServletPath;
	private Object uploadServletPathMutex = new Object();

	enum DEPLOY_MODE {
		PROD, DEV
	}

	// ------------------------------------------------------
	// ------------------------------------------------------
	// ------------------------------------------------------
	
	private static Config getStaticConfig() {
		if (staticConfig == null) {
			synchronized (Config.class) {
				if (staticConfig == null) {
					staticConfig = BeanProvider.getConfig();
				}
			}
		}
		
		return staticConfig;
		
	}
	
	

	public boolean isDevMode() {
		return deploy_mode == DEPLOY_MODE.DEV;
	}

	public boolean isProdMode() {
		return deploy_mode == DEPLOY_MODE.PROD;
	}

	public String getDateDisplayFormat() {
		return "dd/MM/yyyy";
	}

	public static String getDateDisplayFormat_Static() {
		return getStaticConfig().getDateDisplayFormat();
	}

	public String getDateTimeDisplayFormat() {
		return "dd MMM yyyy HH:mm";
	}

	/**
	 * @return the number of message to display per page in the inbox and outbox pages
	 */
	public int getInboxOutboxPageSize() {
		return 12;
	}

	public String getUploadServletPath() {
		if (cachedFullUploadServletPath == null) {
			synchronized (uploadServletPathMutex) {
				if (cachedFullUploadServletPath == null) {
					throw new org.apache.commons.lang.NotImplementedException("TODO play migration");
//					ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
//					cachedFullUploadServletPath = servletContext.getContextPath() + uploadServletPath;
				}
			}
		}

		return cachedFullUploadServletPath;
	}

	public long getCvExpirationDelayInMillis() {
		return cvExpirationDelayInMillis;
	}

	public long getPhotoExpirationDelayInMillis() {
		return photoExpirationDelayInMillis;
	}

	public long getMaxUploadedPhotoSizeInBytes() {
		return maxUploadedPhotoSizeInBytes;
	}

	public long getMaxUploadedCVSizeInBytes() {
		return maxUploadedCVSizeInBytes;
	}

	public long getFreshnessPollingPeriodMillis() {
		return freshnessPollingPeriodMillis;
	}

	public void setFreshnessPollingPeriodMillis(long freshnessPollingPeriodMillis) {
		this.freshnessPollingPeriodMillis = freshnessPollingPeriodMillis;
	}

}
