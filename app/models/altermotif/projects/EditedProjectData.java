package models.altermotif.projects;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.cloudfoundry.org.codehaus.jackson.map.ObjectMapper;

import play.data.validation.Required;
import web.utils.Utils;

import com.svend.dab.core.beans.Location;
import com.svend.dab.core.beans.projects.Project.PROJECT_VISIBILITY;
import com.svend.dab.core.beans.projects.ProjectData;

/**
 * @author svend
 * 
 *         project data common to the creattion and to the edition screens
 * 
 */
public class EditedProjectData {

	private static Logger logger = Logger.getLogger(EditedProjectData.class.getName());

	// ////////////////////////////
	// bean fields

	@Required
	private String name;

	@Required
	private String goal;

	@Required(message = "projectNewAtLeastOneMessageErrorMessage")
	private String allLocationJson;

	private Set<Location> cachedParsedLocations;

	@Required
	private String description;

	private PROJECT_VISIBILITY descriptionVisibility = PROJECT_VISIBILITY.everybody;

	private String reason;

	private String strategy;

	private PROJECT_VISIBILITY strategyVisibility = PROJECT_VISIBILITY.everybody;

	private String offer;

	private PROJECT_VISIBILITY offerVisibility = PROJECT_VISIBILITY.everybody;

	private String dueDateStr;

	@Required
	private String language;

	// ////////////////////////////
	// business logic

	public EditedProjectData(ProjectData pdata, String userLanguage) {
		if (pdata != null) {
			name = pdata.getName();
			goal = pdata.getGoal();
			description = pdata.getDescription();
			descriptionVisibility = pdata.getDescriptionVisibility();
			reason = pdata.getReason();
			strategy = pdata.getStrategy();
			strategyVisibility = pdata.getStrategyVisibility();
			offer = pdata.getOffer();
			offerVisibility = pdata.getOfferVisibility();
			dueDateStr = Utils.formatDate(pdata.getDueDate());
			language = Utils.resolveLanguageOfCode(pdata.getLanguage(), userLanguage); 
					
			ObjectMapper mapper = new ObjectMapper();
			try {
				allLocationJson = mapper.writeValueAsString(pdata.getLocations());
			} catch (Exception e) {
				logger.log(Level.WARNING, "could not marsal to json => falling back to empty json string provided to the gui" , e); 
			} 
			
		}
		
	}

	public EditedProjectData() {
		super();
	}

	public Set<Location> getParsedJsonLocations() {

		if (cachedParsedLocations == null) {
			synchronized (this) {
				if (cachedParsedLocations == null) {
					cachedParsedLocations = Utils.jsonToSetOfStuf(allLocationJson, Location[].class);
				}
			}
		}
		return cachedParsedLocations;
	}

	/**
	 * @param pdata
	 */
	public void applyToProjectData(ProjectData pdata, String userLanguage) {
		if (pdata != null) {
			
// these are read-only data...
//			pdata.setName(name);
//			pdata.setGoal(goal);
			pdata.setDescription(description);
			pdata.setDescriptionVisibility(descriptionVisibility);
			pdata.setReason(reason);
			pdata.setStrategy(strategy);
			pdata.setStrategyVisibility(strategyVisibility);
			pdata.setOffer(offer);
			pdata.setOfferVisibility(offerVisibility);
			pdata.setDueDate(Utils.convertStringToDate(dueDateStr));
			pdata.setLanguage(Utils.resolveCodeOfLanguage(language, userLanguage));
			pdata.setLocations(getParsedJsonLocations());
		}
	}

	// ///////////////////////////////
	// getters, setters

	public String getAllLocationJson() {
		return allLocationJson;
	}

	public void setAllLocationJson(String allLocationJson) {
		this.allLocationJson = allLocationJson;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public PROJECT_VISIBILITY getDescriptionVisibility() {
		return descriptionVisibility;
	}

	public void setDescriptionVisibility(PROJECT_VISIBILITY descriptionVisibility) {
		this.descriptionVisibility = descriptionVisibility;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getStrategy() {
		return strategy;
	}

	public void setStrategy(String strategy) {
		this.strategy = strategy;
	}

	public PROJECT_VISIBILITY getStrategyVisibility() {
		return strategyVisibility;
	}

	public void setStrategyVisibility(PROJECT_VISIBILITY strategyVisibility) {
		this.strategyVisibility = strategyVisibility;
	}

	public String getOffer() {
		return offer;
	}

	public void setOffer(String offer) {
		this.offer = offer;
	}

	public PROJECT_VISIBILITY getOfferVisibility() {
		return offerVisibility;
	}

	public void setOfferVisibility(PROJECT_VISIBILITY offerVisibility) {
		this.offerVisibility = offerVisibility;
	}

	public String getDueDateStr() {
		return dueDateStr;
	}

	public void setDueDateStr(String dueDateStr) {
		this.dueDateStr = dueDateStr;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGoal() {
		return goal;
	}

	public void setGoal(String goal) {
		this.goal = goal;
	}

}