package web.utils;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
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
					languageToCodeMap = new HashMap<String, HashMap<String,String>>();
					codeToLanguageMap = new HashMap<String, HashMap<String,String>>();

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


	//////////////////////////////////////
	// project themes
	
	
	public static void addProjectThemesToRenderArgs(SessionWrapper sessionWrapper, RenderArgs renderArgs) {
		renderArgs.put("allThemes", getConfig().getThemes());
	}

	
	
	public static void addProjectJsonThemesToRenderArgs(SessionWrapper sessionWrapper, RenderArgs renderArgs, String languageCode) {
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
	

	
	public static<K> Set<K> jsonToSetOfStuf(String jsonString, Class<K[]> classType) {
		HashSet<K> result = new HashSet<K>();
		
		if (!Strings.isNullOrEmpty(jsonString)) {
			
			try {
				for (K stuff : jsonMapper.readValue(jsonString, classType)) {
					result.add(stuff);
				}
			} catch (Exception e) {
				logger.log(Level.WARNING, "Could not transform inconming json string values into set of stuff => returnin empty set instead. Original string was: " + jsonString, e);
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
	
	
	//////////////////////////////////
	public static String formatDate(Date date) {
		
		if (date == null) {
			return "";
		} 

		return new SimpleDateFormat(getConfig().getDateDisplayFormat()).format(date );	
	}
	

	public static String formatDateWithTime(Date date) {
		if (date == null) {
			return "";
		} 
		return new SimpleDateFormat(getConfig().getDateTimeDisplayFormat()).format(date );	
	}

	
	public static Date convertStringToDate(String dateStr)  {
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
