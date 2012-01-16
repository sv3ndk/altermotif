package com.svend.dab.dao.mongo;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

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
	@Override
	public List<ForumThread> loadProjectForumThreads(String projectId) {
		Query query = query(where("projectId").is(projectId));
		query.sort().on("creationDate",  Order.DESCENDING);
		return mongoTemplate.find(query, ForumThread.class);
	}

	@Override
	public ForumThread createNewThread(ForumThread forumThread) {
		mongoTemplate.save(forumThread);
		return forumThread;
	}

	@Override
	public void updateThreadVisibility(String projectId, String threadId, boolean isThreadPublic) {
		mongoTemplate.setWriteConcern(WriteConcern.MAJORITY);
		mongoTemplate.updateFirst(query(where("id").is(threadId)), new Update().set("isThreadPublic", isThreadPublic), ForumThread.class);
	}

	@Override
	public ForumThread getThreadById(String threadId) {
		return mongoTemplate.findById(threadId, ForumThread.class);
	}

	@Override
	public void deleteThread(String projectId, String threadId) {
		mongoTemplate.remove(query(where("id").is(threadId)), ForumThread.class);
	}

	@Override
	public void updateNumberOfPosts(String threadId, Long numberOfPosts) {
		mongoTemplate.updateFirst(query(where("id").is(threadId)), new Update().set("numberOfPosts", numberOfPosts), ForumThread.class);
	}

}
