package com.svend.dab.dao.mongo;

import static org.springframework.data.mongodb.core.mapreduce.MapReduceOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.HashSet;
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

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.MongoException;
import com.mongodb.WriteResult;
import com.svend.dab.core.beans.groups.GroupParticipant;
import com.svend.dab.core.beans.groups.GroupParticipant.ROLE;
import com.svend.dab.core.beans.groups.GroupProjectParticipant;
import com.svend.dab.core.beans.groups.GroupTagCount;
import com.svend.dab.core.beans.groups.ProjectGroup;
import com.svend.dab.core.beans.profile.Photo;
import com.svend.dab.core.beans.profile.UserSummary;
import com.svend.dab.core.beans.projects.Project.STATUS;
import com.svend.dab.core.beans.projects.ProjectSummary;
import com.svend.dab.core.beans.projects.SearchQuery.SORT_KEY;
import com.svend.dab.core.dao.IGroupDao;

@Service
public class GroupDao implements IGroupDao {

	@Autowired
	private MongoTemplate mongoTemplate;

	public Set<String> getAllGroupsIds() {
		Query query = query(where("_id").exists(true));
		query.fields().include("_id");
		List<ProjectGroup> list = mongoTemplate.find(query, ProjectGroup.class);
		Set<String> ids = new HashSet<String>();

		if (list != null) {
			for (ProjectGroup group : list) {
				ids.add(group.getId());
			}
		}
		return ids;

	}

	public void save(ProjectGroup group) {
		if (group != null) {
			mongoTemplate.save(group);
		}
	}

	public ProjectGroup retrieveGroupById(String groupId) {
		return mongoTemplate.findById(groupId, ProjectGroup.class);
	}

	public List<ProjectGroup> loadAllGroups(Set<String> allIds, SORT_KEY sortKey) {

		Query query = query(where("_id").in(allIds));

		switch (sortKey) {
		case alphabetic:
			query.sort().on("name", Order.ASCENDING);
			break;
		case duedate:
			// NOP: there is no concept of "due date" in groups => the GUI should never allow this sort key anyway (and if it does, we just ignore it)
			break;
		// TODO: sort by proximity: cf real full text search implementation
		}

		return mongoTemplate.find(query, ProjectGroup.class);
	}

	public void updateParticipantOfAllGroupsWith(UserSummary updatedSummary) {
		if (updatedSummary != null) {
			mongoTemplate.updateMulti(query(where("participants.user._id").is(updatedSummary.getUserName())),
					new Update().set("participants.$.user", updatedSummary), ProjectGroup.class);
		}
	}

	public void updateGroupData(ProjectGroup editedGroup) {

		if (editedGroup != null) {
			mongoTemplate.updateFirst(query(where("id").is(editedGroup.getId())),
					new Update().set("name", editedGroup.getName()).set("description", editedGroup.getDescription()).set("location", editedGroup.getLocation())
							.set("themes", editedGroup.getThemes()).set("tags", editedGroup.getTags()), ProjectGroup.class);
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

	public void addUserApplication(String groupId, UserSummary userSummary) {
		mongoTemplate.updateFirst(query(where("id").is(groupId)), new Update().addToSet("participants", new GroupParticipant(ROLE.member, userSummary, false)),
				ProjectGroup.class);

	}

	public void setUserApplicationAcceptedStatus(String groupId, String userId, boolean acceptedStatus) {
		mongoTemplate.updateFirst(query(where("id").is(groupId).and("participants.user._id").is(userId)),
				new Update().set("participants.$.accepted", acceptedStatus), ProjectGroup.class);
	}

	public void updateParticipantRole(String groupId, String userId, ROLE role) {
		mongoTemplate.updateFirst(query(where("id").is(groupId).and("participants.user._id").is(userId)), new Update().set("participants.$.role", role),
				ProjectGroup.class);
	}

	public void addProjectApplication(String groupId, ProjectSummary projectSummary, String applicationText) {
		mongoTemplate.updateFirst(query(where("id").is(groupId)),
				new Update().addToSet("projectParticipants", new GroupProjectParticipant(projectSummary, applicationText, false)), ProjectGroup.class);
	}

	public void removeProjectParticipant(final String groupId, final String projectId) {
		mongoTemplate.execute("projectGroup", new CollectionCallback<WriteResult>() {
			public WriteResult doInCollection(DBCollection collection) throws MongoException, DataAccessException {
				BasicDBObject queryDbo = new BasicDBObject("_id", groupId);
				BasicDBObject pullDbo = new BasicDBObject("$pull", new BasicDBObject("projectParticipants", new BasicDBObject("projet.projectId", projectId)));
				return collection.update(queryDbo, pullDbo);
			}
		});
	}

	public void setProjectApplicationAcceptedStatus(String groupId, String projectId, boolean accepted) {
		mongoTemplate.updateFirst(query(where("id").is(groupId).and("projectParticipants.projet.projectId").is(projectId)),
				new Update().set("projectParticipants.$.accepted", accepted), ProjectGroup.class);
	}

	public void updateProjectMainPhoto(String groupId, String projectId, Photo mainPhoto) {

		Query query = query(where("id").is(groupId).and("projectParticipants.projet.projectId").is(projectId));

		Update update;
		if (mainPhoto == null) {
			update = new Update().unset("projectParticipants.$.projet.mainPhoto");
		} else {
			update = new Update().set("projectParticipants.$.projet.mainPhoto", mainPhoto);
		}
		mongoTemplate.updateFirst(query, update, ProjectGroup.class);

	}

	public void launchCountGroupTagsJob() {
		mongoTemplate.mapReduce("projectGroup", "classpath:com/svend/dab/dao/mongo/mapreduce/countTagsMap.js",
				"classpath:com/svend/dab/dao/mongo/mapreduce/countTagsReduce.js", options().outputCollection("groupTagCount"), GroupTagCount.class);
	}

	public void updateProjectStatus(String groupId, String projectId, STATUS newStatus) {
		mongoTemplate.updateFirst(query(where("id").is(groupId).and("projectParticipants.projet.projectId").is(projectId)),
				new Update().set("projectParticipants.$.projet.status", newStatus), ProjectGroup.class);

	}

	public void addOnePhoto(String id, Photo newPhoto) {
		genericUpdateProject(id, new Update().addToSet("photoAlbum.photos", newPhoto));

	}

	public void removeOnePhoto(String id, Photo removed) {
		genericUpdateProject(id, new Update().pull("photoAlbum.photos", removed));
	}

	public void removeOnePhotoAndResetMainPhotoIndex(String id, Photo removed) {
		genericUpdateProject(id, new Update().pull("photoAlbum.photos", removed).set("photoAlbum.mainPhotoIndex", 0));
	}

	public void removeOnePhotoAndDecrementMainPhotoIndex(String id, Photo removed) {
		genericUpdateProject(id, new Update().pull("photoAlbum.photos", removed).inc("photoAlbum.mainPhotoIndex", -1));
	}

	public void updatePhotoCaption(String id, String s3Key, String photoCaption) {
		Query query = query(where("_id").is(id).and("photoAlbum.photos.normalPhotoLink.s3Key").is(s3Key));
		Update update = new Update().set("photoAlbum.photos.$.caption", photoCaption);
		mongoTemplate.updateFirst(query, update, ProjectGroup.class);
	}

	public void movePhotoToFirstPosition(String projectId, int mainPhotoIndex) {
		genericUpdateProject(projectId, new Update().set("photoAlbum.mainPhotoIndex", mainPhotoIndex));
	}

	// --------------------------------
	//

	protected void genericUpdateProject(String groupId, Update update) {
		Query query = query(where("_id").is(groupId));
		mongoTemplate.updateFirst(query, update, ProjectGroup.class);
	}

}