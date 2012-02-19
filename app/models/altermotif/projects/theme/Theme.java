package models.altermotif.projects.theme;

import java.util.LinkedList;
import java.util.List;


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
