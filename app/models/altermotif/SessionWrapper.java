package models.altermotif;

import com.google.common.base.Strings;

import play.i18n.Lang;
import play.mvc.Scope;
import play.mvc.Scope.RenderArgs;
import play.mvc.Scope.Session;

/**
 * Nice wrapper that gives easy to interpret method about the session state
 * 
 * @author Svend
 * 
 */
public class SessionWrapper extends AbstractRenderableModel {

	private final Session session;

	// name of this javabean, as visible from the HTML views
	public static String RENDER_PARAM_NAME = "userSession";
	
	// session parameters
	public static String LANGUAGE_SESSION_PARAM = "dablg";
	public static String USERNAME_SESSION_PARAM = "userid";
	
	public final static String POSSIBLE_LANGUAGES[] = {"en", "fr", "nl", "tr"};
	

	public SessionWrapper(Session session) {
		this.session = session;
		Lang.set(getSelectedLg());
	}

	@Override
	protected String getRenderParamName() {
		return RENDER_PARAM_NAME;
	}

	// -----------------------------------------------------------

	public boolean isLoggedIn() {
		return session.contains(USERNAME_SESSION_PARAM);
	}

	/**
	 * @return the index of the currently selected language, (order must be the same as in the template drop down)
	 * 
	 * final because called from constructor
	 */
	public final String getSelectedLg() {
		int selectedLgIndex = getSelectedLgIndex();
		if (selectedLgIndex > 0 && selectedLgIndex -1< POSSIBLE_LANGUAGES.length) {
			return POSSIBLE_LANGUAGES[selectedLgIndex-1];
		} 
		
		// defaulting to English
		return POSSIBLE_LANGUAGES[0];
		
	}
	
	/**
	 * final because called from constructor
	 * 
	 * @return
	 */
	public final int getSelectedLgIndex() {
		try {
			return Integer.parseInt(session.get(LANGUAGE_SESSION_PARAM));
		} catch (Exception exc) {
			// defaulting to english in case of any erryr (index count start at 1)
			return 1;
		}
	}

	public void updateSelectedLanguage(String selection) {
		if (!Strings.isNullOrEmpty(selection)) {
			String adjusted = selection.trim();
			session.put(LANGUAGE_SESSION_PARAM, adjusted);

		}
	}

	public void setLoggedInUserProfileId(String username) {
		session.put(USERNAME_SESSION_PARAM, username);
	}
	
	public String getLoggedInUserProfileId() {
		return session.get(USERNAME_SESSION_PARAM);
	}

}
