package com.svend.dab.core.beans.projects;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;

import com.google.common.base.Strings;

public class IndexedProject {
	
	@Id
	private String projectId;
	
	private Set<String> terms = new HashSet<String>();

	private Set<String> tags = new HashSet<String>();
	
	private Set<String> themesWithSubTheme = new HashSet<String>();

	//----------------------------------
	//
	

	public void addTerm(String term) {
		// skipping empty or too short terms
		if (!Strings.isNullOrEmpty(term) && term.length() > 2) {
			terms.add(term);
		}
	}

	
	public void addSelectedTheme(SelectedTheme st) {
		if (st != null) {
			themesWithSubTheme.add(st.getThemeId() + "_" + st.getSubThemeId());
		}
	}
	
	
	//----------------------------------
	//
	
	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public Set<String> getTerms() {
		return terms;
	}

	public void setTerms(Set<String> terms) {
		this.terms = terms;
	}

	public Set<String> getTags() {
		return tags;
	}

	public void setTags(Set<String> tags) {
		this.tags = tags;
	}

	public Set<String> getThemesWithSubTheme() {
		return themesWithSubTheme;
	}

	public void setThemesWithSubTheme(Set<String> themesWithSubTheme) {
		this.themesWithSubTheme = themesWithSubTheme;
	}
	
	
	

}
