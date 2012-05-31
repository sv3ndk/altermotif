package com.svend.dab.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.solr.common.SolrDocument;

import com.google.common.base.Strings;
import com.svend.dab.core.beans.DabUploadFailedException;
import com.svend.dab.core.beans.DabUploadFailedException.failureReason;

public class CoreTool {

	private static Logger logger = Logger.getLogger(CoreTool.class.getName());

	/**
	 * parses a String to an integer, or logs a warning and returns zero if not possible
	 * 
	 * @param string
	 * @param fieldLoggedName
	 * @return
	 */
	public static Long parseStringToLongOrDefault(String string, String fieldLoggedName, Long defaultValue) {
		try {
			return Long.parseLong(string);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Cannot parse this string as an integer: " + string + " => defaulting " + fieldLoggedName + " to " + defaultValue, e);
			return defaultValue;
		}
	}

	/**
	 * @param date
	 * @param fieldLoggedName
	 * @param defaultValue
	 * @return
	 */
	public static String formatDateToStringOrDefault(Date date, String fieldLoggedName, String defaultValue, DateFormat dateFormat) {
		if (date == null) {
			logger.log(Level.WARNING, "Cannot parse null date to as string  => defaulting " + fieldLoggedName + " to " + defaultValue);
			return "";
		} else {
			return dateFormat.format(date);
		}
	}

	/**
	 * @param date
	 * @param fieldLoggedName
	 * @param defaultValue
	 * @return
	 */
	public static String formatDateToStringOrDefault(Date date, String fieldLoggedName, String defaultValue, String dateFormat) {
		return formatDateToStringOrDefault(date, fieldLoggedName, defaultValue, new SimpleDateFormat(dateFormat));
	}

	/**
	 * @param photoContentStream
	 * @return
	 */
	public static String detectAndValidateMimeType(final InputStream photoContentStream) {
		String detectedMimeType = null;
		try {
			detectedMimeType = URLConnection.guessContentTypeFromStream(photoContentStream);
		} catch (IOException e) {
			throw new DabUploadFailedException("Error while trying to determine uploaded image mime type", failureReason.fileFormatNotAnImageError);
		}
		if (detectedMimeType == null || !detectedMimeType.startsWith("image")) {
			throw new DabUploadFailedException("This mime type is not an image: " + detectedMimeType, failureReason.fileFormatNotAnImageError);
		}
		return detectedMimeType;
	}

	/**
	 * Extracts the value of this field from this solr doc and cast it to a String, or return the defaultValue in case this field is not present
	 * 
	 * @param doc
	 * @param fieldName
	 */
	public static String getSolrFieldString(SolrDocument doc, String fieldName, String defaultVal) {
		if (doc == null || Strings.isNullOrEmpty(fieldName) || !doc.containsKey(fieldName)) {
			return defaultVal;
		}
		return (String) doc.getFieldValue(fieldName);
	}

	/**
	 * @param doc
	 * @param fieldName
	 * @param defaultVal
	 * @return
	 */
	public static Date getSolrFieldDate(SolrDocument doc, String fieldName, Date defaultVal) {
		if (doc == null || Strings.isNullOrEmpty(fieldName) || !doc.containsKey(fieldName)) {
			return defaultVal;
		}
		
		return (Date) doc.getFieldValue(fieldName);
	}
	
	public static Collection<String> getSolrFieldCollection(SolrDocument doc, String fieldName, Collection<String> defaultVal) {
		if (doc == null || Strings.isNullOrEmpty(fieldName) || !doc.containsKey(fieldName)) {
			return defaultVal;
		}
		
		return (Collection) doc.getFieldValues(fieldName);
	}

	/**
	 * Extracts the value of this field from this solr doc and cast it to a String, or return the defaultValue in case this field is not present
	 * 
	 * @param doc
	 * @param fieldName
	 */
	public static Integer getSolrFieldInteger(SolrDocument doc, String fieldName, Integer defaultVal) {
		if (doc == null || Strings.isNullOrEmpty(fieldName) || !doc.containsKey(fieldName)) {
			return defaultVal;
		}
		return (Integer) doc.getFieldValue(fieldName);
	}

}
