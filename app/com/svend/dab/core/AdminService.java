package com.svend.dab.core;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.svend.dab.core.beans.profile.UserProfile;
import com.svend.dab.core.beans.projects.Participation;
import com.svend.dab.core.beans.projects.Project;
import com.svend.dab.core.projects.IProjectFTSService;
import com.svend.dab.dao.mongo.IProjectDao;
import com.svend.dab.dao.mongo.IUserProfileDao;

/**
 * @author svend
 *
 */
@Service
public class AdminService {
	
	
	@Autowired
	IUserProfileDao userProfileDao;
	
	
	@Autowired
	IProjectDao projectDao;

	@Autowired
	private IProjectFTSService projectFTSService;
	
	/**
	 * this ugly method has been created when we added the "creation date" to the proejct summary => we updated all existing data like this (very dangerous method: loses udpates is other users are connected...) 
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
		projectFTSService.ensureIndexOnLocation();
		Set<String> allProjectIds = projectDao.getAllProjectIds();
		for (String id : allProjectIds) {
			projectFTSService.updateProjetIndex(id, true);
		}
	}
	
}
