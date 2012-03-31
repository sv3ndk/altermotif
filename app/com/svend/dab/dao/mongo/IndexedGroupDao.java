package com.svend.dab.dao.mongo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.CollectionCallback;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.MongoException;
import com.svend.dab.core.beans.groups.IndexedGroup;
import com.svend.dab.core.dao.IIndexedGroupDao;

/**
 * @author svend
 *
 */
@Service
public class IndexedGroupDao implements IIndexedGroupDao {

	@Autowired
	private MongoTemplate mongoTemplate;
	
	/* (non-Javadoc)
	 * @see com.svend.dab.core.dao.IIndexedGroupDao#updateIndex(com.svend.dab.core.beans.groups.IndexedGroup)
	 */
	public void updateIndex(IndexedGroup indexedGroup) {
		mongoTemplate.save(indexedGroup);
	}

	public void ensureIndexOnLocation() {
		// "ensureIndex" seems to be missing in RC1 version of mongo Spring Data
		mongoTemplate.execute("indexedGroup", new CollectionCallback<Long>() {
			public Long doInCollection(DBCollection collection) throws MongoException, DataAccessException {
				BasicDBObject indexDbo = new BasicDBObject();
				indexDbo.put("location", "2d");
				collection.ensureIndex(indexDbo);
				return 0l;
			}
		});
		
	}

}
