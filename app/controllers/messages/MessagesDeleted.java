package controllers.messages;

import java.util.Set;

import models.altermotif.MappedValue;
import models.altermotif.messages.MessagesPage;
import web.utils.DateMessageJsonSerializer;
import web.utils.Utils;

import com.google.common.base.Strings;

import controllers.BeanProvider;
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
			MessagesNew.replyTo(messageId);
		}
	}

	public static void doForward(String messageId) {
		if (Strings.isNullOrEmpty(messageId)) {
			messagesDeleted();
		} else {
			MessagesNew.forward(messageId);
		}
	}
	
	public static void doUndeleteMessage(String messageIds) {
		Set<String> ids = Utils.jsonToSetOfStrings(messageIds);
		BeanProvider.getMessagesService().undeleteMessages(ids, getSessionWrapper().getLoggedInUserProfileId());
		// TODO: error handling here
		renderJSON(new MappedValue("result", "ok"));
	}
	
}
