package models.altermotif.projects;

import java.util.Set;
import java.util.logging.Logger;

import web.utils.Utils;

import com.svend.dab.core.beans.projects.Project;
import com.svend.dab.core.beans.projects.ProjectPep;
import com.svend.dab.core.beans.projects.SelectedTheme;


public class EditedProject {
	
	private String allLinksJson;
	
	private String allTagsJson;

	private String allThemesJson;

	private EditedProjectData pdata;
	
	//
	private Set<String> cachedParsedLinks;
	private Set<String> cachedParsedTags;
	private Set<SelectedTheme> cachedParsedThemes;
	
	
	public EditedProject() {
		super();
	}

	public EditedProject(Project project, String userLanguage) {
		pdata = new EditedProjectData(project.getPdata(), userLanguage);
		allTagsJson = Utils.objectToJsonString(project.getTags());
		allThemesJson = Utils.objectToJsonString(project.getThemes());
		setAllLinks(project.getLinks());
	}

	public Set<String> getparsedLinks() {
		if (cachedParsedLinks == null) {
			synchronized (this) {
				if (cachedParsedLinks == null) {
					cachedParsedLinks = Utils.jsonToSetOfStuf(allLinksJson, String[].class);
				}
			}
		}
		return cachedParsedLinks;
	}
	
	public Set<String> getparsedTags() {
		if (cachedParsedTags == null) {
			synchronized (this) {
				if (cachedParsedTags == null) {
					cachedParsedTags = Utils.jsonToSetOfStuf(allTagsJson, String[].class);
				}
			}
		}
		return cachedParsedTags;
	}
	
	public Set<SelectedTheme> getparsedThemes() {
		if (cachedParsedThemes == null) {
			synchronized (this) {
				if (cachedParsedThemes == null) {
					cachedParsedThemes = Utils.jsonToSetOfStuf(allThemesJson, SelectedTheme[].class);
				}
			}
		}
		return cachedParsedThemes;
	}
	
	
	public void applyToProject(Project project, String userLanguage, ProjectPep pep, String username) {
		if (project != null) {
			
			project.setLinks(getparsedLinks());
			
			if (pep.isAllowedToEditProjectTags(username)) {
				project.setTags(getparsedTags());
			}
			if (pep.isAllowedToEditProjectThemes(username)) {
				project.setThemes(getparsedThemes());
			}
			
			if (pdata != null) {
				pdata.applyToProjectData(project.getPdata(), userLanguage, pep, username);
			}
		}
	}

	// final because called from constructor
	public final  void setAllLinks(Set<String> updatedSites) {
		synchronized (this) {
			allLinksJson = Utils.objectToJsonString(updatedSites);
			cachedParsedLinks = updatedSites;
		}
	}
	

	public String getAllLinksJson() {
		return allLinksJson;
	}
	
	public void setAllLinksJson(String allLinksJson) {
		this.allLinksJson = allLinksJson;
	}
	
	public String getAllTagsJson() {
		return allTagsJson;
	}
	
	public void setAllTagsJson(String allTagsJson) {
		this.allTagsJson = allTagsJson;
	}

	public EditedProjectData getPdata() {
		return pdata;
	}

	public void setPdata(EditedProjectData pdata) {
		this.pdata = pdata;
	}

	public String getAllThemesJson() {
		return allThemesJson;
	}

	public void setAllThemesJson(String allThemesJson) {
		this.allThemesJson = allThemesJson;
	}

	

}
