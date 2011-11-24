package models.altermotif.messages;

import controllers.validators.ExistingUsernameValidator;
import play.data.validation.CheckWith;
import play.data.validation.Required;

public class NewMessage {

	
	@Required(message="missingNewMessageRecipient")
	@CheckWith(message="newMessageUnknownRecipient", value=ExistingUsernameValidator.class)
	private String toUserName;

	private String subject;

	@Required(message="missingNewMessageContent")
	private String messageContent;
	
	
	public String getMessageContent() {
		return messageContent;
	}

	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getToUserName() {
		return toUserName;
	}

	public void setToUserName(String toUserName) {
		this.toUserName = toUserName;
	}
	

}
