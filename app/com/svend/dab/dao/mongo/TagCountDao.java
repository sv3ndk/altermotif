package com.svend.dab.dao.mongo;


import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.svend.dab.core.beans.groups.GroupTagCount;
import com.svend.dab.core.beans.projects.TagCount;
import com.svend.dab.core.dao.ITagCountDao;

/**
 * @author svend
 *
 */
@Service
public class TagCountDao implements ITagCountDao {

	
	@Autowired
	private MongoTemplate mongoTemplate;

	
	/* (non-Javadoc)
	 * @see com.svend.dab.core.dao.ITagCountDao#getMostPopularTags(int)
	 */
	
	public List<TagCount> getMostPopularProjectTags(int maxResults) {

		Query query = query(where("value").gt(0));
		query.limit(maxResults);
		query.sort().on("value", Order.DESCENDING);
		
		return mongoTemplate.find(query, TagCount.class);
	}


	public List<TagCount> getMostPopularGroupTags(int maxResults) {
		Query query = query(where("value").gt(0));
		query.limit(maxResults);
		query.sort().on("value", Order.DESCENDING);
		
		List<GroupTagCount> groupTagCounts =  mongoTemplate.find(query, GroupTagCount.class);
		
		List<TagCount> response = new LinkedList<TagCount>();
		
		if (groupTagCounts != null) {
			for (GroupTagCount gtc : groupTagCounts) {
				response.add(new TagCount(gtc.getTag(), gtc.getValue()));
			}
		}
		
		return response;
	}

}
