package controllers;

import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.svend.dab.core.IUserProfileService;
import com.svend.dab.core.UserProfileService;

import com.svend.dab.core.beans.profile.UserProfile;

import controllers.profile.ProfileHome;
import controllers.validators.DabValidators;
import models.altermotif.SessionWrapper;
import models.altermotif.profile.CaptchaString;
import models.altermotif.profile.CreatedProfile;
import play.data.validation.Required;
import play.data.validation.Validation;
import play.mvc.Controller;
import play.modules.spring.Spring;

/**
 * @author Svend
 * 
 */
public class Enter extends DabController {

	private static Logger logger = Logger.getLogger(Enter.class.getName());
	
	private static String CREAYED_PROFILE_RENDERARG_NAME = "createdProfile";
	
	// -----------------------------------
	// Login

	// this navigates to the login page
	public static void login() {
		render();
	}

	public static void doLogin(@Required String username, @Required String password) {
		if (Validation.hasErrors()) {
			params.flash();
			Validation.keep();
			login();
		}

		UserProfile userProfile = BeanProvider.getUserProfileService().loadUserProfile(username, false);

		if (userProfile != null && password.equals(userProfile.getPdata().getPassword())) {
			BeanProvider.getUserProfileService().loggedIn(userProfile);
			getSessionWrapper().setLoggedInUserProfileId(username);
			ProfileHome.profileHome();
		} else {
			Validation.addError("password", "loginErrorDetailMessage", "");
			params.flash();
			Validation.keep();
			login();
		}
	}
	
	
	/**
	 * 
	 */
	public static void doLogout() {
		getSessionWrapper().setLoggedInUserProfileId(null);
		Application.index();
	}

	// -----------------------------------
	// Register

	// this navigates to the register page
	public static void register() {

		CaptchaString captchaString = new CaptchaString();
		captchaString.generateRandomCaptchaLetter();
		captchaString.putInArgsList(renderArgs);
		
		renderArgs.put(CREAYED_PROFILE_RENDERARG_NAME, new CreatedProfile());

		render();
	}

	/**
	 * @param createdProfile
	 */
	public static void doRegister(CreatedProfile createdProfile) {

		DabValidators.postValidateCreatedProfile(createdProfile, CREAYED_PROFILE_RENDERARG_NAME,  validation, flash);

		if (Validation.hasErrors()) {
			params.flash();
			Validation.keep();
			register();
		} else {
			BeanProvider.getUserProfileService().registerUser(createdProfile.toUserProfile());
			getSessionWrapper().setLoggedInUserProfileId(createdProfile.getUsername());
			ProfileHome.profileHome();
		}
	}

}
