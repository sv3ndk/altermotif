package models.altermotif.projects;

import java.util.Set;

import web.utils.Utils;

import com.svend.dab.core.beans.projects.Project;


public class EditedProject {
	
	private String allLinksJson;
	
	private String allTagsJson;


	//
	private Set<String> cachedParsedLinks;
	private Set<String> cachedParsedTags;
	
	
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
	
	
	
	public void applyToProject(Project project) {
		if (project != null) {
			project.setLinks(getparsedLinks());
			project.setTags(getparsedTags());
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
	

}
