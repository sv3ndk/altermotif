package com.svend.dab.core;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.svend.dab.core.beans.PhotoAlbum;
import com.svend.dab.core.beans.profile.UserProfile;
import com.svend.dab.core.beans.projects.Participation;
import com.svend.dab.core.beans.projects.Project;
import com.svend.dab.core.dao.IGroupDao;
import com.svend.dab.core.dao.IProjectDao;
import com.svend.dab.core.dao.IUserProfileDao;
import com.svend.dab.core.groups.IGroupFtsService;
import com.svend.dab.core.projects.IProjectFtsService;
import com.svend.dab.core.projects.IProjectService;

/**
 * @author svend
 * 
 */
@Service
public class AdminService {

	@Autowired
	private IUserProfileDao userProfileDao;

	@Autowired
	private IProjectDao projectDao;

	@Autowired
	private IGroupDao groupDao;
	
	@Autowired
	private IProjectService profileService;

	@Autowired
	private IProjectFtsService projectFtsService;

	@Autowired
	private IGroupFtsService groupFtsService;

	
	@Autowired
	private MongoTemplate mongoTemplate;

	/**
	 * this ugly method has been created when we added the "creation date" to the proejct summary => we updated all existing data like this (very dangerous
	 * method: loses udpates is other users are connected...)
	 */
	public void updateAllProjectsSummaries() {
		Set<String> allUserIds = userProfileDao.getAllUsernames();
		for (String username : allUserIds) {
			UserProfile userProfile = userProfileDao.retrieveUserProfileById(username);
			for (Participation participation : userProfile.getProjects()) {
				Project project = projectDao.findOne(participation.getProjectSummary().getProjectId());
				participation.getProjectSummary().setCreationDate(project.getPdata().getCreationDate());
			}
			userProfileDao.save(userProfile);
		}
	}

	public void indexAllProjects() {
		projectFtsService.ensureIndexOnLocation();
		Set<String> allProjectIds = projectDao.getAllProjectIds();
		for (String id : allProjectIds) {
			projectFtsService.updateProjetIndex(id, true);
		}
	}

	public void indexAllGroups() {
		groupFtsService.ensureIndexOnLocation();
		Set<String> allGroupIds = groupDao.getAllGroupsIds();
		for (String id : allGroupIds) {
			groupFtsService.updateGroupIndex(id, true);
		}
	}

	public void copyPhotosToAlbumPhotos() {
		
		Set<String> allProjectIds = projectDao.getAllProjectIds();
		for (String projectId : allProjectIds) {
			Project project = profileService.loadProject(projectId, false);
			
			if (project.getPhotos() != null) {
				PhotoAlbum photoAlbum = project.getPhotoAlbum();
				photoAlbum.setPhotos(project.getPhotos());
				
				projectDao.updatePhotoAlbum(projectId, photoAlbum);
				
				// Not in DAO: this "photos" is deprecated => not re-usable logic
				Query query = query(where("_id").is(projectId));
				mongoTemplate.updateFirst(query, new Update().unset("photos"), Project.class);
			}

		}
		
		Set<String> allUserIds = userProfileDao.getAllUsernames();
		for (String username : allUserIds) {
			UserProfile profile = userProfileDao.retrieveUserProfileById(username);
			
			if (profile.getPhotos() != null) {
				PhotoAlbum photoAlbum = profile.getPhotoAlbum();
				photoAlbum.setPhotos(profile.getPhotos());
				
				userProfileDao.updatePhotoAlbum(username, photoAlbum);
	
				// Not in DAO: this "photos" is deprecated => not re-usable logic
				Query query = query(where("username").is(username));
				mongoTemplate.updateFirst(query, new Update().unset("photos"), UserProfile.class);
			}

		}
		
	}

}
