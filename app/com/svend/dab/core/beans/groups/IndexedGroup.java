package com.svend.dab.core.beans.groups;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;

import com.google.common.base.Strings;
import com.svend.dab.core.beans.GeoCoord;
import com.svend.dab.core.beans.Location;
import com.svend.dab.core.beans.projects.SelectedTheme;

/**
 * Reprensentation of the indexed data related to a {@link ProjectGroup}. Full text search is done based on this info
 * 
 * @author svend
 * 
 */
public class IndexedGroup {

	@Id
	private String groupId;

	private Set<String> terms = new HashSet<String>();

	private Set<String> tags = new HashSet<String>();

	private Set<String> themesWithSubTheme = new HashSet<String>();

	private GeoCoord location;

	public IndexedGroup() {
		super();
	}

	public IndexedGroup(ProjectGroup group) {
		this.groupId = group.getId();

		if (group.getTags() != null) {
			for (String tag : group.getTags()) {
				this.tags.add(tag);
			}
		}
		
		if (group.getThemes() != null) {
			for (SelectedTheme theme : group.getThemes()) {
				this.addSelectedTheme(theme);
			}
		}

		// TODO: we need MongoDB >= 1.9 in order to index 2D based on several location, but cloudfoundry only provide MongoDb 1.8 for now
		// => we only take the first location for the moment (to be improved when we migrate to Lucene)
		if (group.getLocation() != null && !group.getLocation().isEmpty()) {
			Location loc = group.getLocation().get(0);
			this.location = new GeoCoord(Double.parseDouble(loc.getLatitude()), Double.parseDouble(loc.getLongitude()));
		}

	}

	// final because called from constructor
	public final void addSelectedTheme(SelectedTheme st) {
		if (st != null) {
			themesWithSubTheme.add(st.getThemeId() + "_" + st.getSubThemeId());
		}
	}
	
	public void addFtsTerm(String term) {
		// skipping empty or too short terms
		if (!Strings.isNullOrEmpty(term) && term.length() > 2) {
			terms.add(term);
		}
	}


	// ////////////////////////////////////
	// /

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
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

}
