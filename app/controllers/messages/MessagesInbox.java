package controllers.messages;


import static controllers.messages.MessagesNew.FLASH_FORWARD_MESSAGE_ID;
import static controllers.messages.MessagesNew.FLASH_REPLY_TO_MESSAGE_ID;

import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import models.altermotif.MappedValue;
import models.altermotif.messages.MessagesPage;
import web.utils.DateMessageJsonSerializer;
import web.utils.Utils;

import com.google.common.base.Strings;
import com.svend.dab.core.beans.message.UserMessage;

import controllers.BeanProvider;
import controllers.DabLoggedController;

public class MessagesInbox extends DabLoggedController {

	private static Logger logger = Logger.getLogger(MessagesInbox.class.getName());
	
	public static String FLASH_REDIRECT_INBOX_MESSAGE_ID = "toMessageId";
	
    public static void messagesInbox() {
    	
    	if (flash.contains(FLASH_REDIRECT_INBOX_MESSAGE_ID)) {
    		// the user wants to see this messages => we have to discover which page it belongs to
    		String desiredMessagesId = flash.get(FLASH_REDIRECT_INBOX_MESSAGE_ID);
    		renderArgs.put("pageNumber", BeanProvider.getMessagesService().getInboxPageNumberOfMessage(getSessionWrapper().getLoggedInUserProfileId(), desiredMessagesId));
    		
    	} else {
    		renderArgs.put("pageNumber", 0);
    	}
    	
        render();
    }
    
    
    // I 
    public static void goToMessage(String messageId) {
    	if (!Strings.isNullOrEmpty(messageId)) {
    		flash.put(FLASH_REDIRECT_INBOX_MESSAGE_ID, messageId);
    	}
    	messagesInbox();
    }
    
    
	public static void loadInboxMessages(int pageNumber) {
		MessagesPage page = new MessagesPage();
		page.setMessages(BeanProvider.getMessagesService().getReceivedMessages(getSessionWrapper().getLoggedInUserProfileId(), pageNumber));
		page.setPreviousPageExists(pageNumber > 0);
		page.setNextPageExists(BeanProvider.getMessagesService().isThereMoreInboxPagesThen(getSessionWrapper().getLoggedInUserProfileId(), pageNumber));
		renderJSON(page, new DateMessageJsonSerializer());
	}
	
	
	
	public static void doMarkMessageAsRead(String messageId) {
		if (!Strings.isNullOrEmpty(messageId)) {
			BeanProvider.getMessagesService().markMessageAsRead(messageId);
			renderJSON(new MappedValue("result", "ok"));
		}
	}
	

	public static void doReplyTo(String messageId) {
		if (Strings.isNullOrEmpty(messageId)) {
			messagesInbox();
		} else {
			// redirects to message NEW
			flash.put(FLASH_REPLY_TO_MESSAGE_ID, messageId);
			MessagesNew.messagesNew();
		}
	}

	public static void doForward(String messageId) {
		if (Strings.isNullOrEmpty(messageId)) {
			messagesInbox();
		} else {
			// redirects to message NEW
			flash.put(FLASH_FORWARD_MESSAGE_ID, messageId);
			MessagesNew.messagesNew();
		}
	}

	
	public static void doDeleteMessage(String messageIds) {
		Set<String> ids = Utils.jsonToSetOfStrings(messageIds);
		BeanProvider.getMessagesService().markMessagesAsDeletedByRecipient(ids, getSessionWrapper().getLoggedInUserProfileId());
		renderJSON(new MappedValue("result", "ok"));
	}

}
