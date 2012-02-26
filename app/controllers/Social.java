package controllers;

import java.util.logging.Level;
import java.util.logging.Logger;

import play.mvc.Controller;

import com.svend.dab.core.beans.SendMailResponse;

/**
 * @author svend
 *
 */
public class Social extends Controller{

	private static Logger logger = Logger.getLogger(Social.class.getName());
	
	public static void sendMail(String recipient, String replyTo, String subject, String textContent) {
		
		try {
			BeanProvider.getSocialService().sendEmail(recipient, replyTo, subject, textContent);
			renderJSON(new SendMailResponse(true));
		} catch (Exception exc) {
			logger.log(Level.WARNING, "Error while trying to send an email ", exc);
			renderJSON(new SendMailResponse(false));
		}
		
	}
	
}
