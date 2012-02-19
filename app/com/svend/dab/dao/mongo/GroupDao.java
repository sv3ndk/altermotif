package com.svend.dab.dao.mongo;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.CollectionCallback;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.MongoException;
import com.mongodb.WriteResult;
import com.svend.dab.core.beans.groups.ProjectGroup;
import com.svend.dab.core.beans.profile.UserSummary;
import com.svend.dab.core.dao.IGroupDao;

@Service
public class GroupDao implements IGroupDao {

	@Autowired
	private MongoTemplate mongoTemplate;

	public void save(ProjectGroup group) {
		if (group != null) {
			mongoTemplate.save(group);
		}
	}

	public ProjectGroup retrieveGroupById(String groupId) {
		return mongoTemplate.findById(groupId, ProjectGroup.class);
	}

	public void updateParticipantOfAllGroupsWith(UserSummary updatedSummary) {
		if (updatedSummary != null) {
			mongoTemplate.updateMulti(query(where("participants.user._id").is(updatedSummary.getUserName())), new Update().set("participants.$.user", updatedSummary), ProjectGroup.class);
		}
	}

	public void updateGroupData(ProjectGroup editedGroup) {

		if (editedGroup != null) {
			mongoTemplate.updateFirst(query(where("id").is(editedGroup.getId())), 
					new Update()
						.set("name", editedGroup.getName())
						.set("description", editedGroup.getDescription())
						.set("location", editedGroup.getLocation())
						.set("themes", editedGroup.getThemes())
						.set("tags", editedGroup.getTags()),
					ProjectGroup.class);
		}

	}

	public void updateGroupActiveStatus(String groupId, boolean activeStatus) {
		mongoTemplate.updateFirst(query(where("id").is(groupId)), new Update().set("isActive", activeStatus), ProjectGroup.class); 
	}

	public void removeUserParticipant(final String groupId, final String participantId) {
		mongoTemplate.execute("projectGroup", new CollectionCallback<WriteResult>() {
			public WriteResult doInCollection(DBCollection collection) throws MongoException, DataAccessException {
				BasicDBObject queryDbo = new BasicDBObject("_id", groupId);
				BasicDBObject pullDbo = new BasicDBObject("$pull", new BasicDBObject("participants", new BasicDBObject("user._id", participantId)));
				return collection.update(queryDbo, pullDbo);
			}
		});
	}

}
