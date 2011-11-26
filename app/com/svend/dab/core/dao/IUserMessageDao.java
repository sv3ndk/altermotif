package com.svend.dab.core.dao;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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
	
	public Page<UserMessage> findDeletedMessages(String username, Pageable pageable);
	
	public List<UserMessage> retrieveUserMessageById(List<String> messageIds);
	public abstract UserMessage retrieveUserMessageById(String messageId);

	public void markMessageAsDeletedByRecipient(Collection<String> messageIds, String recipientId);
	public void markMessageAsDeletedByEmitter(List<String> messageIds);
	
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
	
	
	public abstract List<UserMessage> findAllUserMessageBytoUserUserNameAndDeletedByRecipient(String toUserName, boolean deletedByRecipient, int pageNumber, int inboxOutboxPageSize);
	
	public Page<UserMessage> findAllUserMessageByFromUserUserNameAndDeletedByEmitter(String fromUserName, Boolean deletedByEmitter, Pageable pageable);

	public List<UserMessage> findAllUserMessageBytoUserUserNameAndReadAndDeletedByRecipient(String username, boolean read, boolean deletedByRecipient);

	public abstract void save(List<UserMessage> foundMessages);

	public abstract void save(UserMessage userMessage);





}
