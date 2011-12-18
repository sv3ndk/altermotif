package models.altermotif.projects.theme;

import java.util.HashSet;
import java.util.Set;

/**
 * @author svend
 *
 */
public class Theme {

	private final String id;
	private final String label;
	private Set<SubTheme> subThemes = new HashSet<SubTheme>();

	public Theme(String id, String label) {
		super();
		this.id = id;
		this.label = label;
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

	public Set<SubTheme> getSubThemes() {
		return subThemes;
	}

}
