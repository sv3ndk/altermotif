package com.svend.dab.core.beans;

import java.util.LinkedList;
import java.util.List;

import models.altermotif.projects.theme.SubTheme;
import models.altermotif.projects.theme.Theme;

import org.springframework.stereotype.Component;

import controllers.BeanProvider;

@Component("config")
public class Config {

	private static Config staticConfig;

	// when generating the S3 presigned links, what delay do we set, in millis
	private long cvExpirationDelayInMillis = 1000 * 60 * 60;

	private long photoExpirationDelayInMillis = 1000 * 60 * 60;

	private long maxUploadedPhotoSizeInBytes = 5l * 1024l * 1024;

	private long maxUploadedCVSizeInBytes = 3l * 1024 * 1024;
	
	private int maxNumberOfDisplayedProjectTag = 35;

	// some pages polls for boolean values to the server to know if some data is outdate (and if so, refresh whatever is approapriate). This is the period, in
	// millis, of the polling
	private long freshnessPollingPeriodMillis = 5000;
	
	private long howLongIsABitInMillis = 750; 
	
	private List<Theme> projectThemes = new LinkedList<Theme>();
	
	
	// ------------------------------------------------------
	// ------------------------------------------------------

	public Config() {
		
		Theme environment = new Theme("environment", "projectThemeEnvironment");
		
		environment.addSubTheme(new SubTheme("protecting", "projectThemeEnvironmentSubThemeProtecting"));
		environment.addSubTheme(new SubTheme("recycling", "projectThemeEnvironmentSubThemeRecycling"));
		environment.addSubTheme(new SubTheme("sensitizing", "projectThemeEnvironmentSubThemeSensitizing"));
		environment.addSubTheme(new SubTheme("cleaning", "projectThemeEnvironmentSubThemeCleaning"));
		environment.addSubTheme(new SubTheme("agriculture", "projectThemeEnvironmentSubThemeAgriculture"));
		environment.addSubTheme(new SubTheme("other", "projectThemeEnvironmentSubThemeOther"));
		projectThemes.add(environment);
		
		Theme development = new Theme("development", "projectThemeDevelopment");
		development.addSubTheme(new SubTheme("charity", "projectThemeDevelopmentSubThemeCharity"));
		development.addSubTheme(new SubTheme("devAtHome", "projectThemeDevelopmentSubThemeDevAtHome"));
		development.addSubTheme(new SubTheme("devAbroad", "projectThemeDevelopmentSubThemeDevAbroad"));
		development.addSubTheme(new SubTheme("sensitizing", "projectThemeDevelopmentSubThemeSensitizing"));
		development.addSubTheme(new SubTheme("fairtrade", "projectThemeDevelopmentSubThemeFairTrade"));
		development.addSubTheme(new SubTheme("other", "projectThemeDevelopmentSubThemeOther"));
		projectThemes.add(development);
		
		Theme culture = new Theme("culture", "projectThemeCulture");
		culture.addSubTheme(new SubTheme("arts", "projectThemeCultureSubThemeArts"));
		culture.addSubTheme(new SubTheme("journalism", "projectThemeCultureSubThemeJournalism"));
		culture.addSubTheme(new SubTheme("education", "projectThemeCultureSubThemeEducation"));
		culture.addSubTheme(new SubTheme("other", "projectThemeCultureSubThemeOther"));
		projectThemes.add(culture);
				
		Theme science = new Theme("science", "projectThemeScience");
		science.addSubTheme(new SubTheme("education", "projectThemeScienceSubThemeEducation"));
		science.addSubTheme(new SubTheme("research", "projectThemeScienceSubThemeResearch"));
		science.addSubTheme(new SubTheme("technology", "projectThemeScienceSubThemeTechnology"));
		science.addSubTheme(new SubTheme("other", "projectThemeScienceSubThemeOther"));
		projectThemes.add(science);
		
		Theme travel = new Theme("travel", "projectThemeTravel");
		travel.addSubTheme(new SubTheme("intEx", "projectThemeTravelSubThemeIntExchange"));
		travel.addSubTheme(new SubTheme("travInGroup", "projectThemeTravelSubThemeTravellingInGroup"));
		travel.addSubTheme(new SubTheme("exploration", "projectThemeTravelSubThemeExploration"));
		travel.addSubTheme(new SubTheme("other", "projectThemeTravelSubThemeOther"));
		projectThemes.add(travel);
		
		Theme sports = new Theme("sports", "projectThemeSports");
		sports.addSubTheme(new SubTheme("teamBuilding", "projectThemeSportsSubThemeTeamBuilding"));
		sports.addSubTheme(new SubTheme("events", "projectThemeSportsSubThemeEvent"));
		sports.addSubTheme(new SubTheme("capacity", "projectThemeSportsSubThemeCapacity"));
		sports.addSubTheme(new SubTheme("promotion", "projectThemeSportsSubThemePromotion"));
		sports.addSubTheme(new SubTheme("other", "projectThemeSportsSubThemeOther"));
		projectThemes.add(sports);
		
		Theme society = new Theme("society", "projectThemeSociety");
		society.addSubTheme(new SubTheme("urbanism", "projectThemeSocietySubThemeUrbanism"));
		society.addSubTheme(new SubTheme("feminism", "projectThemeSocietySubThemeFeminism"));
		society.addSubTheme(new SubTheme("drugs", "projectThemeSocietySubThemeDrugs"));
		society.addSubTheme(new SubTheme("entertainment", "projectThemeSocietySubThemeEntertainment"));
		society.addSubTheme(new SubTheme("other", "projectThemeSocietySubThemeOther"));
		projectThemes.add(society);
		
		
		Theme other = new Theme("other", "projectThemeOther");
		other.addSubTheme(new SubTheme("religion", "projectThemeOtherSubThemeReligion"));
		other.addSubTheme(new SubTheme("employment", "projectThemeOtherSubThemeEmployment"));
		other.addSubTheme(new SubTheme("other", "projectThemeOtherSubThemeOther"));
		projectThemes.add(other);
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

	public String getDateDisplayFormat() {
		return "dd/MM/yyyy";
	}

	public static String getDateDisplayFormat_Static() {
		return getStaticConfig().getDateDisplayFormat();
	}

	public String getDateTimeDisplayFormat() {
		return "dd/MM/yyyy HH:mm";
	}

	/**
	 * @return the number of message to display per page in the inbox and outbox pages
	 */
	public int getInboxOutboxPageSize() {
		return 12;
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

	public List<Theme> getProjectThemes() {
		return projectThemes;
	}

	public int getMaxNumberOfDisplayedProjectTags() {
		return maxNumberOfDisplayedProjectTag;
	}

	public long getHowLongIsABitInMillis() {
		return howLongIsABitInMillis;
	}

}
