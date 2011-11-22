package com.svend.dab.core;

import java.util.List;

import org.springframework.data.domain.Page;

import com.svend.dab.core.beans.message.UserMessage;


/**
 * @author Svend
 *
 */
public interface IMessagesServices {
	
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
	public Page<UserMessage> getReceivedMessages(String userName, int pageNumber);
	
	
	public Page<UserMessage> getWrittenMessages(String fromUserName, int pageNumber);

	public Page<UserMessage> getDeletedMessages(String username, int pageNumber);
	
	public  List<UserMessage> getUnreadReceivedMessages(String username);
	
	
	public void markMessageAsRead(String readMessageId);

	public Long getNumberOfUnreadMessages(String username);
	


	public void markMessagesAsDeletedByRecipient(List<String> deletedMessages);

	public void markMessagesAsDeletedByEmitter(List<String> deletedMessages);

	public void undeleteMessages(List<String> undeletedMessages, String username);
	





	
}
