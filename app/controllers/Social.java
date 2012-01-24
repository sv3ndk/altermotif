package controllers;

import models.altermotif.MappedValue;
import play.mvc.Controller;

/**
 * @author svend
 *
 */
public class Social extends Controller{

	
	
	
	public static void sendMail(String recipient, String replyTo, String subject, String textContent) {
		
		BeanProvider.getSocialService().sendEmail(recipient, replyTo, subject, textContent);
		renderJSON(new MappedValue("bla","la"));
	}
	
	
}
