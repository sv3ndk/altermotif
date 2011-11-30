package models.altermotif.profile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import play.data.validation.Email;
import play.data.validation.Match;
import play.data.validation.Required;
import web.utils.Utils;

import com.svend.dab.core.beans.profile.PersonalData;
import com.svend.dab.core.beans.profile.UserProfile;

import controllers.BeanProvider;

/**
 * @author Svend
 * 
 */
public class CreatedProfile {
	
	final static String DATE_FORMAT = BeanProvider.getConfig().getDateDisplayFormat();
	
	private static Logger logger = Logger.getLogger(CreatedProfile.class.getName());

	@Required
	@Match(value="[a-z0-9]*", message="errorUsernameFormat")
	private String username;

	@Required
	private String firstpassword;

	@Required
	private String secondpassword;

	@Required
	private String firstName;

	@Required
	private String lastName;

	@Required
	private String dateOfBirthStr;

	@Required
	@Email( message="incorrectEmailFormat") 
	private String email;
	
	
	public CreatedProfile() {
		super();
	}

	public CreatedProfile(UserProfile profile) {

		setUsername(profile.getUsername());
		setFirstpassword(profile.getPdata().getPassword());
		setSecondpassword(profile.getPdata().getPassword());
		
		firstName = profile.getPdata().getFirstName();
		lastName = profile.getPdata().getLastName();
		applyDateOfBirth(profile.getPdata().getDateOfBirth());
		email = profile.getPdata().getEmail();

	}
	


	// ----------------------------

	public void applyDateOfBirth(Date date) {
		if (date != null) {
			dateOfBirthStr = new SimpleDateFormat(DATE_FORMAT).format(date);
		}
	}
	
	public Date getDateOfBirth() throws ParseException {
		return Utils.convertStringToDate(getDateOfBirthStr());
	}

	
	
	public boolean arePasswordEquals() {
		if (firstpassword == null) {
			return secondpassword == null;
		}
		return firstpassword.equals(secondpassword);
	}
	
	
	public UserProfile toUserProfile() {
		UserProfile profile = new UserProfile();
		applyToProfile(profile);
		profile.getPrivacySettings().setProfileActive(true);
		return profile;
	}
	
	
	public void applyToProfile(UserProfile profile) {
		profile.setUsername(username);
		applyToPData(profile.getPdata());
	}
	
	public void applyToPData(PersonalData pdata) {
		
		pdata.setPassword(firstpassword);
		pdata.setFirstName(firstName);
		pdata.setLastName(lastName);
		pdata.setEmail(email);
		try {
			pdata.setDateOfBirth(getDateOfBirth());
		} catch (ParseException e) {
			// TODO log here (this should be impossible anywqy..)
			logger.log(Level.WARNING, "could not convert dateOfBirthStr to date", e);
		}
		
	}

	

	// ----------------------------

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFirstpassword() {
		return firstpassword;
	}

	public void setFirstpassword(String firstpassword) {
		this.firstpassword = firstpassword;
	}

	public String getSecondpassword() {
		return secondpassword;
	}

	public void setSecondpassword(String secondpassword) {
		this.secondpassword = secondpassword;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstname) {
		this.firstName = firstname;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDateOfBirthStr() {
		return dateOfBirthStr;
	}

	public void setDateOfBirthStr(String dateOfBirthStr) {
		this.dateOfBirthStr = dateOfBirthStr;
	}


	

}
