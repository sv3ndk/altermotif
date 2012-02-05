package com.svend.dab.core.beans.groups;


import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.svend.dab.core.beans.Location;
import com.svend.dab.core.beans.projects.SelectedTheme;


public class ProjectGroup {

	private String id;
	private String name;
	private String description;
	private Date creationDate;

	private List<Location> location;
	private List<SelectedTheme> themes;
	private List<String> tags;
	
	private List<GroupParticipant> participants;
	
	//////////////////////

	public ProjectGroup() {
		super();
	}

	public ProjectGroup(String id, String name, String description, List<Location> location, List<SelectedTheme> themes, List<String> tags, Date creationDate) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.location = location;
		this.themes = themes;
		this.tags = tags;
		this.creationDate = creationDate;
	}

	// ////////////////////////////////////////

	
	public void generatePhotoLinks(Date expirationdate) {
		
		if (participants != null) {
			for (GroupParticipant participant : participants) {
				participant.generatePhotoLinks(expirationdate);
			}
		}
	}

	
	public void addParticipant(GroupParticipant groupParticipant) {
		if (participants == null) {
			participants = new LinkedList<GroupParticipant>();
		}
		participants.add(groupParticipant);
	}
	
	public int getNumberOfParticipants() {
		if (participants == null) {
			return 0;
		} else {
			return participants.size();
		}
	}

	public int getNumberOfProjects() {
		return 0;
	}

	
	public void replaceLocations(Set<Location> newLocations) {
		if (location == null) {
			location = new LinkedList<Location>();
		} else {
			location.clear();
		}
		location.addAll(newLocations);
	}
	
	public void replaceThemes(Set<SelectedTheme> newThemes) {
		if (themes == null) {
			themes = new LinkedList<SelectedTheme>();
		} else {
			themes.clear();
		}
		themes.addAll(newThemes);
	}

	public void replaceTags(Set<String> newTags) {
		if (tags == null) {
			tags = new LinkedList<String>();
		} else {
			tags.clear();
		}
		tags.addAll(newTags);
	}

	// /////////////////////////////////

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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

	public List<Location> getLocation() {
		return location;
	}

	public void setLocation(List<Location> location) {
		this.location = location;
	}

	public List<SelectedTheme> getThemes() {
		return themes;
	}

	public void setThemes(List<SelectedTheme> themes) {
		this.themes = themes;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public List<GroupParticipant> getParticipants() {
		return participants;
	}

	public void setParticipants(List<GroupParticipant> participants) {
		this.participants = participants;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}




}
