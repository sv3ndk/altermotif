package com.svend.dab.dao.mongo;

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
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import static org.springframework.data.mongodb.core.mapreduce.MapReduceOptions.options;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.MongoException;
import com.mongodb.WriteResult;
import com.svend.dab.core.beans.profile.Photo;
import com.svend.dab.core.beans.profile.UserProfile;
import com.svend.dab.core.beans.profile.UserSummary;
import com.svend.dab.core.beans.projects.Participant;
import com.svend.dab.core.beans.projects.ProjectOverview;
import com.svend.dab.core.beans.projects.ProjectSearchRequest;
import com.svend.dab.core.beans.projects.TagCount;
import com.svend.dab.core.beans.projects.Participant.ROLE;
import com.svend.dab.core.beans.projects.Project;
import com.svend.dab.core.beans.projects.Project.STATUS;
import com.svend.dab.core.beans.projects.SelectedTheme;
import com.svend.dab.core.beans.projects.Task;

/**
 * @author Svend
 *
 */
@Component
public class ProjectRepoImpl implements IProjectDao {
	
	@Autowired
	private MongoTemplate mongoTemplate;

	/* (non-Javadoc)
	 * @see com.svend.dab.dao.mongo.IProjectRepo#updateProjectParticipation(java.lang.String, com.svend.dab.core.beans.profile.UserSummary)
	 */
	@Override
	public void updateProjectParticipation(String projectId, UserSummary updatedSummary) {
		Query query = query(where("_id").is(projectId).and("participants.user._id").is(updatedSummary.getUserName()));
		mongoTemplate.updateFirst(query, new Update().set("participants.$.user", updatedSummary), Project.class);
	}

	@Override
	public Project findOne(String projectId) {
		return mongoTemplate.findOne(query(where("_id").is(projectId)), Project.class);
	}
	
	
	@Override
	public List<Project> loadAllProjects(Set<String> allIds) {
		return mongoTemplate.find(query(where("_id").in(allIds)), Project.class);
	}

	@Override
	public Set<String> getAllProjectIds() {
		Query query = query(where("_id").exists(true));
		query.fields().include("_id");
		List<Project> list =  mongoTemplate.find(query, Project.class);
		Set<String > ids = new HashSet<String>();
		
		if (list != null) {
			for (Project profile : list) {
				ids.add(profile.getId());
			}
		}
		return ids;
	}

	

	@Override
	public void save(Project project) {
		mongoTemplate.save(project);
	}
	
	@Override
	public void updateProjectStatus(String projectId, STATUS newStatus) {
		genericUpdateProject(projectId, new Update().set("status", newStatus));
	}
	
	
	@Override
	public List<ProjectOverview> searchProjects(ProjectSearchRequest request) {

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


	@Override
	public void updateProjectPDataAndLinksAndTagsAndThemes(String projectId, Project project) {
		Query query = query(where("_id").is(projectId));
		
		// for some reason, Mongo prefers list to set (and I happen to prefer set to lists...)
		List<String> links =new LinkedList<String>();
		if (project.getLinks() != null) {
			links.addAll(project.getLinks());
		}
		
		List<String> tags =new LinkedList<String>();
		if (project.getTags() != null) {
			tags.addAll(project.getTags());
		}
		
		List<SelectedTheme> themes =new LinkedList<SelectedTheme>();
		if (project.getThemes() != null) {
			themes.addAll(project.getThemes());
		}
		
		Update update = new Update().set("pdata", project.getPdata()).set("links", links).set("tags", tags).set("themes", themes);
		mongoTemplate.updateFirst(query, update, Project.class);
	}
	
	
	
	

	@Override
	public void addOnePhoto(String id, Photo newPhoto) {
		genericUpdateProject(id, new Update().addToSet("photos", newPhoto));
	}
	
	
	@Override
	public void removeOnePhoto(String id, Photo removed) {
		genericUpdateProject(id, new Update(). pull("photos", removed));
	}
	
	@Override
	public void removeOnePhotoAndResetMainPhotoIndex(String id, Photo removed) {
		genericUpdateProject(id, new Update(). pull("photos", removed).set("mainPhotoIndex", 0));
	}
	
	@Override
	public void removeOnePhotoAndDecrementMainPhotoIndex(String id, Photo removed) {
		genericUpdateProject(id, new Update(). pull("photos", removed).inc("mainPhotoIndex", -1));
	}
	
	
	@Override
	public void updatePhotoCaption(String projectId, String s3PhotoKey, String photoCaption) {
		Query query = query(where("_id").is(projectId).and("photos.normalPhotoLink.s3Key").is(s3PhotoKey));
		Update update = new Update().set("photos.$.caption", photoCaption);
		mongoTemplate.updateFirst(query, update, Project.class);
	}

	@Override
	public void movePhotoToFirstPosition(String projectId, int mainPhotoIndex) {
		genericUpdateProject(projectId, new Update().set("mainPhotoIndex", mainPhotoIndex));
	}
	
	///////////////////////////
	// project participants

	
	@Override
	public void addOneParticipant(String projectId, Participant createdParticipant) {
		genericUpdateProject(projectId, new Update().addToSet("participants", createdParticipant));
	}
	
	@Override
	public void updateParticipantList(String projectId, List<Participant> newPList) {
		genericUpdateProject(projectId, new Update().set("participants", newPList));
	}

	@Override
	public void markParticipantAsAccepted(String projectId, String participantId) {
		Query query = query(where("_id").is(projectId).and("participants.user._id").is(participantId));
		Update update = new Update().set("participants.$.accepted", true);
		mongoTemplate.updateFirst(query, update, Project.class);
	}
	
	@Override
	public void removeParticipant(final String projectId, final String userId) {
		// Spring data is currently missing one level of indirection for this, 
		// db.project.update({'pdata.name':'eee'}, { $pull : {'participants': {'user._id' : 'testuser'}} }); 
		mongoTemplate.execute("project", new CollectionCallback<WriteResult>() {
			@Override
			public WriteResult doInCollection(DBCollection collection) throws MongoException, DataAccessException {
				BasicDBObject queryDbo = new BasicDBObject("_id", projectId);
				BasicDBObject pullDbo = new BasicDBObject("$pull", new BasicDBObject("participants", new BasicDBObject("user._id", userId)));
				return collection.update(queryDbo, pullDbo);
			}
		});
	}
	
	
	@Override
	public void updateParticipantRole(String projectId, String userId, ROLE role) {
		Query query = query(where("_id").is(projectId).and("participants.user._id").is(userId));
		Update update = new Update().set("participants.$.role", role);
		mongoTemplate.updateFirst(query, update, Project.class);
	}

	
	@Override
	public Project loadProjectParticipants(String projectId) {
		Query query = query(where("_id").is(projectId));
		query.fields().include("participants");
		return mongoTemplate.findOne(query, Project.class);
	}
	
	
	@Override
	public void updateOwnerShipProposed(String projectId, String userName, boolean ownershipProposed) {
		Query query = query(where("_id").is(projectId).and("participants.user._id").is(userName));
		Update update = new Update().set("participants.$.ownershipProposed", ownershipProposed);
		mongoTemplate.updateFirst(query, update, Project.class);
	}
	
	//////////////////////////////////////////////////////////
	// tags
	
	@Override
	public void launchCountProjectTagsJob() {
		mongoTemplate.mapReduce("project", "classpath:com/svend/dab/dao/mongo/mapreduce/countTagsMap.js", "classpath:com/svend/dab/dao/mongo/mapreduce/countTagsReduce.js", options().outputCollection("tagCount"), TagCount.class);
	}

	////////////////////////////////////
	// tasks
	
	@Override
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
	
	@Override
	public void removeTaskFromProject(final String projectId, final String removedTasksId) {
	
		// db.project.update({'_id':'21bf4b86cbe943eda6ec9e305c840199'},{'$pull': {'tasks': {'_id':'8c1e701c-e1c5-48ff-a375-46480e33fd4c'}}})
		
		mongoTemplate.execute("project", new CollectionCallback<WriteResult>() {
			@Override
			public WriteResult doInCollection(DBCollection collection) throws MongoException, DataAccessException {
				BasicDBObject queryDbo = new BasicDBObject("_id", projectId);
				BasicDBObject pullDbo = new BasicDBObject("$pull", new BasicDBObject("tasks", new BasicDBObject("_id", removedTasksId)));
				return collection.update(queryDbo, pullDbo);
			}
		});

	}
	
	
	// --------------------------------
	//

	protected void genericUpdateProject(String projectId, Update update) {
		Query query = query(where("_id").is(projectId));
		mongoTemplate.updateFirst(query, update, Project.class);
	}



}
