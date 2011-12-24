package models.altermotif.projects;

import java.util.LinkedList;
import java.util.Set;

import web.utils.Utils;

import com.google.common.base.Strings;
import com.svend.dab.core.beans.projects.ProjectSearchRequest;
import com.svend.dab.core.beans.projects.SelectedTheme;

/**
 * @author svend
 *
 */
public class WebSearchRequest {

	private String term;
	
	private String tag[];
	
	// JSON format String containing a list of SelectedThemes
	private String themes;

	
	public ProjectSearchRequest toBackendRequest() {
		
		ProjectSearchRequest request = new ProjectSearchRequest();
		
		request.setSearchTerm(term);
		
		request.setTags(new LinkedList<String>());
		if (tag != null) {
			for (String oneTag : tag) {
				request.getTags().add(oneTag);
			}
		}

		request.setThemes(new LinkedList<SelectedTheme>());
		if (themes != null) {
			Set<SelectedTheme> themeSet = Utils.jsonToSetOfStuf(themes, SelectedTheme[].class);
			request.getThemes().addAll(themeSet);
		}
		
		return request;
	}

	
	public boolean isEmpty() {
		return Strings.isNullOrEmpty(term) && Strings.isNullOrEmpty(themes) && (tag == null || tag.length == 0);  
	}

	//---------------------------------------
	
	
	
	public String getTerm() {
		return term;
	}

	public void setTerm(String searchString) {
		this.term = searchString;
	}

	public String[] getTag() {
		return tag;
	}

	public void setTag(String[] tags) {
		this.tag = tags;
	}

	public String getThemes() {
		return themes;
	}

	public void setThemes(String themes) {
		this.themes = themes;
	}




	
	
}
