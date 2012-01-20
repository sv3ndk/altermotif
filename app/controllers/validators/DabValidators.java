package controllers.validators;

import java.net.URI;
import java.net.URL;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import models.altermotif.profile.CreatedProfile;
import models.altermotif.profile.EditedProfile;
import models.altermotif.projects.EditedProject;

import org.apache.commons.collections.CollectionUtils;

import play.data.validation.Validation;
import play.mvc.Scope.Flash;
import web.utils.Utils;

import com.google.common.base.Strings;
import com.mongodb.util.Hash;

/**
 * @author Svend
 * 
 */
public class DabValidators {

	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
	private static Logger logger = Logger.getLogger(DabValidators.class.getName());

	// -----------------------------------
	//

	/**
	 * @param createdProfile
	 */
	public static void postValidateCreatedProfile(CreatedProfile createdProfile, String renderArgName, Validation validation, Flash flash) {
		validation.valid(createdProfile);

		if (!createdProfile.arePasswordEquals()) {
			Validation.addError(renderArgName + ".secondpassword", "passwordMustBeIdentical", "");
			flash.remove(renderArgName + ".firstpassword");
			flash.remove(renderArgName + ".secondpassword");
		}

		if (!createdProfile.isAcceptConditions()) {
			Validation.addError(renderArgName + ".acceptConditions", "registerMustAcceptConditionsErrorMessage", "");
		}
		
		try {
			Date dateOfBirth = createdProfile.getDateOfBirth();
			if (dateOfBirth != null) {
				if (dateOfBirth.after(new Date())) {
					throw new IllegalArgumentException("birth date cannot be in the past!");
				}
			}
		} catch (Exception exc) {
			Validation.addError(renderArgName + ".dateOfBirthStr", "errorIncorrectDate", "");
		}
	}

	/**
	 * @param editedProfile
	 * @param renderArgName
	 * @param validation
	 * @param flash
	 */
	public static void postValidateEditedProfile(EditedProfile editedProfile, String renderArgName, Validation validation, Flash flash) {
		postValidateCreatedProfile(editedProfile, renderArgName, validation, flash);

		if (!Validation.hasError("languagesJson") && (editedProfile.parseJsonLanguages() == null || editedProfile.parseJsonLanguages().isEmpty())) {
			Validation.addError(renderArgName + ".languagesJson", "atLeastOneLanguage", "");
		}

		// username is not submitted by the edit form (it is present in the bean because we re-use it from the register screen)
		// there are always at min 2 errors: the one for username + one global for editedProfile
		if (Validation.errors().size() == 2 && Validation.hasError(renderArgName + ".username")) {
			Validation.clear();
		}

		if (!Validation.hasError("location")) {
			if (Validation.hasError("locationLat")) {
				Validation.addError("location", Validation.errors("locationLat").get(0).message(), "");
			} else if (Validation.hasError("locationLong")) {
				Validation.addError("location", Validation.errors("locationLong").get(0).message(), "");
			}
		}

		if (!Strings.isNullOrEmpty(editedProfile.getWebsite()) && !isValidateHttpAddress(editedProfile.getWebsite())) {
			Validation.addError(renderArgName +".website", "illegalHttpAddressFormat", "");
		}
	}

	public static boolean isValidateHttpAddress(String address) {

		try {

			URL url = new URI(address).toURL();
			if (!(url.getProtocol().equals("http") || url.getProtocol().equals("https"))) {
				// this prevents a security attack with URL pointing to the file system of the server
				return false;
			}

		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	
	
	
	public static void validateCreatedProject(EditedProject editedProject, Validation validation, Flash flash) {

		// TODO: validate HTTP addresses
		Set<String> updatedSite = new HashSet<String>();
		for (String website : editedProject.getparsedLinks()) {
			String sanitized = Utils.sanitizedUrl(website);
			if (!Strings.isNullOrEmpty(sanitized)) {
				updatedSite.add(sanitized);
			}
		}
		editedProject.setAllLinks(updatedSite);
		
		
		Validation.valid("editedProject", editedProject);
		Validation.valid("editedProject.pdata", editedProject.getPdata());

		// in case of any error in the parsing of the incoming location, we fall back to an empty list => error message of the empty list
		// (this should never happen as this is prevalidated on js side 
		if (!Validation.hasError("editedProject.pdata.allLocationJson")) {
			if (CollectionUtils.isEmpty(editedProject.getPdata().getParsedJsonLocations())) {
				Validation.addError("editedProject.pdata.allLocationJson", "projectNewAtLeastOneMessageErrorMessage", "");
			}
		}
		
	}
	
	public static void validateEditedProject(EditedProject editedProject, Validation validation, Flash flash) {
		
		validateCreatedProject(editedProject, validation, flash);

		// discaring the errors concerning the name and the goal: in edition these are raed only; => never submitted  
		if (Validation.errors().size() == 3 && Validation.hasError("editedProject.pdata.name") && Validation.hasError("editedProject.pdata.goal")) {
			Validation.clear();
		}
		
		
	}

}
