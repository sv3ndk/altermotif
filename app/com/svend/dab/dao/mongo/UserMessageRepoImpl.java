  /**
 * 
 */
package com.svend.dab.dao.mongo;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.CollectionCallback;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.mongodb.DBCollection;
import com.mongodb.MongoException;
import com.svend.dab.core.beans.message.UserMessage;
import com.svend.dab.core.beans.profile.ProfileRef;
import com.svend.dab.core.dao.IUserMessageDao;

/**
 * @author svend
 * 
 */
@Service
public class UserMessageRepoImpl implements IUserMessageDao {

	@Autowired
	private MongoTemplate mongoTemplate;

	private static Logger logger = Logger.getLogger(UserMessageRepoImpl.class.getName());

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.svend.dab.dao.mongo.IUserMessageDao#markMesasgeAsRead(java.lang.String)
	 */
	
	public void markMessageAsRead(String messageId) {
		mongoTemplate.updateFirst(query(where("id").is(messageId)), new Update().set("read", Boolean.TRUE), UserMessage.class);
	}

	
	public Long countNumberOfUnreadMessages(String username) {
		final Query query = query(where("toUser._id").is(username).and("read").is(false).and("deletedByRecipient").is(false));
		return mongoTemplate.execute("userMessage", new CollectionCallback<Long>() {
			
			public Long doInCollection(DBCollection collection) throws MongoException, DataAccessException {
				return collection.count(query.getQueryObject());
			}
		});
	}
	
	
	public Long countNumberOfReceivedMessages(String username) {
		final Query query = query(where("toUser._id").is(username).and("deletedByRecipient").is(false));
		return mongoTemplate.execute("userMessage", new CollectionCallback<Long>() {
			
			public Long doInCollection(DBCollection collection) throws MongoException, DataAccessException {
				return collection.count(query.getQueryObject());
			}
		});
	}
	
	
	
	public long countNumberOfReceivedMessagesBefore(String username, String messageId) {
		
		UserMessage message = retrieveUserMessageById(messageId);
		if (message == null) {
			return 0l;
		} else {
			final Query query = query(where("toUser._id").is(username).and("deletedByRecipient").is(false).and("creationDate").gt(message.getCreationDate()));
			query.sort().on("creationDate", Order.DESCENDING);
			return mongoTemplate.execute("userMessage", new CollectionCallback<Long>() {
				
				public Long doInCollection(DBCollection collection) throws MongoException, DataAccessException {
					return collection.count(query.getQueryObject());
				}
			});
		}
	}

	
	
	public long countNumberOfWrittenMessages(String username) {
		final Query query = query(where("fromUser._id").is(username).and("deletedByEmitter").is(false));
		return mongoTemplate.execute("userMessage", new CollectionCallback<Long>() {
			
			public Long doInCollection(DBCollection collection) throws MongoException, DataAccessException {
				return collection.count(query.getQueryObject());
			}
		});
	}

	

	
	public long countNumberOfDeletedMessages(String username) {
		Criteria deletedAsRecipientCriterium = where("toUser._id").is(username).and("deletedByRecipient").is(true);
		Criteria deletedAsEmitterCriterium = where("fromUser._id").is(username).and("deletedByEmitter").is(true);
		final Query query = query(new Criteria().orOperator(new Criteria[] {deletedAsRecipientCriterium, deletedAsEmitterCriterium}));
		
		return mongoTemplate.execute("userMessage", new CollectionCallback<Long>() {
			
			public Long doInCollection(DBCollection collection) throws MongoException, DataAccessException {
				return collection.count(query.getQueryObject());
			}
		});
	}

	
	
	
	
	public void markMessageAsDeletedByRecipient(Collection<String> messageIds, String recipientId) {
		Query theQuery = query(where("toUser._id").is(recipientId).and("id").in(messageIds));
		Update update = new Update().set("deletedByRecipient", true);
		mongoTemplate.updateMulti(theQuery, update, UserMessage.class);
	}

	
	public void markMessageAsDeletedByEmitter(Set<String> messageIds, String emitterId) {
		Query theQuery = query(where("fromUser._id").is(emitterId).and("id").in(messageIds));
		Update update = new Update().set("deletedByEmitter", true);
		mongoTemplate.updateMulti(theQuery, update, UserMessage.class);
	}

	
	public List<UserMessage> retrieveUserMessageById(List<String> ids) {
		return mongoTemplate.find(query(where("id").in(ids)), UserMessage.class);
	}
	
	
	
	
	public UserMessage retrieveUserMessageById(String messageId) {
		return mongoTemplate.findById(messageId, UserMessage.class);
	}
	

	
	public void updateFromUserProfileRef(ProfileRef profileRef) {
		Query query = query(where("fromUser._id").is(profileRef.getUserName()));
		Update update = new Update().set("fromUser", profileRef);
		mongoTemplate.updateMulti(query, update, UserMessage.class);
	}

	
	public void updateTOUserProfileRef(ProfileRef profileRef) {
		Query query = query(where("toUser._id").is(profileRef.getUserName()));
		Update update = new Update().set("toUser", profileRef);
		mongoTemplate.updateMulti(query, update, UserMessage.class);
	}

	
	
	public List<UserMessage> findAllUserMessageBytoUserUserNameAndDeletedByRecipient(String toUserName, int pageNumber, int pageSize) {
		if (pageNumber >= 0 && pageSize > 0) {
			Query query = query(where("toUser._id").is(toUserName).and("deletedByRecipient").is(false) ).limit(pageSize).skip(pageNumber*pageSize);
			query.sort().on("creationDate", Order.DESCENDING);
			return mongoTemplate.find(query, UserMessage.class);
		} else {
			return new LinkedList<UserMessage>();
		}
	}

	
	public List<UserMessage> findAllUserMessageByFromUserUserNameAndDeletedByEmitter(String fromUserName,  int pageNumber, int pageSize) {
		if (pageNumber >= 0 && pageSize > 0) {
			Query query = query(where("fromUser._id").is(fromUserName).and("deletedByEmitter").is(false) ).limit(pageSize).skip(pageNumber*pageSize);
			query.sort().on("creationDate", Order.DESCENDING);
			return mongoTemplate.find(query, UserMessage.class);
		} else {
			return new LinkedList<UserMessage>();
		}
	}

	
	public List<UserMessage> findAllUserMessageBytoUserUserNameAndReadAndDeletedByRecipient(String username, boolean read) {
		Query query = query(where("toUser._id").is(username).and("read").is(read).and("deletedByRecipient").is(false));
		query.sort().on("creationDate", Order.DESCENDING);
		return mongoTemplate.find(query, UserMessage.class);
	}

	
	
	public List<UserMessage> findDeletedMessages(String username, int pageNumber, int inboxOutboxPageSize) {
		Criteria deletedAsRecipientCriterium = where("toUser._id").is(username).and("deletedByRecipient").is(true);
		Criteria deletedAsEmitterCriterium = where("fromUser._id").is(username).and("deletedByEmitter").is(true);
		Query query = query(new Criteria().orOperator(new Criteria[] {deletedAsRecipientCriterium, deletedAsEmitterCriterium}));
		query.sort().on("creationDate", Order.DESCENDING);
		return mongoTemplate.find(query, UserMessage.class);
	}
	
	
	
	public void save(List<UserMessage> messages) {
		for (UserMessage msg : messages) {
			save (msg);
		}
	}

	
	public void save(UserMessage userMessage) {
		mongoTemplate.save(userMessage);
	}


}