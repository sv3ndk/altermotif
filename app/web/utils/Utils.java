package web.utils;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import models.altermotif.MappedValue;
import models.altermotif.SessionWrapper;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import play.mvc.Scope.RenderArgs;

import com.google.common.base.Strings;
import com.svend.dab.core.beans.Config;
import com.svend.dab.core.beans.profile.UserProfile;
import com.svend.dab.core.beans.projects.RankedTag;
import com.svend.dab.core.beans.projects.TagCount;

import controllers.BeanProvider;

public class Utils {

	private static Logger logger = Logger.getLogger(Utils.class.getName());

	// both these contain the same thing, but the second is more practicel fro use in the back end (the first one is for the from end)
	private static HashMap<String, List<MappedValue>> allPossibleLanguageNames = null;

	// map of language name to language code
	private static HashMap<String, HashMap<String, String>> languageToCodeMap = null;
	private static HashMap<String, HashMap<String, String>> codeToLanguageMap = null;

	private static ObjectMapper jsonMapper = new ObjectMapper();

	// do not use this directly: use the getter instead (lazy init..)
	private static Config config;

	// ----------------------------------
	// ----------------------------------

	private enum filenames {

		en("languagenames_en_iso8859.properties"), fr("languagenames_fr_iso8859.properties"), nl("languagenames_nl_iso8859.properties");

		private filenames(String fn) {
			this.filename = fn;
		}

		final String filename;

	}

	// ----------------------------------
	// language stuff

	public static void addAllPossibleLanguageNamesToRenderArgs(SessionWrapper sessionWrapper, RenderArgs renderArgs) {
		List<MappedValue> allPossibleLanguageNames = Utils.getAllPossibleLanguageNames(sessionWrapper.getSelectedLg());
		try {
			renderArgs.put("allPossibleLanguageNames", jsonMapper.writeValueAsString(allPossibleLanguageNames));
		} catch (Exception e) {
			logger.log(Level.WARNING, "Could not put allPossibleLanguageNames in model", e);
		}
	}

	public static List<MappedValue> getAllPossibleLanguageNames(String inLanguage) {

		if (allPossibleLanguageNames == null) {
			synchronized (Utils.class) {
				if (allPossibleLanguageNames == null) {

					allPossibleLanguageNames = new HashMap<String, List<MappedValue>>();
					languageToCodeMap = new HashMap<String, HashMap<String, String>>();
					codeToLanguageMap = new HashMap<String, HashMap<String, String>>();

					for (filenames propertyFileName : filenames.values()) {

						List<MappedValue> addedListOfNames = new LinkedList<MappedValue>();
						HashMap<String, String> addedLanguageToCodeMap = new HashMap<String, String>();
						HashMap<String, String> addedCodeMap = new HashMap<String, String>();

						try {
							InputStream is = Utils.class.getResourceAsStream(propertyFileName.filename);
							Properties languageNamesProp = new Properties();
							languageNamesProp.load(is);

							for (Object languageName : languageNamesProp.keySet()) {
								String code = (String) languageName;
								String name = languageNamesProp.getProperty(code);

								addedListOfNames.add(new MappedValue(code, name));
								addedLanguageToCodeMap.put(name, code);
								addedCodeMap.put(code, name);
							}

							allPossibleLanguageNames.put(propertyFileName.toString(), addedListOfNames);
							languageToCodeMap.put(propertyFileName.toString(), addedLanguageToCodeMap);
							codeToLanguageMap.put(propertyFileName.toString(), addedCodeMap);

						} catch (IOException e) {
							logger.log(Level.WARNING, "Could not load languages names as jar resource", e);
						}

					}
				}
			}
		}

		return allPossibleLanguageNames.get(inLanguage);
	}

	public static HashMap<String, String> getLangugeToCodeMap(String inLanguage) {
		// makes sure the lazy loader is executed...
		getAllPossibleLanguageNames(inLanguage);
		return languageToCodeMap.get(inLanguage);
	}

	public static HashMap<String, String> getCodeToLanguageMap(String inLanguage) {
		// makes sure the lazy loader is executed...
		getAllPossibleLanguageNames(inLanguage);
		return codeToLanguageMap.get(inLanguage);
	}

	/**
	 * @param addedLanguageName
	 * @return
	 */
	public static String resolveCodeOfLanguage(String languageHumanName, String userLanguage) {
		HashMap<String, String> languagesToCodeMap = getLangugeToCodeMap(userLanguage);
		if (languagesToCodeMap == null) {
			return "";
		} else {
			return languagesToCodeMap.get(languageHumanName);
		}
	}

	public static String resolveLanguageOfCode(String code, String userLanguage) {
		HashMap<String, String> codeToLanguage = getCodeToLanguageMap(userLanguage);
		if (codeToLanguage == null) {
			return "";
		} else {
			return codeToLanguage.get(code);
		}
	}
	
	
	

	// ////////////////////////////////////
	// themes (identical for groups and projects)

	public static void addThemesToRenderArgs(SessionWrapper sessionWrapper, RenderArgs renderArgs) {
		renderArgs.put("allThemes", getConfig().getThemes());
	}

	public static void addJsonThemesToRenderArgs(SessionWrapper sessionWrapper, RenderArgs renderArgs, String languageCode) {
		renderArgs.put("allThemesJson", objectToJsonString(getConfig().getLocalizedThemes(languageCode)));
	}

	// ...-------------------
	// JSON stuff

	public static Set<String> jsonToSetOfStrings(String orginal) {
		try {
			return jsonMapper.readValue(orginal, Set.class);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Could not parse list of STING JSON => returning empty list instead: " + orginal);
			return new HashSet<String>();
		}
	}

	public static <K> Set<K> jsonToSetOfStuf(String jsonString, Class<K[]> classType) {
		HashSet<K> result = new HashSet<K>();

		if (!Strings.isNullOrEmpty(jsonString)) {

			try {
				for (K stuff : jsonMapper.readValue(jsonString, classType)) {
					result.add(stuff);
				}
			} catch (Exception e) {
				logger.log(Level.WARNING,
						"Could not transform inconming json string values into set of stuff => returnin empty set instead. Original string was: " + jsonString,
						e);
			}
		}
		return result;
	}

	public static String objectToJsonString(Object any) {
		if (any == null) {
			return "";
		} else {
			try {
				return jsonMapper.writeValueAsString(any);
			} catch (Exception e) {
				logger.log(Level.WARNING, "Could not convert object to jSON string => considering empty string instead. This will lead to data loss...");
				return "";
			}
		}
	}
	
	////////////////////////////////////
	
	public static void addDefaultReferenceLocationToRenderArgs(RenderArgs renderArgs, String loggedUserId) {
		
		boolean useSearchReferenceLocationFromConfig = true;
		
		if (Strings.isNullOrEmpty(loggedUserId)) {
			UserProfile loggedInProfile = BeanProvider.getUserProfileService().loadUserProfile(loggedUserId, false);
			if (loggedInProfile != null && loggedInProfile.getPdata().getLocation() != null) {
				renderArgs.put("defaultRefenceLocation", loggedInProfile.getPdata().getLocation());
				renderArgs.put("defaultReferenceLatitude", loggedInProfile.getPdata().getLocationLat());
				renderArgs.put("defaultReferenceLongitude", loggedInProfile.getPdata().getLocationLong());
				useSearchReferenceLocationFromConfig = false;
			}
		}
		if (useSearchReferenceLocationFromConfig) {
			Config config = BeanProvider.getConfig();
			renderArgs.put("defaultRefenceLocation", config.getDefaultSearchReferenceLocation().getLocation());
			renderArgs.put("defaultReferenceLatitude", config.getDefaultSearchReferenceLocation().getLatitude());
			renderArgs.put("defaultReferenceLongitude", config.getDefaultSearchReferenceLocation().getLongitude());
		}
	}


	// ////////////////////////////////
	public static String formatDate(Date date) {

		if (date == null) {
			return "";
		}

		return new SimpleDateFormat(getConfig().getDateDisplayFormat()).format(date);
	}

	public static String formatDateWithTime(Date date) {
		if (date == null) {
			return "";
		}
		return new SimpleDateFormat(getConfig().getDateTimeDisplayFormat()).format(date);
	}

	public static Date convertStringToDate(String dateStr) {
		if (Strings.isNullOrEmpty(dateStr)) {
			return null;
		} else {
			try {
				return new SimpleDateFormat(getConfig().getDateDisplayFormat()).parse(dateStr);
			} catch (Exception e) {
				logger.log(Level.WARNING, "could not parse date: " + dateStr + " => considering null");
				return null;
			}
		}
	}

	public static String sanitizedUrl(String website) {

		if (website == null) {
			return "";
		}

		if (website.length() < 7 || !website.substring(0, 7).equalsIgnoreCase("http://")) {
			return "http://" + website;
		} else {
			return website;
		}
	}

	public static void waitABit() {

		try {
			Thread.sleep(getConfig().getHowLongIsABitInMillis());
		} catch (Exception e) {
			logger.log(Level.WARNING, "interrupted while waiting", e);
		}
	}
	
	
	
	public static List<RankedTag> rankCountedTags( List<TagCount> rawTags) {
		List<RankedTag> rankedTags = new LinkedList<RankedTag>();
		
		if (rawTags != null && !rawTags.isEmpty()) {

			if (rawTags.size() == 1) {
				rankedTags.add(new RankedTag(rawTags.get(0).getTag(), 0));
			} else {
				int highestFreq = rawTags.get(0).getValue();
				int lowestFreq = rawTags.get(rawTags.size() - 1).getValue();

				float rankStep = (highestFreq - lowestFreq) / 5;

				for (TagCount rawTag : rawTags) {
					if (rawTag.getValue() > lowestFreq + 4 * rankStep) {
						rankedTags.add(new RankedTag(rawTag.getTag(), 0));
					} else if (rawTag.getValue() > lowestFreq + 3 * rankStep) {
						rankedTags.add(new RankedTag(rawTag.getTag(), 1));
					} else if (rawTag.getValue() > lowestFreq + 2 * rankStep) {
						rankedTags.add(new RankedTag(rawTag.getTag(), 2));
					} else if (rawTag.getValue() > lowestFreq + rankStep) {
						rankedTags.add(new RankedTag(rawTag.getTag(), 3));
					} else {
						rankedTags.add(new RankedTag(rawTag.getTag(), 4));
					}
				}
			}
		}

		Collections.sort(rankedTags, new Comparator<RankedTag>() {

			public int compare(RankedTag tag1, RankedTag tag2) {

				if ((tag1 == null || tag1.getTag() == null) && (tag2 == null || tag2.getTag() == null)) {
					return 0;
				}

				if (tag1 == null || tag1.getTag() == null) {
					return -1;
				}

				if (tag2 == null || tag2.getTag() == null) {
					return 1;
				}

				return tag1.getTag().compareTo(tag2.getTag());
			}
		});
		return rankedTags;
	}

	// ----------------------------------
	// ----------------------------------

	private static Config getConfig() {

		if (config == null) {
			synchronized (Utils.class) {
				if (config == null) {
					config = BeanProvider.getConfig();
				}
			}
		}

		return config;
	}

	public static long countElapsedMillisSince(Date creationDate) {

		if (creationDate == null) {
			return 0;
		}

		return new Date().getTime() - creationDate.getTime();

	}


}
