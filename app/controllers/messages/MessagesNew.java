/**
 * 
 */
package controllers.messages;

import java.util.logging.Logger;

import com.svend.dab.core.beans.profile.UserProfile;

import models.altermotif.messages.NewMessage;
import play.data.validation.Validation;
import web.utils.Utils;
import controllers.Application;
import controllers.BeanProvider;
import controllers.DabLoggedController;

/**
 * @author Svend
 *
 */
public class MessagesNew extends DabLoggedController {


	private static Logger logger = Logger.getLogger(MessagesNew.class.getName());

	
	// some utility methods (any static method of this controller is exposed in the REST API, I do not want to expose those methods)
	private static MessageNewHelper messageNewHelper = new MessageNewHelper();
	
	
	// this is the flash parameter used when we want to post-redirect-get to this page manually, to indicate this recipient
	public static String FLASH_MESSAGE_TO ="messageTo";
	
	// same thing: to indicate we are replying to this message id
	public static String FLASH_REPLY_TO_MESSAGE_ID ="replyToMsgId";

	// same thing: to indicate we are replying to this message id
	public static String FLASH_FORWARD_MESSAGE_ID ="forwardMsgId";
	
	
	// this is the flash parameter used by the form, after a submission, to keep the FORM data between sucessive erroneous submisions 
	public static String FLASH_FORM_MESSAGE_TO ="writtenMessage.toUserName";
	public static String FLASH_FORM_SUBJECT ="writtenMessage.subject";
	public static String FLASH_FORM_CONTENT ="writtenMessage.messageContent";

	

	
	// this is used when we want to jump to this page directly with the "to" field sets => triggered direcly from a Web request
	public static void messagesNewTo(String toUser) {
		flash.put(FLASH_MESSAGE_TO, toUser);
		messagesNew();
	}
	
	
	
	public static void messagesNew() {
	
		if (flash.contains(FLASH_MESSAGE_TO)) {
			// present if we are redirected here after messagesNewTo
			flash.put(FLASH_FORM_MESSAGE_TO, flash.get(FLASH_MESSAGE_TO));
		} else if (flash.contains(FLASH_REPLY_TO_MESSAGE_ID)) {
			// present if we are redirected here after a replyTo (
			messageNewHelper.caterScopeForReply(getSessionWrapper().getLoggedInUserProfileId(), renderArgs, flash, flash.get(FLASH_REPLY_TO_MESSAGE_ID));
		} else if (flash.contains(FLASH_FORWARD_MESSAGE_ID)) {
			// present if we are redirected here after a replyTo (
			messageNewHelper.caterScopeForForward(getSessionWrapper().getLoggedInUserProfileId(), renderArgs, flash, flash.get(FLASH_FORWARD_MESSAGE_ID));
		}
	
		UserProfile  loggedInUserProfile = BeanProvider.getUserProfileService().loadUserProfile(getSessionWrapper().getLoggedInUserProfileId(), true);
		renderArgs.put("loggedinUserProfile", loggedInUserProfile);
		
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
