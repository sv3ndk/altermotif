package controllers.messages;

import static controllers.messages.MessagesNew.FLASH_FORWARD_MESSAGE_ID;

import java.util.Set;

import com.google.common.base.Strings;

import models.altermotif.MappedValue;
import models.altermotif.messages.MessagesPage;
import controllers.BeanProvider;
import controllers.DabController;
import controllers.DabLoggedController;
import play.mvc.*;
import web.utils.DateMessageJsonSerializer;
import web.utils.Utils;

public class MessagesOutbox extends DabLoggedController {

    public static void messagesOutbox() {
        render();
    }
    
    
    public static void loadOutboxMessages(int pageNumber) {
		MessagesPage page = new MessagesPage();
		page.setMessages(BeanProvider.getMessagesService().getWrittenMessages(getSessionWrapper().getLoggedInUserProfileId(), pageNumber));
		page.setPreviousPageExists(pageNumber > 0);
		page.setNextPageExists(BeanProvider.getMessagesService().isThereMoreOutboxPagesThen(getSessionWrapper().getLoggedInUserProfileId(), pageNumber));
		renderJSON(page, new DateMessageJsonSerializer());

    }
    
    
    public static void doForward(String messageId) {
		if (Strings.isNullOrEmpty(messageId)) {
			messagesOutbox();
		} else {
			// redirects to message NEW
			flash.put(FLASH_FORWARD_MESSAGE_ID, messageId);
			MessagesNew.messagesNew();
		}
    	
    }
    

	public static void doDeleteMessage(String messageIds) {
		Set<String> ids = Utils.jsonToSetOfStrings(messageIds);
		BeanProvider.getMessagesService().markMessagesAsDeletedByEmitter(ids, getSessionWrapper().getLoggedInUserProfileId());
		renderJSON(new MappedValue("result", "ok"));
	}


}
