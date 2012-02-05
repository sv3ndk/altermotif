package com.svend.dab.dao.mongo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.svend.dab.core.beans.groups.ProjectGroup;
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
		// TODO Auto-generated method stub
		return mongoTemplate.findById(groupId, ProjectGroup.class);
	}

}
