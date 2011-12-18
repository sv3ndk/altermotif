package models.altermotif.projects;

/**
 * @author svend
 *
 */
public class SearchRequest {

	private String term;
	
	private String tag[];
	
	// JSON format String containing a list of SelectedThemes
	private String themes;

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
