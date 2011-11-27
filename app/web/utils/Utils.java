package web.utils;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.cloudfoundry.org.codehaus.jackson.JsonParseException;
import org.cloudfoundry.org.codehaus.jackson.map.JsonMappingException;
import org.cloudfoundry.org.codehaus.jackson.map.ObjectMapper;

import play.mvc.Scope.RenderArgs;

import com.google.common.base.Strings;
import com.google.common.collect.ObjectArrays;
import com.google.gson.JsonElement;
import com.mongodb.util.Hash;
import com.svend.dab.core.beans.Config;
import com.svend.dab.core.beans.Location;
import com.svend.dab.core.beans.profile.Contact;
import com.svend.dab.core.beans.profile.UserProfile;

import controllers.BeanProvider;

import models.altermotif.MappedValue;
import models.altermotif.SessionWrapper;

public class Utils {

	private static Logger logger = Logger.getLogger(Utils.class.getName());

	// both these contain the same thing, but the second is more practicel fro use in the back end (the first one is for the from end)
	private static HashMap<String, List<MappedValue>> allPossibleLanguageNames = null;
	
	// map of language name to language code
	private static HashMap<String, HashMap<String, String>> allPossibleLanguageMap = null;
	
	private static ObjectMapper jsonMapper = new ObjectMapper();

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
		
		ObjectMapper mapper = new ObjectMapper();
		List<MappedValue> allPossibleLanguageNames = Utils.getAllPossibleLanguageNames(sessionWrapper.getSelectedLg());
		
		try {
			renderArgs.put("allPossibleLanguageNames", mapper.writeValueAsString(allPossibleLanguageNames));
		} catch (Exception e) {
			logger.log(Level.WARNING, "Could not put allPossibleLanguageNames in model", e);
		}
	}

	

	public static List<MappedValue> getAllPossibleLanguageNames(String inLanguage) {

		if (allPossibleLanguageNames == null) {
			synchronized (Utils.class) {
				if (allPossibleLanguageNames == null) {

					allPossibleLanguageNames = new HashMap<String, List<MappedValue>>();
					allPossibleLanguageMap = new HashMap<String, HashMap<String,String>>();

					for (filenames propertyFileName : filenames.values()) {
						
						List<MappedValue> addedListOfNames = new LinkedList<MappedValue>();
						HashMap<String, String> addedMapOfNames = new HashMap<String, String>();

						try {
							InputStream is = Utils.class.getResourceAsStream(propertyFileName.filename);
							Properties languageNamesProp = new Properties();
							languageNamesProp.load(is);
							
							for (Object languageName : languageNamesProp.keySet()) {
								String code = (String) languageName;
								String name = languageNamesProp.getProperty(code);
								
								addedListOfNames.add(new MappedValue(code, name));
								addedMapOfNames.put(name, code);
							}
							
							allPossibleLanguageNames.put(propertyFileName.toString(), addedListOfNames);
							allPossibleLanguageMap.put(propertyFileName.toString(), addedMapOfNames);
							
						} catch (IOException e) {
							logger.log(Level.WARNING, "Could not load languages names as jar resource", e);
						}

					}
				}
			}
		}

		return allPossibleLanguageNames.get(inLanguage);
	}
	
	public static HashMap<String, String> getAllPossibleLangugeMap(String inLanguage) {
		
		// makes sure the lazy loader is executed...
		getAllPossibleLanguageNames(inLanguage);
		
		return allPossibleLanguageMap.get(inLanguage);
		
	}
	
	
	/**
	 * @param addedLanguageName
	 * @return
	 */
	public static String resolveCodeOfLanguage(String languageHumanName, String userLanguage) {
		
		HashMap<String, String> languagesMap = getAllPossibleLangugeMap(userLanguage);
		
		if (languagesMap == null) {
			return "";
		} else {
			return languagesMap.get(languageHumanName);
		}


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
				ObjectMapper jsonMapper = new ObjectMapper();
				
				for (K stuff : jsonMapper.readValue(jsonString, classType)) {
					result.add(stuff);
				}
			} catch (Exception e) {
				logger.log(Level.WARNING, "Could not transform inconming json string values into set of stuff => returnin empty set instead", e);
			}
		}

		return result;
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







}
