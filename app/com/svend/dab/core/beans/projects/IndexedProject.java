package com.svend.dab.core.beans.projects;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;

import com.google.common.base.Strings;
import com.svend.dab.core.beans.GeoCoord;
import com.svend.dab.core.beans.Location;

public class IndexedProject {

	@Id
	private String projectId;

	private Set<String> terms = new HashSet<String>();

	private Set<String> tags = new HashSet<String>();

	private Set<String> themesWithSubTheme = new HashSet<String>();

	private GeoCoord location;

	private String language;

	private Date dueDate;

	// ----------------------------------
	//

	public IndexedProject(Project project) {

		this.projectId = project.getId();
		this.tags = project.getTags();
		this.language = project.getPdata().getLanguage();
		this.dueDate = project.getPdata().getDueDate();

		// TODO: we need MongoDB >= 1.9 in order to index 2D based on several location, but cloundfoundry only provide MongoDb 1.8 for now
		// => we only take the first location for the moment (to be improved when we migrate to Lucene)
		if (project.getPdata().getLocations() != null && !project.getPdata().getLocations().isEmpty()) {
			Location loc = (Location) project.getPdata().getLocations().toArray()[0];
			location = new GeoCoord(Double.parseDouble(loc.getLatitude()), Double.parseDouble(loc.getLongitude()));
		}
	}

	public IndexedProject() {
		super();
	}

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

	// ----------------------------------
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

	public GeoCoord getLocation() {
		return location;
	}

	public void setLocation(GeoCoord location) {
		this.location = location;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

}
