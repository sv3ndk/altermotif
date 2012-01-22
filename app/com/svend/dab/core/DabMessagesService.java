package com.svend.dab.core;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;
import com.svend.dab.core.beans.Config;
import com.svend.dab.core.beans.message.UserMessage;
import com.svend.dab.core.dao.IUserMessageDao;
import com.svend.dab.eda.EventEmitter;
import com.svend.dab.eda.events.messages.MessageWrittenEvent;

/**
 * @author Svend
 * 
 */
@Component
public class DabMessagesService implements IUserMessagesServices, Serializable {

	private static final long serialVersionUID = -8226701664350200145L;

	// -----------------------------------
	// helper beans

	
	@Autowired
	private IUserMessageDao userMessageDao;

	private static Logger logger = Logger.getLogger(DabMessagesService.class.getName());
	
	@Autowired
	private EventEmitter eventEmitter;
	
	@Autowired
	private Config config;

	// -----------------------------------
	//
	

	public void sendMessage(String fromUserName, String toUsername, String subject, String messageContent) {
		eventEmitter.emit(new MessageWrittenEvent(fromUserName, toUsername, subject, messageContent));
	}

	public List<UserMessage> getReceivedMessages(String toUserName, int pageNumber) {
		return userMessageDao.findAllUserMessageBytoUserUserNameAndDeletedByRecipient(toUserName, pageNumber, config.getInboxOutboxPageSize());
	}
	
	public boolean isThereMoreInboxPagesThen(String username, int pageNumber) {
		long numberOfReceivedMessages = userMessageDao.countNumberOfReceivedMessages(username);
		return numberOfReceivedMessages > (pageNumber + 1 ) * config.getInboxOutboxPageSize();
	}

	public boolean isThereMoreOutboxPagesThen(String username, int pageNumber) {
		long numberOfWrittendMessages = userMessageDao.countNumberOfWrittenMessages(username);
		return numberOfWrittendMessages > (pageNumber + 1 ) * config.getInboxOutboxPageSize();
	}

	
	public boolean isThereMoreDeletedPagesThen(String username, int pageNumber) {
		long numberOfDeletedMessages = userMessageDao.countNumberOfDeletedMessages(username);
		return numberOfDeletedMessages > (pageNumber + 1 ) * config.getInboxOutboxPageSize();
	}


	public List<UserMessage> getWrittenMessages(String fromUserName, int pageNumber) {
		return userMessageDao.findAllUserMessageByFromUserUserNameAndDeletedByEmitter(fromUserName, pageNumber, config.getInboxOutboxPageSize());
	}
	
	public List<UserMessage> getDeletedMessages(String username, int pageNumber) {
		return userMessageDao.findDeletedMessages(username, pageNumber, config.getInboxOutboxPageSize());
	}
	
	public List<UserMessage> getUnreadReceivedMessages(String username) {
		return userMessageDao.findAllUserMessageBytoUserUserNameAndReadAndDeletedByRecipient(username, false);
	}
	
	public void markMessageAsRead(String messageId) {
		userMessageDao.markMessageAsRead(messageId);
	}

	public Long getNumberOfUnreadMessages(String username) {
		return userMessageDao.countNumberOfUnreadMessages(username);
	}
	
	
	
	public void markMessagesAsDeletedByRecipient(Collection<String> messageIds, String recipientId) {
		if (!Strings.isNullOrEmpty(recipientId) && CollectionUtils.isNotEmpty(messageIds)) {
		 userMessageDao.markMessageAsDeletedByRecipient(messageIds, recipientId);
		}
	}
	
	
	public void markMessagesAsDeletedByEmitter(Set<String> messageIds, String emitterId) {
		if (!Strings.isNullOrEmpty(emitterId) && CollectionUtils.isNotEmpty(messageIds)) {
			userMessageDao.markMessageAsDeletedByEmitter(messageIds, emitterId);
		}
	}

	
	public void undeleteMessages(List<String> undeletedMessagesIds, String username) {
		if (undeletedMessagesIds != null && undeletedMessagesIds.size() > 0 && username != null) {
			
			List<UserMessage> foundMessages = userMessageDao.retrieveUserMessageById(undeletedMessagesIds);
			logger.log(Level.INFO, "found " + foundMessages.size() + " corresponding messages in db");
			
			// undeleting messages deleted by this user 
			for (UserMessage msg : foundMessages) {
				if (username.equals(msg.getFromUser().getUserName()) ) {
					msg.setDeletedByEmitter(false);
				} 
				
				// this is not a "else": the message could be sent from and to the same user...
				if (username.equals(msg.getToUser().getUserName())) {
					msg.setDeletedByRecipient(false);
				}
			}
			
			userMessageDao.save(foundMessages);
		}
	}

	public UserMessage getMessageById(String messageId) {
		return userMessageDao.retrieveUserMessageById(messageId);
	}

	public int getInboxPageNumberOfMessage(String userid, String messageId) {
		
		if (Strings.isNullOrEmpty(userid)  || Strings.isNullOrEmpty(messageId)) {
			return 0;
		} else {
			long messageOrder = userMessageDao.countNumberOfReceivedMessagesBefore(userid, messageId);
			return (int) Math.ceil(messageOrder / config.getInboxOutboxPageSize());
		}
	}









}
