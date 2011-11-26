package controllers.messages;

import static controllers.messages.MessagesNew.FLASH_FORWARD_MESSAGE_ID;
import static controllers.messages.MessagesNew.FLASH_REPLY_TO_MESSAGE_ID;

import com.google.common.base.Strings;

import models.altermotif.messages.MessagesPage;
import web.utils.DateMessageJsonSerializer;
import controllers.BeanProvider;
import controllers.DabController;
import controllers.DabLoggedController;

public class MessagesDeleted extends DabLoggedController {

    public static void messagesDeleted() {
        render();
    }
    
    
	public static void loadMessages(int pageNumber) {
		MessagesPage page = new MessagesPage();
		page.setMessages(BeanProvider.getMessagesService().getDeletedMessages(getSessionWrapper().getLoggedInUserProfileId(), pageNumber));
		page.setPreviousPageExists(pageNumber > 0);
		page.setNextPageExists(BeanProvider.getMessagesService().isThereMoreDeletedPagesThen(getSessionWrapper().getLoggedInUserProfileId(), pageNumber));
		renderJSON(page, new DateMessageJsonSerializer());
	}
	
	
	public static void doReplyTo(String messageId) {
		if (Strings.isNullOrEmpty(messageId)) {
			messagesDeleted();
		} else {
			// redirects to message NEW
			flash.put(FLASH_REPLY_TO_MESSAGE_ID, messageId);
			MessagesNew.messagesNew();
		}
	}

	public static void doForward(String messageId) {
		if (Strings.isNullOrEmpty(messageId)) {
			messagesDeleted();
		} else {
			// redirects to message NEW
			flash.put(FLASH_FORWARD_MESSAGE_ID, messageId);
			MessagesNew.messagesNew();
		}
	}



}
