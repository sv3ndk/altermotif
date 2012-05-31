package com.svend.dab.core.beans.projects;

import java.util.Date;
import java.util.List;

/**
 * Query for searching projects. 
 * 
 * All terms are AND-ed at the DAO level
 * 
 * @author svend
 *
 */
public class SearchQuery {
	
	public enum SORT_KEY {
		relevancy,
		duedate,
		proximity
	}
	
	
	// a project must have all of those keywords in order to be selected 
	private String searchTerm;
	
	// a project must have all of those tags in order to be selected 
	private List<String> tags;

	// a project must have all of those themes in order to be selected 
	private List<SelectedTheme> themes;

	// a project must have one of its location within this geographic region in order to be selected 
	private GeographicCircle inGeographicRegion;
	
	// a project must have a dueDate after this in order to be selected 
	private Date dueDateBefore;

	private String language;
	
	private SORT_KEY sortKey = SORT_KEY.relevancy;
	
	

	public SearchQuery(String searchTerm, List<String> tags, List<SelectedTheme> themes) {
		super();
		this.searchTerm = searchTerm;
		this.tags = tags;
		this.themes = themes;
	}

	public SearchQuery() {
		super();
	}
	
	

	public String getSearchTerm() {
		return searchTerm;
	}

	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public List<SelectedTheme> getThemes() {
		return themes;
	}

	public void setThemes(List<SelectedTheme> themes) {
		this.themes = themes;
	}

	public SORT_KEY getSortKey() {
		return sortKey;
	}

	public void setSortKey(SORT_KEY sortKey) {
		this.sortKey = sortKey;
	}

	public Date getDueDateBefore() {
		return dueDateBefore;
	}

	public void setDueDateBefore(Date dueDateBefore) {
		this.dueDateBefore = dueDateBefore;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public GeographicCircle getInGeographicRegion() {
		return inGeographicRegion;
	}

	public void setInGeographicRegion(GeographicCircle inGeographicRegion) {
		this.inGeographicRegion = inGeographicRegion;
	}

}
