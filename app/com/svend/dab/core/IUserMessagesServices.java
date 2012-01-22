package com.svend.dab.core;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.svend.dab.core.beans.message.UserMessage;


/**
 * @author Svend
 *
 */
public interface IUserMessagesServices {
	
	public void sendMessage(String userName, String toUsername, String subject, String messageContent);
	
	/**
	 * 
	 * Get the received message corresponding to the page indicated by this inboxPageMarked. If visibleMessageId is not null, this method will also 
	 * automatically move forward in the pages until it finds the page with this message id
	 * 
	 * @param userName
	 * @param currentInboxPageNumber
	 * @param visibleMessageId
	 * @return
	 */
	public List<UserMessage> getReceivedMessages(String userName, int pageNumber);
	
	
	public List<UserMessage> getWrittenMessages(String fromUserName, int pageNumber);

	public List<UserMessage> getDeletedMessages(String username, int pageNumber);
	
	public  List<UserMessage> getUnreadReceivedMessages(String username);
	
	public void markMessageAsRead(String readMessageId);

	public Long getNumberOfUnreadMessages(String username);
	public boolean isThereMoreInboxPagesThen(String username, int pageNumber);

	public boolean isThereMoreOutboxPagesThen(String username, int pageNumber);
	
	public boolean isThereMoreDeletedPagesThen(String username, int pageNumber);
	
	public void markMessagesAsDeletedByRecipient(Collection<String> messageIds, String recipientId);

	public void markMessagesAsDeletedByEmitter(Set<String> ids, String emitterId);

	public void undeleteMessages(List<String> undeletedMessages, String username);

	public UserMessage getMessageById(String messageId);

	public int getInboxPageNumberOfMessage(String loggedInUserProfileId, String desiredMessagesId);




	





	
}
