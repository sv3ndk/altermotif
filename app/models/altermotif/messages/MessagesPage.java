package models.altermotif.messages;

import java.util.List;

import com.svend.dab.core.beans.message.UserMessage;

public class MessagesPage {

	private List<UserMessage> messages;
	
	private boolean nextPageExists;
	private boolean previousPageExists;
	

	public List<UserMessage> getMessages() {
		return messages;
	}

	public boolean isNextPageExists() {
		return nextPageExists;
	}

	public void setNextPageExists(boolean nextPageExists) {
		this.nextPageExists = nextPageExists;
	}

	public boolean isPreviousPageExists() {
		return previousPageExists;
	}

	public void setPreviousPageExists(boolean previousPageExists) {
		this.previousPageExists = previousPageExists;
	}

	public void setMessages(List<UserMessage> messages) {
		this.messages = messages;
	}
	
}
