package controllers.messages;

import static controllers.messages.MessagesNew.FLASH_FORM_CONTENT;
import static controllers.messages.MessagesNew.FLASH_FORM_MESSAGE_TO;
import static controllers.messages.MessagesNew.FLASH_FORM_SUBJECT;
import play.mvc.Scope.Flash;
import play.mvc.Scope.RenderArgs;
import web.utils.Utils;

import com.svend.dab.core.beans.message.UserMessage;

import controllers.BeanProvider;

public class MessageNewHelper {

	
	public String buildMessageRepliedContent(String userId, UserMessage msg) {
		StringBuffer content = new StringBuffer("\n\n-----------------------------\n");
		content.append("from: ").append(msg.getFromUser().getUserName()).append("\n");
		content.append("to: ").append(userId).append("\n");
		content.append("sent: ").append( Utils.formatDate(msg.getCreationDate())).append("\n");
		content.append("subject: ").append(msg.getSubject()).append("\n-----------------------------\n\n");
		content.append(msg.getContent());
		return content.toString();
	}
	
	public void caterScopeForReply(String userId, RenderArgs renderArgs, Flash flash, String messageId) {
		
		UserMessage message = BeanProvider.getMessagesService().getMessageById(messageId);
		// security meseare: makes sure nobody tries to anwser to a message he did not receive...
		if ( message != null && userId.equals(message.getToUser().getUserName())) {
			flash.put(FLASH_FORM_MESSAGE_TO,message.getFromUser().getUserName());
			flash.put(FLASH_FORM_SUBJECT,"RE: " + message.getSubject());
			flash.put(FLASH_FORM_CONTENT, buildMessageRepliedContent(userId, message));
		} 
	}


	public void caterScopeForForward(String userId, RenderArgs renderArgs, Flash flash, String messageId) {
		UserMessage message = BeanProvider.getMessagesService().getMessageById(messageId);
		// security meseare: makes sure nobody tries to forwad to a message he did not receive or sent...
		if ( message != null && (userId.equals(message.getFromUser().getUserName()) || userId.equals(message.getToUser().getUserName()))) {
			flash.put(FLASH_FORM_SUBJECT,"FW: " + message.getSubject());
			flash.put(FLASH_FORM_CONTENT, buildMessageRepliedContent(userId, message));
		}
	}
	
}
