package com.svend.dab.core;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

import com.svend.dab.core.beans.Config;
import com.svend.dab.core.beans.message.UserMessage;
import com.svend.dab.core.dao.IUserMessageDao;
import com.svend.dab.eda.EventEmitter;
import com.svend.dab.eda.events.messages.MessageWrittenEvent;

/**
 * @author Svend
 * 
 */
@Component("messagesServices")
public class MessagesService implements IMessagesServices, Serializable {

	private static final long serialVersionUID = -8226701664350200145L;

	// -----------------------------------
	// helper beans

	
	@Autowired
	private IUserMessageDao userMessageDao;

	private static Logger logger = Logger.getLogger(MessagesService.class.getName());
	
	@Autowired
	private EventEmitter eventEmitter;
	
	@Autowired
	private Config config;

	// -----------------------------------
	//
	

	@Override
	public void sendMessage(String fromUserName, String toUsername, String subject, String messageContent) {
		eventEmitter.emit(new MessageWrittenEvent(fromUserName, toUsername, subject, messageContent));
	}

	@Override
	public List<UserMessage> getReceivedMessages(String toUserName, int pageNumber) {
		//PageRequest pageRequest = new PageRequest(pageNumber, config.getInboxOutboxPageSize(), new Sort(Direction.DESC, "creationDate"));
		return userMessageDao.findAllUserMessageBytoUserUserNameAndDeletedByRecipient(toUserName, false, pageNumber, config.getInboxOutboxPageSize());
	}

	@Override
	public Page<UserMessage> getWrittenMessages(String fromUserName, int pageNumber) {
		PageRequest pageRequest = new PageRequest(pageNumber, config.getInboxOutboxPageSize(), new Sort(Direction.DESC, "creationDate"));
		return userMessageDao.findAllUserMessageByFromUserUserNameAndDeletedByEmitter(fromUserName, false, pageRequest);
	}
	
	@Override
	public Page<UserMessage> getDeletedMessages(String username, int pageNumber) {
		PageRequest pageRequest = new PageRequest(pageNumber, config.getInboxOutboxPageSize(), new Sort(Direction.DESC, "creationDate"));
		return userMessageDao.findDeletedMessages(username, pageRequest);
	}
	
	@Override
	public List<UserMessage> getUnreadReceivedMessages(String username) {
		return userMessageDao.findAllUserMessageBytoUserUserNameAndReadAndDeletedByRecipient(username, false, false, new Sort(Direction.DESC, "creationDate"));
	}
	
	@Override
	public void markMessageAsRead(String messageId) {
		userMessageDao.markMessageAsRead(messageId);
	}

	@Override
	public Long getNumberOfUnreadMessages(String username) {
		return userMessageDao.countNumberOfUnreadMessages(username);
	}
	
	
	/* (non-Javadoc)
	 * @see com.svend.dab.core.IMessagesServices#markMessagesAsDeletedByRecipient(java.util.List)
	 */
	@Override
	public void markMessagesAsDeletedByRecipient(List<String> messageIds) {
		if (messageIds != null && messageIds.size() > 0) {
			userMessageDao.markMessageAsDeletedByRecipient(messageIds);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.svend.dab.core.IMessagesServices#markMessagesAsDeletedByEmitter(java.util.List)
	 */
	@Override
	public void markMessagesAsDeletedByEmitter(List<String> deletedMessages) {
		if (deletedMessages != null && deletedMessages.size() > 0) {
			userMessageDao.markMessageAsDeletedByEmitter(deletedMessages);
		} else {
			logger.log(Level.WARNING, "This is weird: received a request to delete an empty list of outbox messages => not doing anything");
		}
	}
	
	@Override
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
	





}
