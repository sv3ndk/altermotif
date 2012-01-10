package com.svend.dab.dao.mongo;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

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
	
	
	/* (non-Javadoc)
	 * @see com.svend.dab.core.dao.IForumThreadDao#loadProjectForumThreads(java.lang.String)
	 */
	@Override
	public List<ForumThread> loadProjectForumThreads(String projectId) {
		return mongoTemplate.find(query(where("projectId").is(projectId)), ForumThread.class);
	}


	@Override
	public void createNewThread(ForumThread forumThread) {
		mongoTemplate.save(forumThread);
	}

}
