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

import org.cloudfoundry.org.codehaus.jackson.JsonParseException;
import org.cloudfoundry.org.codehaus.jackson.map.JsonMappingException;
import org.cloudfoundry.org.codehaus.jackson.map.ObjectMapper;

import com.google.gson.JsonElement;
import com.svend.dab.core.beans.Config;
import com.svend.dab.core.beans.profile.Contact;
import com.svend.dab.core.beans.profile.UserProfile;

import controllers.BeanProvider;

import models.altermotif.MappedValue;

public class Utils {

	private static Logger logger = Logger.getLogger(Utils.class.getName());

	private static HashMap<String, List<MappedValue>> allPossibleLanguageNames = null;
	
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
	// ----------------------------------

	public static List<MappedValue> getAllPossibleLanguageNames(String inLanguage) {

		if (allPossibleLanguageNames == null) {
			synchronized (Utils.class) {
				if (allPossibleLanguageNames == null) {

					allPossibleLanguageNames = new HashMap<String, List<MappedValue>>();

					for (filenames propertyFileName : filenames.values()) {
						
						List<MappedValue> addedListOfNames = new LinkedList<MappedValue>();

						try {
							InputStream is = Utils.class.getResourceAsStream(propertyFileName.filename);
							Properties languageNamesProp = new Properties();
							languageNamesProp.load(is);
							
							for (Object languageName : languageNamesProp.keySet()) {
								addedListOfNames.add(new MappedValue((String) languageName, languageNamesProp.getProperty((String) languageName)));
							}
							
							allPossibleLanguageNames.put(propertyFileName.toString(), addedListOfNames);
							
						} catch (IOException e) {
							logger.log(Level.WARNING, "Could not load languages names as jar resource", e);
						}

					}
				}
			}
		}

		return allPossibleLanguageNames.get(inLanguage);
	}
	
	
	public static Set<String> jsonToSetOfStrings(String orginal) {
		try {
			return jsonMapper.readValue(orginal, Set.class);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Could not parse list of STING JSON => returning empty list instead: " + orginal);
			return new HashSet<String>();
		}
	}
	
	
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
