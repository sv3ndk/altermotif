package models.altermotif.profile;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import models.altermotif.AbstractRenderableModel;

/**
 * @author Svend
 *
 */
public class CaptchaString extends AbstractRenderableModel{

	
	private static Character[] CAPTCHA_AVAILABLE_SYMBOLS = new Character[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
		'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
	
	
	// name of this javabean, as visible from the HTML views
	public static String RENDER_PARAM_NAME = "captcha";
	
	
	private String captchValue;
	
	public String getValue() {
		return captchValue;
	}
	
	/**
	 * @return a random set of letters, that the user must put in order in order
	 *         to pass the captcha (they must all be unique because of
	 *         restrictions in ButtonCaptcha)
	 */
	public  String generateRandomCaptchaLetter() {

		List<Character> generatedString = new LinkedList<Character>();

		for (int i = 0; i < 5; i++) {
			Character generated = null;
			
			while(generated == null || isAlreadyPresent(generated, generatedString)) {
				generated = CAPTCHA_AVAILABLE_SYMBOLS[(int) Math.floor(Math.random() * CAPTCHA_AVAILABLE_SYMBOLS.length)];
			}
			
			generatedString.add(generated);
			Collections.sort(generatedString);
		}
		
		StringBuffer strBuf = new StringBuffer();
		
		for (Character ch : generatedString) {
			strBuf.append(ch);
		}
		
		captchValue =  strBuf.toString();
		return captchValue;
	}
	

	/**
	 * @param generated
	 * @param response
	 * @return
	 */
	private boolean isAlreadyPresent(Character generated, List<Character>  response) {
		if (response == null || response.size() == 0) {
			return false;
		}
		
		for (int i = 0; i< response.size(); i++) {
			if (generated.equals(response.get(i))) {
				return true;
			}
			
		}
		return false;
	}


	@Override
	protected String getRenderParamName() {
		return RENDER_PARAM_NAME;
	}
	
	
}
