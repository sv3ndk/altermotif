/**
 * 
 */
package controllers.messages;

import java.util.logging.Logger;

import models.altermotif.messages.NewMessage;
import play.data.validation.Validation;
import controllers.Application;
import controllers.BeanProvider;
import controllers.DabLoggedController;

/**
 * @author Svend
 *
 */
public class MessagesNew extends DabLoggedController {


	private static Logger logger = Logger.getLogger(MessagesNew.class.getName());
	
	
	// this is used when we want to jump to this page directly with the "to" field sets
	public static void messagesNewTo(String toUser) {
		flash.put("messageTo", toUser);
		messagesNew();
	}
	
	
	
	public static void messagesNew() {
		
		if (flash.contains("messageTo")) {
			flash.put("writtenMessage.toUserName", flash.get("messageTo"));
		}
		
		
		render();
	}
	
	
	public static void doSendMessage(NewMessage writtenMessage) {
		
		validation.valid(writtenMessage);
		
		if (Validation.hasErrors()) {
			params.flash();
			Validation.keep();
			messagesNew();
			
		} else {
			BeanProvider.getMessagesService().sendMessage(getSessionWrapper().getLoggedInUserProfileId(), writtenMessage.getToUserName(), writtenMessage.getSubject(), writtenMessage.getMessageContent());
			Application.index();
		}
	}
	
}
