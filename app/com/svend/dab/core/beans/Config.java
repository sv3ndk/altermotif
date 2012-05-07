package com.svend.dab.core.beans;

import java.util.LinkedList;
import java.util.List;

import models.altermotif.projects.theme.SubTheme;
import models.altermotif.projects.theme.Theme;

import org.springframework.stereotype.Component;

import play.i18n.Messages;
import play.modules.spring.SpringPlugin;
import controllers.BeanProvider;

/**
 * 
 * THis is meant to be replaced by a real external config
 * 
 * @author svend
 * 
 */
@Component("config")
public class Config {

	private static Config staticConfig;

	// when generating the S3 presigned links, what delay do we set, in millis
	private final long cvExpirationDelayInMillis = 1000 * 60 * 60;

	private final long photoExpirationDelayInMillis = 1000 * 60 * 60;

	private final long maxUploadedPhotoSizeInBytes = 5l * 1024l * 1024;

	private final long maxUploadedCVSizeInBytes = 3l * 1024 * 1024;
	
	private final int maxNumberOfPhotosInProject = 20;
	
	private final int maxNumberOfPhotosInGroup = 20;
	
	private final int maxNumberOfPhotosInProfile = 20;

	private final int maxNumberOfDisplayedTags = 35;

	// some pages polls for boolean values to the server to know if some data is outdate (and if so, refresh whatever is approapriate). This is the period, in
	// millis, of the polling
	private final long freshnessPollingPeriodMillis = 5000;

	private final long howLongIsABitInMillis = 750;

	private final List<Theme> projectThemes = new LinkedList<Theme>();

	// TODO: it should be possible to deduce this from the deployment environment
	private final String altermotifBaseUrl = "http://altermotif.cloudfoundry.com";

	private final Location defaultSearchReferenceLocation = new Location("City of Brussels, Belgium", "50.8503396", "4.351710300000036");

	private final int SOLR_COMMIT_WITHIN = 30000;
	
	
	// ------------------------------------------------------
	// ------------------------------------------------------

	public Config() {

		Theme animalRights = new Theme("animalRights", "projectThemeAnimalRights");
		animalRights.addSubTheme(new SubTheme("campaigning", "projectThemeAnimalRightsSubThemeCampaigning"));
		animalRights.addSubTheme(new SubTheme("foodIndustry", "projectThemeAnimalRightsSubThemeFoodIndustry"));
		animalRights.addSubTheme(new SubTheme("strayAnimals", "projectThemeAnimalRightsSubThemeStrayAnimals"));
		animalRights.addSubTheme(new SubTheme("testingonAnimals", "projectThemeAnimalRightsSubThemeTestingonAnimals"));
		animalRights.addSubTheme(new SubTheme("other", "projectThemeAnimalRightsSubThemeOther"));
		projectThemes.add(animalRights);

		Theme arts = new Theme("arts", "projectThemeArts");
		arts.addSubTheme(new SubTheme("architecture", "projectThemeArtsSubThemeArchitecture"));
		arts.addSubTheme(new SubTheme("calligraphy", "projectThemeArtsSubThemeCalligraphy"));
		arts.addSubTheme(new SubTheme("cinema", "projectThemeArtsSubThemeCinema"));
		arts.addSubTheme(new SubTheme("comics", "projectThemeArtsSubThemeComics"));
		arts.addSubTheme(new SubTheme("conceptualArts", "projectThemeArtsSubThemeConceptualArt"));
		arts.addSubTheme(new SubTheme("dance", "projectThemeArtsSubThemeDance"));
		arts.addSubTheme(new SubTheme("exhibitions", "projectThemeArtsSubThemeExhibitions"));
		arts.addSubTheme(new SubTheme("experimental", "projectThemeArtsSubThemeExperimentalArt"));
		arts.addSubTheme(new SubTheme("fiberArt", "projectThemeArtsSubThemeFiberArt"));
		arts.addSubTheme(new SubTheme("illustration", "projectThemeArtsSubThemeIllustration"));
		arts.addSubTheme(new SubTheme("imagePrint", "projectThemeArtsSubThemeImagingPrints"));
		arts.addSubTheme(new SubTheme("literature", "projectThemeArtsSubThemeLiterature"));
		arts.addSubTheme(new SubTheme("mosaic", "projectThemeArtsSubThemeMosaics"));
		arts.addSubTheme(new SubTheme("paintingDrawing", "projectThemeArtsSubThemePaintingDrawing"));
		arts.addSubTheme(new SubTheme("photography", "projectThemeArtsSubThemePhotography"));
		arts.addSubTheme(new SubTheme("poetry", "projectThemeArtsSubThemePoetry"));
		arts.addSubTheme(new SubTheme("sculpture", "projectThemeArtsSubThemeSculpture"));
		arts.addSubTheme(new SubTheme("streetArt", "projectThemeArtsSubThemeStreetArt"));
		arts.addSubTheme(new SubTheme("theatre", "projectThemeArtsSubThemeTheatre"));
		arts.addSubTheme(new SubTheme("other", "projectThemeArtsSubThemeOther"));
		projectThemes.add(arts);

		Theme culture = new Theme("culture", "projectThemeCulture");
		culture.addSubTheme(new SubTheme("education", "projectThemeCultureSubThemeEducation"));
		culture.addSubTheme(new SubTheme("events", "projectThemeCultureSubThemeEvents"));
		culture.addSubTheme(new SubTheme("film", "projectThemeCultureSubThemeFilms"));
		culture.addSubTheme(new SubTheme("internet", "projectThemeCultureSubThemeInternet"));
		culture.addSubTheme(new SubTheme("music", "projectThemeCultureSubThemeMusic"));
		culture.addSubTheme(new SubTheme("journalism", "projectThemeCultureSubThemeJournalism"));
		culture.addSubTheme(new SubTheme("printedMedia", "projectThemeCultureSubThemePrintedMedia"));
		culture.addSubTheme(new SubTheme("games", "projectThemeCultureSubThemeGames"));
		culture.addSubTheme(new SubTheme("other", "projectThemeCultureSubThemeOther"));
		projectThemes.add(culture);

		Theme development = new Theme("development", "projectThemeDevelopment");
		development.addSubTheme(new SubTheme("fairtrade", "projectThemeDevelopmentSubThemeFairTrade"));
		development.addSubTheme(new SubTheme("northsouth", "projectThemeDevelopmentSubThemeNorthSouthRelations"));
		development.addSubTheme(new SubTheme("propaganda", "projectThemeDevelopmentSubThemePropaganda"));
		development.addSubTheme(new SubTheme("rethinking", "projectThemeDevelopmentSubThemeRethinkingEstablishedIdeas"));
		development.addSubTheme(new SubTheme("sustainable", "projectThemeDevelopmentSubThemeSustainableLiving"));
		development.addSubTheme(new SubTheme("other", "projectThemeDevelopmentSubThemeOther"));
		projectThemes.add(development);

		Theme environment = new Theme("environment", "projectThemeEnvironment");
		environment.addSubTheme(new SubTheme("agriculture", "projectThemeEnvironmentSubThemeAgriculture"));
		environment.addSubTheme(new SubTheme("awareness", "projectThemeEnvironmentSubThemeAwarenessRaising"));
		environment.addSubTheme(new SubTheme("lobbying", "projectThemeEnvironmentSubThemeLobbying"));
		environment.addSubTheme(new SubTheme("climate", "projectThemeEnvironmentSubThemeClimateChange"));
		environment.addSubTheme(new SubTheme("conservation", "projectThemeEnvironmentSubThemeConservation"));
		environment.addSubTheme(new SubTheme("energy", "projectThemeEnvironmentSubThemeEnergy"));
		environment.addSubTheme(new SubTheme("recycling", "projectThemeEnvironmentSubThemeRecyclingReuse"));
		environment.addSubTheme(new SubTheme("research", "projectThemeEnvironmentSubThemeResearch"));
		environment.addSubTheme(new SubTheme("waste", "projectThemeEnvironmentSubThemeWasteManagement"));
		environment.addSubTheme(new SubTheme("other", "projectThemeEnvironmentSubThemeOther"));
		projectThemes.add(environment);

		Theme science = new Theme("science", "projectThemeScience");
		science.addSubTheme(new SubTheme("education", "projectThemeScienceSubThemeEducation"));
		science.addSubTheme(new SubTheme("research", "projectThemeScienceSubThemeResearch"));
		science.addSubTheme(new SubTheme("technology", "projectThemeScienceSubThemeTechnology"));
		science.addSubTheme(new SubTheme("other", "projectThemeScienceSubThemeOther"));
		projectThemes.add(science);

		Theme society = new Theme("society", "projectThemeSociety");
		society.addSubTheme(new SubTheme("elderly", "projectThemeSocietySubThemeElderly"));
		society.addSubTheme(new SubTheme("employment", "projectThemeSocietySubThemeEmployment"));
		society.addSubTheme(new SubTheme("gathering", "projectThemeSocietySubThemeGatherings"));
		society.addSubTheme(new SubTheme("peace", "projectThemeSocietySubThemePeaceDisarmement"));
		society.addSubTheme(new SubTheme("poverty", "projectThemeSocietySubThemePoverty"));
		society.addSubTheme(new SubTheme("cohesion", "projectThemeSocietySubThemeSocialCohesion"));
		society.addSubTheme(new SubTheme("urbanism", "projectThemeSocietySubThemeUrbanism"));
		society.addSubTheme(new SubTheme("women", "projectThemeSocietySubThemeWomen"));
		society.addSubTheme(new SubTheme("youth", "projectThemeSocietySubThemeYouth"));
		society.addSubTheme(new SubTheme("other", "projectThemeSocietySubThemeOther"));
		projectThemes.add(society);

		Theme sports = new Theme("sports", "projectThemeSports");
		sports.addSubTheme(new SubTheme("teamBuilding", "projectThemeSportsSubThemeTeamBuilding"));
		sports.addSubTheme(new SubTheme("events", "projectThemeSportsSubThemeEvent"));
		sports.addSubTheme(new SubTheme("education", "projectThemeSportsSubThemeEducation"));
		sports.addSubTheme(new SubTheme("other", "projectThemeSportsSubThemeOther"));
		projectThemes.add(sports);

		Theme travel = new Theme("travel", "projectThemeTravel");
		travel.addSubTheme(new SubTheme("ecotourism", "projectThemeTravelSubThemeEcoTourism"));
		travel.addSubTheme(new SubTheme("exploration", "projectThemeTravelSubThemeExploration"));
		travel.addSubTheme(new SubTheme("travingroup", "projectThemeTravelSubThemeTravellingInGroup"));
		travel.addSubTheme(new SubTheme("exchange", "projectThemeTravelSubThemeTravellingIntExchange"));
		travel.addSubTheme(new SubTheme("other", "projectThemeTravelSubThemeOther"));
		projectThemes.add(travel);

	}

	// ------------------------------------------------------
	// ------------------------------------------------------
	// ------------------------------------------------------

	public enum DEPLOYMENT_MODE {
		devOnline, 
		devLocal,
		prod
	}
	
	public DEPLOYMENT_MODE getDeploymentMode() {
		return DEPLOYMENT_MODE.valueOf(SpringPlugin.applicationContext.getEnvironment().getActiveProfiles()[0]);
	}
	
	
	
	///////////////////////
	
	
	public List<Theme> getLocalizedThemes(String languageCode) {
		List<Theme> localizedThemes = new LinkedList<Theme>();

		for (Theme orgininalTheme : getThemes()) {
			Theme localizedTheme = new Theme(orgininalTheme.getId(), Messages.getMessage(languageCode, orgininalTheme.getLabel(), null));
			for (SubTheme st : orgininalTheme.getSubThemes()) {
				SubTheme localizedSubTheme = new SubTheme(st.getId(), Messages.getMessage(languageCode, st.getLabel(), null));
				localizedTheme.addSubTheme(localizedSubTheme);
			}
			localizedThemes.add(localizedTheme);
		}

		return localizedThemes;
	}

	private static Config getStaticConfig() {
		// TODO: clean up: josuah block static holder here...
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

	public List<Theme> getThemes() {
		return projectThemes;
	}

	public int getMaxNumberOfDisplayedTags() {
		return maxNumberOfDisplayedTags;
	}

	public long getHowLongIsABitInMillis() {
		return howLongIsABitInMillis;
	}

	public String getAltermotifBaseUrl() {
		return altermotifBaseUrl;
	}

	public Location getDefaultSearchReferenceLocation() {
		return defaultSearchReferenceLocation;
	}

	public int getMaxNumberOfPhotosInProject() {
		return maxNumberOfPhotosInProject;
	}

	public int getMaxNumberOfPhotosInProfile() {
		return maxNumberOfPhotosInProfile;
	}

	public int getMaxNumberOfPhotosInGroup() {
		return maxNumberOfPhotosInGroup;
	}



	public int getSolrCommitWithin() {
		return SOLR_COMMIT_WITHIN;
	}


}