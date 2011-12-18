package com.svend.dab.core.beans.projects;

import org.apache.commons.lang.StringUtils;

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
	private String subThemeId;

	public SelectedTheme() {
		super();
	}

	public SelectedTheme(String themeId, String subThemeId) {
		super();
		this.themeId = themeId;
		this.subThemeId = subThemeId;
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

}
