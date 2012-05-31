package com.svend.dab.core.beans.projects;

import org.springframework.data.annotation.Transient;

import models.altermotif.projects.theme.Theme;

/**
 * 
 * This represent one particular theme subtheme selected for a project, as opposed to {@link Theme} which defines the complete set of subthemes and labels for a given theme (and is not related to any
 * particular project)
 * 
 * @author svend
 * 
 */
public class SelectedTheme {

	private String themeId;
	private String themeLabel;

	private String subThemeId;
	private String subThemeLabel;

	// useless and ugly, but used on js side => streamed back to java in some cases
	@Transient
	private String jsonId;
	
	public SelectedTheme() {
		super();
	}


	public SelectedTheme(String themeId, String themeLabel, String subThemeId, String subThemeLabel) {
		super();
		this.themeId = themeId;
		this.themeLabel = themeLabel;
		this.subThemeId = subThemeId;
		this.subThemeLabel = subThemeLabel;
	}
	
	
	public String buildStringRepresentation() {
		return getThemeId() + "_" + getSubThemeId();
	}


	public String getThemeId() {
		return themeId;
	}

	public void setThemeId(String themeId) {
		this.themeId = themeId;
	}

	public String getSubThemeId() {
		return subThemeId;
	}

	public void setSubThemeId(String subThemeId) {
		this.subThemeId = subThemeId;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || ! (obj instanceof SelectedTheme)) {
			return false;
		}
		
		SelectedTheme otherTheme = (SelectedTheme) obj;
		
		if (themeId == null) {
			if (subThemeId == null) {
				return otherTheme.getThemeId() == null && otherTheme.getSubThemeId() == null;
			} else {
				return subThemeId.equals(otherTheme.getSubThemeId());
			}
		}
		
		if (!themeId.equals(otherTheme.getThemeId())) {
			return false;
		}
		
		if (subThemeId == null) {
			return otherTheme.getSubThemeId() == null;
		} else {
			return subThemeId.equals(otherTheme.getSubThemeId());
		}
	}
	
	@Override
	public int hashCode() {
		
		int hash = super.hashCode();
		
		if (themeId != null) {
			hash += themeId.hashCode();
		}

		if (subThemeId != null) {
			hash += subThemeId.hashCode();
		}
		
		return hash;
	}


	public String getThemeLabel() {
		return themeLabel;
	}


	public void setThemeLabel(String themeLabel) {
		this.themeLabel = themeLabel;
	}


	public String getSubThemeLabel() {
		return subThemeLabel;
	}


	public void setSubThemeLabel(String subThemeLabel) {
		this.subThemeLabel = subThemeLabel;
	}


	public String getJsonId() {
		return jsonId;
	}


	public void setJsonId(String jsonId) {
		this.jsonId = jsonId;
	}

}
