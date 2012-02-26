package com.svend.dab.dao.mongo;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.CollectionCallback;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.mongodb.DBCollection;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;
import com.svend.dab.core.beans.projects.ForumThread;
import com.svend.dab.core.dao.IForumThreadDao;

/**
 * @author svend
 * 
 */
@Service
public class ForumThreadDao implements IForumThreadDao {

	@Autowired
	private MongoTemplate mongoTemplate;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.svend.dab.core.dao.IForumThreadDao#loadProjectForumThreads(java.lang.String)
	 */
	
	public List<ForumThread> loadProjectForumThreads(String projectId) {
		Query query = query(where("projectId").is(projectId));
		query.sort().on("creationDate",  Order.DESCENDING);
		return mongoTemplate.find(query, ForumThread.class);
	}

	
	public ForumThread createNewThread(ForumThread forumThread) {
		mongoTemplate.save(forumThread);
		return forumThread;
	}

	
	public void updateThreadVisibility(String projectId, String threadId, boolean isThreadPublic) {
		mongoTemplate.setWriteConcern(WriteConcern.MAJORITY);
		mongoTemplate.updateFirst(query(where("id").is(threadId)), new Update().set("isThreadPublic", isThreadPublic), ForumThread.class);
	}

	
	public ForumThread getThreadById(String threadId) {
		return mongoTemplate.findById(threadId, ForumThread.class);
	}

	
	public void deleteThread(String projectId, String threadId) {
		mongoTemplate.remove(query(where("id").is(threadId)), ForumThread.class);
	}

	
	public void updateNumberOfPosts(String threadId, Long numberOfPosts) {
		mongoTemplate.updateFirst(query(where("id").is(threadId)), new Update().set("numberOfPosts", numberOfPosts), ForumThread.class);
	}


	public Long countThreadsOfProject(String projectId) {
		final Query query = query(where("projectId").is(projectId));
		return mongoTemplate.execute("forumThread", new CollectionCallback<Long>() {
			public Long doInCollection(DBCollection collection) throws MongoException, DataAccessException {
				return collection.count(query.getQueryObject());
			}
		});
	}

}
