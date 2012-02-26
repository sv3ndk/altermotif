package models.altermotif;

import play.i18n.Lang;
import play.mvc.Scope.Session;

import com.google.common.base.Strings;

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
	
	public final static String LANGUAGE_CODE_ENGLISH = "en";
	public final static String LANGUAGE_CODE_FRENCH = "fr";
	public final static String LANGUAGE_CODE_DUTCH = "nl";
	public final static String LANGUAGE_CODE_TURKISH = "tr";
	
	public final static String POSSIBLE_LANGUAGES[] = {LANGUAGE_CODE_ENGLISH, LANGUAGE_CODE_FRENCH, LANGUAGE_CODE_DUTCH, LANGUAGE_CODE_TURKISH};
	

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
	 * @return the the currently selected language code, 
	 * 
	 * final because called from constructor
	 */
	public final String getSelectedLg() {
		
		String selectedCode = session.get(LANGUAGE_SESSION_PARAM);
		for (String code : POSSIBLE_LANGUAGES) {
			if (code .equals(selectedCode)) {
				return code;
			}
		}
		
		return POSSIBLE_LANGUAGES[0];
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
