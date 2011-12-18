package models.altermotif.projects.theme;

/**
 * @author svend
 *
 */
public class SubTheme {
	
	private final String id;
	private final String label;
	
	public SubTheme(String id, String label) {
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

}
