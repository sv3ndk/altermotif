package models.altermotif.projects;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.cloudfoundry.org.codehaus.jackson.map.ObjectMapper;

import web.utils.Utils;

import com.svend.dab.core.beans.projects.Project;


public class EditedProject {
	
	private static Logger logger = Logger.getLogger(EditedProject.class.getName());
	
	ObjectMapper mapper = new ObjectMapper();

	
	private String allLinksJson;
	
	private String allTagsJson;

	private String allThemesJson;

	private EditedProjectData pdata;
	
	//
	private Set<String> cachedParsedLinks;
	private Set<String> cachedParsedTags;
	
	
	
	
	public EditedProject() {
		super();
	}

	public EditedProject(Project project, String userLanguage) {
		pdata = new EditedProjectData(project.getPdata(), userLanguage);
		try {
			allTagsJson = mapper.writeValueAsString(project.getTags());
			setAllLinks(project.getLinks());
		} catch (Exception e) {
			logger.log(Level.WARNING, "could not marsal to json => falling back to empty json string provided to the gui" , e); 
		} 
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
	
	
	
	public void applyToProject(Project project, String userLanguage) {
		if (project != null) {
			project.setLinks(getparsedLinks());
			project.setTags(getparsedTags());
			
			if (pdata != null) {
				pdata.applyToProjectData(project.getPdata(), userLanguage);
			}
		}
	}

	// final because called from constructor
	public final  void setAllLinks(Set<String> updatedSites) {
		try {
			synchronized (this) {
				allLinksJson = mapper.writeValueAsString(updatedSites);
				cachedParsedLinks = updatedSites;
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, "could not marsal to json => falling back to empty json string provided to the gui" , e); 
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
