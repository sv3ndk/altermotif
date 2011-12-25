package models.altermotif.profile;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.cloudfoundry.org.codehaus.jackson.map.ObjectMapper;

import play.data.validation.Required;

import com.svend.dab.core.beans.profile.Language;
import com.svend.dab.core.beans.profile.PersonalData;
import com.svend.dab.core.beans.profile.UserProfile;

/**
 * @author Svend
 * 
 */
public class EditedProfile extends CreatedProfile {

	private static Logger logger = Logger.getLogger(EditedProfile.class.getName());

	@Required
	private String location;
	@Required
	private String locationLat;
	@Required
	private String locationLong;

	private String personalObjective;
	private String personalDescription;
	private String personalPhilosophy;
	private String personalAssets;

	private String website;

	private String gender;

	@Required(message = "atLeastOneLanguage")
	private String languagesJson;

	// translated version of the json thing
	private List<Language> cachedLanguages;

	public EditedProfile(UserProfile profile) {
		super(profile);

		setLocation(profile.getPdata().getLocation());
		setLocationLat(profile.getPdata().getLocationLat());
		setLocationLong(profile.getPdata().getLocationLong());

		setPersonalObjective(profile.getPdata().getPersonalObjective());
		setPersonalDescription(profile.getPdata().getPersonalDescription());
		setPersonalPhilosophy(profile.getPdata().getPersonalPhilosophy());
		setPersonalAssets(profile.getPdata().getPersonalAssets());
		setGender(profile.getPdata().getGender());

		setWebsite(profile.getPdata().getWebsite());

		List<Language> mappedLanguages = new LinkedList<Language>();
		if (profile.getPdata().getLanguages() != null) {
			for (Language language : profile.getPdata().getLanguages()) {
				mappedLanguages.add(language);
			}
		}

		ObjectMapper jsonMapper = new ObjectMapper();
		try {
			languagesJson = jsonMapper.writeValueAsString(mappedLanguages);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Could not marshall languages to a json String", e);
		}

	}

	@Override
	public void applyToPData(PersonalData pdata) {

		super.applyToPData(pdata);

		pdata.setLocation(location);
		pdata.setLocationLat(locationLat);
		pdata.setLocationLong(locationLong);

		pdata.setPersonalObjective(personalObjective);
		pdata.setPersonalDescription(personalDescription);
		pdata.setPersonalPhilosophy(personalPhilosophy);
		pdata.setPersonalAssets(personalAssets);

		pdata.setWebsite(website);

		// values should always be valid, unless if there is client side hacking => silently falling back to "U" in such case
		if ("F".equals(gender) || "M".equals(gender) || "U".equals(gender)) {
			pdata.setGender(gender);
		} else {
			pdata.setGender("U");
		}

		pdata.setLanguages(parseJsonLanguages());

	}

	public List<Language> parseJsonLanguages() {
		if (cachedLanguages == null) {
			synchronized (this) {
				if (cachedLanguages == null) {
					try {
						ObjectMapper jsonMapper = new ObjectMapper();
						cachedLanguages = Arrays.asList(jsonMapper.readValue(languagesJson, Language[].class));
					} catch (Exception e) {
						logger.log(Level.WARNING, "Could not transform inconming json language values into list of languges => keeping old value", e);
					}

				}
			}
		}

		return cachedLanguages;
	}

	// ---------------------------------
	// ---------------------------------

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLocationLat() {
		return locationLat;
	}

	public void setLocationLat(String locationLat) {
		this.locationLat = locationLat;
	}

	public String getLocationLong() {
		return locationLong;
	}

	public void setLocationLong(String locatioLong) {
		this.locationLong = locatioLong;
	}

	public String getPersonalObjective() {
		return personalObjective;
	}

	public void setPersonalObjective(String personalObjective) {
		this.personalObjective = personalObjective;
	}

	public String getPersonalDescription() {
		return personalDescription;
	}

	public void setPersonalDescription(String personalDescription) {
		this.personalDescription = personalDescription;
	}

	public String getPersonalPhilosophy() {
		return personalPhilosophy;
	}

	public void setPersonalPhilosophy(String personalPhilosophy) {
		this.personalPhilosophy = personalPhilosophy;
	}

	public String getPersonalAssets() {
		return personalAssets;
	}

	public void setPersonalAssets(String personalAssets) {
		this.personalAssets = personalAssets;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getLanguagesJson() {
		return languagesJson;
	}

	public void setLanguagesJson(String languagesJson) {
		this.languagesJson = languagesJson;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

}