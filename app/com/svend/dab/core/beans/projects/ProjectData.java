package com.svend.dab.core.beans.projects;

import java.util.Date;
import java.util.Set;

import com.svend.dab.core.beans.Location;
import com.svend.dab.core.beans.projects.Project.PROJECT_VISIBILITY;

/**
 * @author Svend
 * 
 */
public class ProjectData {

	private String name;

	private String goal;

	private String description;

	private Set<Location> locations;

	private PROJECT_VISIBILITY descriptionVisibility = PROJECT_VISIBILITY.everybody;
	
	private String reason;

	private String strategy;

	private PROJECT_VISIBILITY strategyVisibility = PROJECT_VISIBILITY.everybody;

	private String offer;

	private PROJECT_VISIBILITY offerVisibility = PROJECT_VISIBILITY.everybody;
	
	private Date dueDate ;
	
	private Date creationDate ;

	// ISO code of the language of this project
	private String language;
	
	
	@Override
	public String toString() {
		return "[name=" + name + ", goal=" + goal + ", description=" + description + ", descriptionVisibility=" + descriptionVisibility + ", reason=" + reason +"]";
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


	public PROJECT_VISIBILITY getStrategyVisibility() {
		return strategyVisibility;
	}


	public void setStrategyVisibility(PROJECT_VISIBILITY strategyVisibility) {
		this.strategyVisibility = strategyVisibility;
	}


	public Date getDueDate() {
		return dueDate;
	}


	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}


	public String getLanguage() {
		return language;
	}


	public void setLanguage(String language) {
		this.language = language;
	}


	public Date getCreationDate() {
		return creationDate;
	}


	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}


	public Set<Location> getLocations() {
		return locations;
	}


	public void setLocations(Set<Location> locations) {
		this.locations = locations;
	}

}
