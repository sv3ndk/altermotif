package com.svend.dab.dao.mongo;

import static org.springframework.data.mongodb.core.mapreduce.MapReduceOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.CollectionCallback;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.MongoException;
import com.mongodb.WriteResult;
import com.svend.dab.core.beans.PhotoAlbum;
import com.svend.dab.core.beans.groups.GroupSummary;
import com.svend.dab.core.beans.profile.Photo;
import com.svend.dab.core.beans.profile.UserSummary;
import com.svend.dab.core.beans.projects.Asset;
import com.svend.dab.core.beans.projects.Participant;
import com.svend.dab.core.beans.projects.Participant.ROLE;
import com.svend.dab.core.beans.projects.Project;
import com.svend.dab.core.beans.projects.Project.STATUS;
import com.svend.dab.core.beans.projects.ProjectOverview;
import com.svend.dab.core.beans.projects.SearchQuery;
import com.svend.dab.core.beans.projects.SearchQuery.SORT_KEY;
import com.svend.dab.core.beans.projects.SelectedTheme;
import com.svend.dab.core.beans.projects.TagCount;
import com.svend.dab.core.beans.projects.Task;
import com.svend.dab.core.dao.IProjectDao;

/**
 * @author Svend
 * 
 */
@Component
public class ProjectRepoImpl implements IProjectDao {

	@Autowired
	private MongoTemplate mongoTemplate;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.svend.dab.dao.mongo.IProjectRepo#updateProjectParticipation(java.lang.String, com.svend.dab.core.beans.profile.UserSummary)
	 */

	public void updateProjectParticipation(String projectId, UserSummary updatedSummary) {
		Query query = query(where("_id").is(projectId).and("participants.user._id").is(updatedSummary.getUserName()));
		mongoTemplate.updateFirst(query, new Update().set("participants.$.user", updatedSummary), Project.class);
	}

	public Project findOne(String projectId) {
		return mongoTemplate.findOne(query(where("_id").is(projectId)), Project.class);
	}

	public List<Project> loadAllProjects(Set<String> allIds, SORT_KEY sortkey) {

		Query query = query(where("_id").in(allIds));

		switch (sortkey) {
		case relevancy:
			query.sort().on("pdata.name", Order.ASCENDING);
			break;
		case duedate:
			query.sort().on("pdata.dueDate", Order.ASCENDING);
			break;
		// TODO: sort by proximity: cf real full text search implementation
		}

		List<Project> projects = mongoTemplate.find(query, Project.class);

		if (sortkey == SORT_KEY.duedate) {
			// projects with no due dates should be at the end, but appear at the beginnging => splitting into two lists
			List<Project> projectsWithADueDate = new LinkedList<Project>();
			List<Project> projectsWithoutADueDate = new LinkedList<Project>();

			for (Project project : projects) {
				if (project.getPdata().getDueDate() == null) {
					projectsWithoutADueDate.add(project);
				} else {
					projectsWithADueDate.add(project);
				}
			}

			projects.clear();
			projects.addAll(projectsWithADueDate);
			projects.addAll(projectsWithoutADueDate);

		}

		return projects;
	}

	public Set<String> getAllProjectIds() {
		Query query = query(where("_id").exists(true));
		query.fields().include("_id");
		List<Project> list = mongoTemplate.find(query, Project.class);
		Set<String> ids = new HashSet<String>();

		if (list != null) {
			for (Project profile : list) {
				ids.add(profile.getId());
			}
		}
		return ids;
	}

	public void save(Project project) {
		mongoTemplate.save(project);
	}

	public void updateProjectStatus(String projectId, STATUS newStatus) {
		genericUpdateProject(projectId, new Update().set("status", newStatus));
	}

	public List<ProjectOverview> searchProjects(SearchQuery request) {

		List<ProjectOverview> response = new LinkedList<ProjectOverview>();

		Criteria criteria = where("status").ne("cancelled");

		if (request.getTags() != null && !request.getTags().isEmpty()) {
			criteria.and("tags").all(request.getTags().toArray());
		}

		Query query = query(criteria);

		List<Project> projects = mongoTemplate.find(query, Project.class);
		if (projects != null) {
			for (Project project : projects) {
				response.add(new ProjectOverview(project));
			}
		}

		return response;
	}

	public void updateProjectPDataAndLinksAndTagsAndThemes(String projectId, Project project) {
		Query query = query(where("_id").is(projectId));

		// for some reason, Mongo prefers list to set (and I happen to prefer set to lists...)
		List<String> links = new LinkedList<String>();
		if (project.getLinks() != null) {
			links.addAll(project.getLinks());
		}

		List<String> tags = new LinkedList<String>();
		if (project.getTags() != null) {
			tags.addAll(project.getTags());
		}

		List<SelectedTheme> themes = new LinkedList<SelectedTheme>();
		if (project.getThemes() != null) {
			themes.addAll(project.getThemes());
		}

		Update update = new Update().set("pdata", project.getPdata()).set("links", links).set("tags", tags).set("themes", themes);
		mongoTemplate.updateFirst(query, update, Project.class);
	}
	
	/////////////////////
	// photos
	
	
	public void updatePhotoAlbum(String id, PhotoAlbum photoAlbum) {
		genericUpdateProject(id, new Update().set("photoAlbum", photoAlbum));
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

	public void updatePhotoCaption(String projectId, String s3PhotoKey, String photoCaption) {
		Query query = query(where("_id").is(projectId).and("photoAlbum.photos.normalPhotoLink.s3Key").is(s3PhotoKey));
		Update update = new Update().set("photoAlbum.photos.$.caption", photoCaption);
		mongoTemplate.updateFirst(query, update, Project.class);
	}

	public void movePhotoToFirstPosition(String projectId, int mainPhotoIndex) {
		genericUpdateProject(projectId, new Update().set("photoAlbum.mainPhotoIndex", mainPhotoIndex));
	}

	// /////////////////////////
	// project participants

	public void addOneParticipant(String projectId, Participant createdParticipant) {
		genericUpdateProject(projectId, new Update().addToSet("participants", createdParticipant));
	}

	public void updateParticipantList(String projectId, List<Participant> newPList) {
		genericUpdateProject(projectId, new Update().set("participants", newPList));
	}

	public void markParticipantAsAccepted(String projectId, String participantId) {
		Query query = query(where("_id").is(projectId).and("participants.user._id").is(participantId));
		Update update = new Update().set("participants.$.accepted", true);
		mongoTemplate.updateFirst(query, update, Project.class);
	}

	public void removeParticipant(final String projectId, final String userId) {
		// Spring data is currently missing one level of indirection for this,
		// db.project.update({'pdata.name':'eee'}, { $pull : {'participants': {'user._id' : 'testuser'}} });
		mongoTemplate.execute("project", new CollectionCallback<WriteResult>() {

			public WriteResult doInCollection(DBCollection collection) throws MongoException, DataAccessException {
				BasicDBObject queryDbo = new BasicDBObject("_id", projectId);
				BasicDBObject pullDbo = new BasicDBObject("$pull", new BasicDBObject("participants", new BasicDBObject("user._id", userId)));
				return collection.update(queryDbo, pullDbo);
			}
		});
	}

	public void updateParticipantRole(String projectId, String userId, ROLE role) {
		Query query = query(where("_id").is(projectId).and("participants.user._id").is(userId));
		Update update = new Update().set("participants.$.role", role);
		mongoTemplate.updateFirst(query, update, Project.class);
	}

	public Project loadProjectParticipants(String projectId) {
		Query query = query(where("_id").is(projectId));
		query.fields().include("participants");
		return mongoTemplate.findOne(query, Project.class);
	}

	public void updateOwnerShipProposed(String projectId, String userName, boolean ownershipProposed) {
		Query query = query(where("_id").is(projectId).and("participants.user._id").is(userName));
		Update update = new Update().set("participants.$.ownershipProposed", ownershipProposed);
		mongoTemplate.updateFirst(query, update, Project.class);
	}

	// ////////////////////////////////////////////////////////
	// tags

	public void launchCountProjectTagsJob() {
		mongoTemplate.mapReduce("project", "classpath:com/svend/dab/dao/mongo/mapreduce/countTagsMap.js",
				"classpath:com/svend/dab/dao/mongo/mapreduce/countTagsReduce.js", options().outputCollection("tagCount"), TagCount.class);
	}

	// //////////////////////////////////
	// tasks

	public void addOrUpdateProjectTasks(String projectId, Task newOrUpdatedTask) {
		// upsert this tasks: this just does nothing if the task already exists
		Query upsertQuery = query(where("_id").is(projectId).and("tasks._id").ne(newOrUpdatedTask.getId()));
		Update upsertUpdate = new Update().addToSet("tasks", newOrUpdatedTask);
		WriteResult upsertWriteResult = mongoTemplate.updateFirst(upsertQuery, upsertUpdate, Project.class);

		// updates the existing task (this is useless in case of insert, which is ok, the point is to make any update idempotent...)
		Query updateQuery = query(where("_id").is(projectId).and("tasks._id").is(newOrUpdatedTask.getId()));
		Update updateUpdate = new Update().set("tasks.$", newOrUpdatedTask);
		WriteResult updateWriteResult = mongoTemplate.updateFirst(updateQuery, updateUpdate, Project.class);

	}

	public void removeTaskFromProject(final String projectId, final String removedTasksId) {

		// db.project.update({'_id':'21bf4b86cbe943eda6ec9e305c840199'},{'$pull': {'tasks': {'_id':'8c1e701c-e1c5-48ff-a375-46480e33fd4c'}}})

		mongoTemplate.execute("project", new CollectionCallback<WriteResult>() {

			public WriteResult doInCollection(DBCollection collection) throws MongoException, DataAccessException {
				BasicDBObject queryDbo = new BasicDBObject("_id", projectId);
				BasicDBObject pullDbo = new BasicDBObject("$pull", new BasicDBObject("tasks", new BasicDBObject("_id", removedTasksId)));
				return collection.update(queryDbo, pullDbo);
			}
		});
	}

	public void addOrUpdateProjectAsset(String projectId, Asset newOrUpdatedAsset) {
		// upsert this tasks: this just does nothing if the asset already exists
		Query upsertQuery = query(where("_id").is(projectId).and("assets._id").ne(newOrUpdatedAsset.getId()));
		Update upsertUpdate = new Update().addToSet("assets", newOrUpdatedAsset);
		WriteResult upsertWriteResult = mongoTemplate.updateFirst(upsertQuery, upsertUpdate, Project.class);

		// updates the existing asset (this is useless in case of insert, which is ok, the point is to make any update idempotent...)
		Query updateQuery = query(where("_id").is(projectId).and("assets._id").is(newOrUpdatedAsset.getId()));
		Update updateUpdate = new Update().set("assets.$", newOrUpdatedAsset);
		WriteResult updateWriteResult = mongoTemplate.updateFirst(updateQuery, updateUpdate, Project.class);

	}

	public void removeAssetFromProject(final String projectId, final String removedAssetId) {
		mongoTemplate.execute("project", new CollectionCallback<WriteResult>() {
			public WriteResult doInCollection(DBCollection collection) throws MongoException, DataAccessException {
				BasicDBObject queryDbo = new BasicDBObject("_id", projectId);
				BasicDBObject pullDbo = new BasicDBObject("$pull", new BasicDBObject("assets", new BasicDBObject("_id", removedAssetId)));
				return collection.update(queryDbo, pullDbo);
			}
		});
	}

	public void addOneGroup(String projectId, GroupSummary groupSummary) {
		mongoTemplate.updateFirst(query(where("_id").is(projectId)), new Update().addToSet("groups", groupSummary), Project.class);
	}

	public void removeParticipationInGroup(final String projectId, final String groupId) {
		mongoTemplate.execute("project", new CollectionCallback<WriteResult>() {
			public WriteResult doInCollection(DBCollection collection) throws MongoException, DataAccessException {
				BasicDBObject queryDbo = new BasicDBObject("_id", projectId);
				BasicDBObject pullDbo = new BasicDBObject("$pull", new BasicDBObject("groups", new BasicDBObject("groupId", groupId)));
				return collection.update(queryDbo, pullDbo);
			}
		});
	}

	public void updateGroupSummaryOfAllProjectsPartOf(GroupSummary updatedSummary) {
		if (updatedSummary != null) {
			mongoTemplate.updateMulti(query(where("groups.groupId").is(updatedSummary.getGroupId())),
					new Update().set("groups.$.name", updatedSummary.getName()).set("groups.$.mainPhoto", updatedSummary.getMainPhoto()), Project.class);
		}
	}

	// --------------------------------
	//

	protected void genericUpdateProject(String projectId, Update update) {
		Query query = query(where("_id").is(projectId));
		mongoTemplate.updateFirst(query, update, Project.class);
	}

}
