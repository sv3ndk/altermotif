package com.svend.dab.core.dao;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.svend.dab.core.beans.message.UserMessage;
import com.svend.dab.core.beans.profile.ProfileRef;

/**
 * @author svend
 *
 */
public interface IUserMessageDao {

	public abstract void  markMessageAsRead(String messageId);
	
	public abstract Long countNumberOfUnreadMessages(String username);

	public abstract Long countNumberOfReceivedMessages(String username);

	public abstract long countNumberOfReceivedMessagesBefore(String userid, String messageId);

	public abstract long countNumberOfWrittenMessages(String username);
	
	public abstract long countNumberOfDeletedMessages(String username);
	public abstract List<UserMessage> findDeletedMessages(String username, int pageNumber, int inboxOutboxPageSize);
	
	public List<UserMessage> retrieveUserMessageById(Collection<String> messageIds);
	public abstract UserMessage retrieveUserMessageById(String messageId);

	public void markMessageAsDeletedByRecipient(Collection<String> messageIds, String recipientId);
	
	public abstract void markMessageAsDeletedByEmitter(Set<String> messageIds, String emitterId);
	
	/**
	 * updates all messages where this user is the "from user" and replace the fromUser field
	 * 
	 * @param profileRef
	 */
	public void updateFromUserProfileRef(ProfileRef profileRef);

	/**
	 * updates all messages where this user is the "to user" and replace the toUser field
	 * 
	 * @param profileRef
	 */
	public void updateTOUserProfileRef(ProfileRef profileRef);
	
	
	public abstract List<UserMessage> findAllUserMessageBytoUserUserNameAndDeletedByRecipient(String toUserName, int pageNumber, int inboxOutboxPageSize);
	
	public List<UserMessage> findAllUserMessageByFromUserUserNameAndDeletedByEmitter(String fromUserName, int pageNumber, int pageSize);
		
	public List<UserMessage> findAllUserMessageBytoUserUserNameAndReadAndDeletedByRecipient(String username, boolean read);

	public abstract void save(List<UserMessage> foundMessages);

	public abstract void save(UserMessage userMessage);









}
