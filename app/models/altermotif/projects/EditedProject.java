package models.altermotif.projects;

import java.util.List;
import java.util.Set;

import web.utils.Utils;

import com.svend.dab.core.beans.profile.UserSummary;
import com.svend.dab.core.beans.projects.Project;
import com.svend.dab.core.beans.projects.ProjectPep;
import com.svend.dab.core.beans.projects.SelectedTheme;
import com.svend.dab.core.beans.projects.Task;


public class EditedProject {
	
	private String id;
	private String allLinksJson;
	private String allTagsJson;
	private String allThemesJson;

	// this is always empty when sent from server to browser (actual list of tasks are retrieved thanks to async ajax call)
	// when this is sent back from browser to server, this only contains the new or updated tasks (in order to avoid clashes if several admins update simultaneously)
	private String updatedTasksJson;
	
	private String removedTasksIdJson;
	
	private EditedProjectData pdata;
	
	//
	private Set<String> cachedParsedLinks;
	private Set<String> cachedParsedTags;
	private Set<SelectedTheme> cachedParsedThemes;
	private Set<Task> cachedParsedTasks;
	private Set<String> cachedParsedRemovedTasksIds;
	
	
	private List<UserSummary> confirmedActiveParticipants;
	
	public EditedProject() {
		super();
	}

	public EditedProject(Project project, String userLanguage) {
		pdata = new EditedProjectData(project.getPdata(), userLanguage);
		confirmedActiveParticipants = project.getConfirmedActiveParticipantsSummaries();
		allTagsJson = Utils.objectToJsonString(project.getTags());
		allThemesJson = Utils.objectToJsonString(project.getThemes());
		setAllLinks(project.getLinks());
		id = project.getId();
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
	
	public Set<Task> getParsedTasks() {
		if (cachedParsedTasks == null) {
			synchronized (this) {
				if (cachedParsedTasks == null) {
					cachedParsedTasks = Utils.jsonToSetOfStuf(updatedTasksJson, Task[].class);
				}
			}
		}
		return cachedParsedTasks;
	}
	
	public Set<String> getParsedRemovedTasksIds() {
		if (cachedParsedRemovedTasksIds == null) {
			synchronized (this) {
				if (cachedParsedRemovedTasksIds == null) {
					cachedParsedRemovedTasksIds = Utils.jsonToSetOfStuf(removedTasksIdJson, String[].class);
				}
			}
		}
		return cachedParsedRemovedTasksIds;
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

	public String getId() {
		return id;
	}

	public List<UserSummary> getConfirmedActiveParticipants() {
		return confirmedActiveParticipants;
	}

	public String getUpdatedTasksJson() {
		return updatedTasksJson;
	}

	public void setUpdatedTasksJson(String updatedTasksJson) {
		this.updatedTasksJson = updatedTasksJson;
	}

	public String getRemovedTasksIdJson() {
		return removedTasksIdJson;
	}

	public void setRemovedTasksIdJson(String removedTasksIdJson) {
		this.removedTasksIdJson = removedTasksIdJson;
	}

	

}
