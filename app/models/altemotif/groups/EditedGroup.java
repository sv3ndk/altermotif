package models.altemotif.groups;

import java.util.List;
import java.util.Set;

import com.svend.dab.core.beans.Location;
import com.svend.dab.core.beans.groups.ProjectGroup;
import com.svend.dab.core.beans.projects.SelectedTheme;

import play.data.validation.Required;
import web.utils.Utils;

/**
 * @author svend
 * 
 */
public class EditedGroup {
	
	private String id;
	
	@Required
	private String name;

	@Required
	private String description;

	@Required(message = "groupsEditAtLeastOneMessageErrorMessage")
	private String locationJson;

	private String themesJson;

	private String tagsJson;

	private Set<Location> cachedParsedLocation;
	private Set<SelectedTheme> cachedParsedThemes;
	private Set<String> cachedParsedTags;

	// /////////////////////////
	
	public EditedGroup(ProjectGroup group) {
		if (group != null) {
			this.id = id;
			this.name = group.getName();
			this.description = group.getDescription();
			this.locationJson = Utils.objectToJsonString(group.getLocation());
			this.themesJson = Utils.objectToJsonString(group.getThemes());
			this.tagsJson = Utils.objectToJsonString(group.getTags());
		}
	}

	

	public EditedGroup() {
		super();
	}



	public void applyToGroup(ProjectGroup group) {
		
		if (group != null) {
			group.setName(name);
			group.setDescription(description);

			group.replaceLocations(getParsedLocations());
			group.replaceThemes(getParsedThemes());
			group.replaceTags(getParsedTags());
			
		}
	}
	

	public Set<Location> getParsedLocations() {
		if (cachedParsedLocation == null) {
			cachedParsedLocation = Utils.jsonToSetOfStuf(locationJson, Location[].class);
		}
		return cachedParsedLocation;
	}

	public Set<SelectedTheme> getParsedThemes() {
		if (cachedParsedThemes == null) {
			cachedParsedThemes = Utils.jsonToSetOfStuf(themesJson, SelectedTheme[].class);
		}
		return cachedParsedThemes;
	}

	public Set<String> getParsedTags() {
		if (cachedParsedTags == null) {
			cachedParsedTags = Utils.jsonToSetOfStuf(tagsJson, String[].class);
		}
		return cachedParsedTags;
	}

	// ////////////

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLocationJson() {
		return locationJson;
	}

	public void setLocationJson(String locationJson) {
		this.locationJson = locationJson;
	}

	public String getThemesJson() {
		return themesJson;
	}

	public void setThemesJson(String themesJson) {
		this.themesJson = themesJson;
	}

	public String getTagsJson() {
		return tagsJson;
	}

	public void setTagsJson(String tagsJson) {
		this.tagsJson = tagsJson;
	}



	public String getId() {
		return id;
	}



	public void setId(String id) {
		this.id = id;
	}

}
