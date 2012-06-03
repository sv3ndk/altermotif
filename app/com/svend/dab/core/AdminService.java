package com.svend.dab.core;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.svend.dab.core.beans.profile.UserProfile;
import com.svend.dab.core.beans.projects.Participation;
import com.svend.dab.core.beans.projects.Project;
import com.svend.dab.core.dao.IGroupDao;
import com.svend.dab.core.dao.IGroupIndexDao;
import com.svend.dab.core.dao.IProjectDao;
import com.svend.dab.core.dao.IProjectIndexDao;
import com.svend.dab.core.dao.IUserProfileDao;

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
	private IProjectIndexDao projectIndexDao;

	@Autowired
	private IGroupIndexDao groupIndexDao;


	/**
	 * this ugly method has been created when we added the "creation date" to the project summary => we updated all existing data like this (very dangerous
	 * method: loses udpates is other users are connected...)
	 */
	public void updateAllProjectsSummaries() {
		Set<String> allUserIds = userProfileDao.getAllUsernames();
		for (String username : allUserIds) {
			UserProfile userProfile = userProfileDao.retrieveUserProfileById(username);
			for (Participation participation : userProfile.getProjects()) {
				Project project = projectDao.findOne(participation.getProjectSummary().getProjectId());
				participation.getProjectSummary().setCreationDate(project.getPdata().getCreationDate());
				participation.getProjectSummary().setMainPhoto(project.getPhotoAlbum().getMainPhoto().buildCopyWithThumbOnly());
			}
			userProfileDao.save(userProfile);
		}
		
	}

	public void indexAllProjects() {
		Set<String> allProjectIds = projectDao.getAllProjectIds();
		for (String id : allProjectIds) {
			projectIndexDao.updateIndex(id, true);
		}
	}

	public void indexAllGroups() {
		Set<String> allGroupIds = groupDao.getAllGroupsIds();
		for (String id : allGroupIds) {
			
			groupIndexDao.updateIndex(id, true);
		}
	}

	public void encryptUserPasswords() {
		Set<String> allUserIds = userProfileDao.getAllUsernames();
		for (String username : allUserIds) {
			UserProfile userProfile = userProfileDao.retrieveUserProfileById(username);
			
			String oldPass = userProfile.getPdata().getPassword();
			if (oldPass == null) {
				oldPass= "";
			}
			
			userProfile.getPdata().updatePassword(oldPass);
			userProfile.getPdata().setPassword(null);
			userProfileDao.save(userProfile);
		}
	}

}
