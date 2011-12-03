/**
 * 
 */
package com.svend.dab.dao.mongo;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.svend.dab.core.beans.profile.Photo;
import com.svend.dab.core.beans.profile.UserSummary;
import com.svend.dab.core.beans.projects.Project;

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
	public void save(Project project) {
		mongoTemplate.save(project);
	}

	@Override
	public void updateProjectPDataLinksAndTags(String projectId, Project project) {
		Query query = query(where("_id").is(projectId));
		
		// for some reason, Mongo prefers list to set (and I happen to prefer set to lists...)
		List<String> links =new LinkedList<String>();
		links.addAll(project.getLinks());
		List<String> tags =new LinkedList<String>();
		tags.addAll(project.getTags());
		
		Update update = new Update().set("pdata", project.getPdata()).set("links", links).set("tags", tags);
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
	
	// --------------------------------
	//
	

	protected void genericUpdateProject(String projectId, Update update) {
		Query query = query(where("_id").is(projectId));
		mongoTemplate.updateFirst(query, update, Project.class);
	}
	
}
