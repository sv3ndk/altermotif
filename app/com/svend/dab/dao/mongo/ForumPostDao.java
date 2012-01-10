package com.svend.dab.dao.mongo;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.svend.dab.core.beans.projects.ForumPost;
import com.svend.dab.core.dao.IForumPostDao;

@Service
public class ForumPostDao implements IForumPostDao {

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public List<ForumPost> getPosts(String threadId) {
		List<ForumPost> posts =  mongoTemplate.find(query(where("threadId").is(threadId)), ForumPost.class);

		if (posts == null) {
			posts = new LinkedList<ForumPost>();
		}
		
		return posts;
	}

}
