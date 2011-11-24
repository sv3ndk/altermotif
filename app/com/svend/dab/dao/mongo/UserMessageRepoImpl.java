/**
 * 
 */
package com.svend.dab.dao.mongo;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.CollectionCallback;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
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
	@Override
	public void markMessageAsRead(String messageId) {
		mongoTemplate.updateFirst(query(where("id").is(messageId)), new Update().set("read", Boolean.TRUE), UserMessage.class);
	}

	@Override
	public Long countNumberOfUnreadMessages(String username) {
		final Query query = query(where("toUser._id").is(username).and("read").is(false).and("deletedByRecipient").is(false));

		return mongoTemplate.execute("userMessage", new CollectionCallback<Long>() {
			@Override
			public Long doInCollection(DBCollection collection) throws MongoException, DataAccessException {
				return collection.count(query.getQueryObject());
			}
		});

	}

	@Override
	public Page<UserMessage> findDeletedMessages(String username, Pageable pageable) {
		final Query query = query(new Criteria().orOperator(where("toUser._id").is(username).and("deletedByRecipient").is(true),
				where("fromUser._id").is(username).and("deletedByEmitter").is(true)));

		query.limit(pageable.getPageSize());
		query.skip(pageable.getOffset());

		// TODO: shouldn't we count the number of result in the query instead?
		Long count = mongoTemplate.getCollection("userMessage").count();

		List<UserMessage> list = mongoTemplate.find(query, UserMessage.class);
		logger.log(Level.INFO, "found " + list.size());
		return new PageImpl<UserMessage>(list, pageable, count);
	}

	@Override
	public void markMessageAsDeletedByRecipient(List<String> messageIds) {
		
		
		for (UserMessage msg : retrieveUserMessageById(messageIds)) {
			msg.setDeletedByRecipient(Boolean.TRUE);
			mongoTemplate.save(msg);
		}
	}

	@Override
	public void markMessageAsDeletedByEmitter(List<String> messageIds) {

		// TODO: optimize this
		for (UserMessage msg : retrieveUserMessageById(messageIds)) {
			msg.setDeletedByEmitter(Boolean.TRUE);
			mongoTemplate.save(msg);
		}

	}

	@Override
	public List<UserMessage> retrieveUserMessageById(List<String> ids) {

		List<Criteria> criterias= new LinkedList<Criteria>();

		for (String id : ids) {
			criterias.add(where("id").is(id));
		}

		Query theQuery = query( new Criteria().orOperator(criterias.toArray(new Criteria []{})));

		return mongoTemplate.find(theQuery, UserMessage.class);
	}

	@Override
	public void updateFromUserProfileRef(ProfileRef profileRef) {
		Query query = query(where("fromUser._id").is(profileRef.getUserName()));
		Update update = new Update().set("fromUser", profileRef);
		mongoTemplate.updateMulti(query, update, UserMessage.class);
	}

	@Override
	public void updateTOUserProfileRef(ProfileRef profileRef) {
		Query query = query(where("toUser._id").is(profileRef.getUserName()));
		Update update = new Update().set("toUser", profileRef);
		mongoTemplate.updateMulti(query, update, UserMessage.class);
	}

	
	@Override
	public List<UserMessage> findAllUserMessageBytoUserUserNameAndDeletedByRecipient(String toUserName, boolean deletedByRecipient, int pageNumber, int pageSize) {
		
		if (pageNumber >= 0 && pageSize > 0) {
			Query query = query(where("toUser._id").is(toUserName).and("deletedByRecipient").is(deletedByRecipient) ).limit(pageSize).skip(pageNumber*pageSize);
			return mongoTemplate.find(query, UserMessage.class);
		} else {
			return new LinkedList<UserMessage>();
		}
	}

	@Override
	public Page<UserMessage> findAllUserMessageByFromUserUserNameAndDeletedByEmitter(String fromUserName, Boolean deletedByEmitter, Pageable pageable) {
		throw new NotImplementedException("TODO (play migration)");
	}

	@Override
	public List<UserMessage> findAllUserMessageBytoUserUserNameAndReadAndDeletedByRecipient(String username, boolean read, boolean deletedByRecipient, Sort sort) {
		throw new NotImplementedException("TODO (play migration)");
	}

	@Override
	public void save(List<UserMessage> messages) {
		
		for (UserMessage msg : messages) {
			mongoTemplate.save (msg);
		}
		
	}

	@Override
	public void save(UserMessage userMessage) {
		mongoTemplate.save(userMessage);
		
	}
}
