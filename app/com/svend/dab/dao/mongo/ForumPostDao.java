package com.svend.dab.dao.mongo;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
import com.svend.dab.core.beans.ForumPost;
import com.svend.dab.core.beans.profile.UserSummary;
import com.svend.dab.core.dao.IForumPostDao;

@Service
public class ForumPostDao implements IForumPostDao {

	@Autowired
	private MongoTemplate mongoTemplate;

	
	public ForumPost loadPost(String postId) {
		return mongoTemplate.findById(postId, ForumPost.class);
	}

	
	public List<ForumPost> getAllPosts(String threadId) {
		Query query = query(where("threadId").is(threadId));
		query.sort().on("creationDate", Order.DESCENDING);

		List<ForumPost> posts = mongoTemplate.find(query, ForumPost.class);

		if (posts == null) {
			posts = new LinkedList<ForumPost>();
		}
		return posts;
	}

	
	public void saveNewPost(ForumPost createdPost) {
		mongoTemplate.save(createdPost);
	}

	
	public Set<String> findAllPostIdsOfThread(String threadId) {
		Query query = query(where("threadId").is(threadId));
		query.fields().include("id");

		List<ForumPost> posts = mongoTemplate.find(query, ForumPost.class);

		Set<String> response = new HashSet<String>();
		if (posts != null) {
			for (ForumPost post : posts) {
				response.add(post.getId());
			}
		}

		return response;
	}

	
	public List<ForumPost> findThreadPostsExcluding(String threadId, Set<String> excludedPostIds) {
		Query query = query(where("threadId").is(threadId).and("id").nin(excludedPostIds.toArray()));
		query.sort().on("creationDate", Order.DESCENDING);
		return mongoTemplate.find(query, ForumPost.class);
	}

	
	public void updateAuthorOfAllPostsFrom(UserSummary updatedSummary) {
		if (updatedSummary != null) {
			mongoTemplate.updateMulti(query(where("author._id").is(updatedSummary.getUserName())), new Update().set("author", updatedSummary), ForumPost.class);
		}
	}

	
	public void deletePost(String postId) {
		mongoTemplate.remove(query(where("id").is(postId)), ForumPost.class);
	}

	
	public void deletePostsOfThread(String threadId) {
		mongoTemplate.remove(query(where("threadId").is(threadId)), ForumPost.class);
	}

	
	public Long countPostOfThread(final String threadId) {
		return mongoTemplate.execute("forumPost", new CollectionCallback<Long>() {
			
			public Long doInCollection(DBCollection collection) throws MongoException, DataAccessException {
				return collection.count(query(where("threadId").is(threadId)).getQueryObject());
			}
		});
	}

	
	public void updateThreadIdOfPost(String postId, String originalThreadId, String targetThreadId, Date updatedCreationDate, String updatedContent) {
		// we also query on the original threadId as a security measure
		mongoTemplate.updateFirst(query(where("id").is(postId).and("threadId").is(originalThreadId)), new Update().set("threadId", targetThreadId).set("creationDate", updatedCreationDate).set("content", updatedContent), ForumPost.class);
	}

}
