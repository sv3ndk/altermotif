package models.altermotif.projects.theme;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.solr.common.util.Hash;


/**
 * @author svend
 *
 */
public class Theme {

	private final String id;
	private final String label;
	private List<SubTheme> subThemes = new LinkedList<SubTheme>();

	public Theme(String id, String label) {
		super();
		this.id = id;
		this.label = label;
	}
	
	
	public Set<String> buildSetOfStringRepresentation() {
		Set<String> response = new HashSet<String>();
		for (SubTheme subTheme : subThemes){
			response.add(id + "_" + subTheme.getId());
		}
		return response;
	}


	public String getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	/**
	 * @param st
	 */
	public void addSubTheme(SubTheme st) {
		subThemes.add(st);
	}

	public List<SubTheme> getSubThemes() {
		return subThemes;
	}

}
