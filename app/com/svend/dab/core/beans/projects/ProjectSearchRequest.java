package com.svend.dab.core.beans.projects;

import java.util.List;

public class ProjectSearchRequest {

	private String searchTerm;

	private List<String> tags;

	private List<SelectedTheme> themes;

	public ProjectSearchRequest(String searchTerm, List<String> tags, List<SelectedTheme> themes) {
		super();
		this.searchTerm = searchTerm;
		this.tags = tags;
		this.themes = themes;
	}

	public ProjectSearchRequest() {
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

}
