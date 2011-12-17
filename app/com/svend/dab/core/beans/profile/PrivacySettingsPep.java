package com.svend.dab.core.beans.profile;

import com.svend.dab.core.beans.projects.Participant;
import com.svend.dab.core.beans.projects.Participation;
import com.svend.dab.core.beans.projects.Project;
import com.svend.dab.core.projects.IProjectService;

/**
 * security Policy enforcement point for actions on the Privacy settings of a {@link UserProfile} 
 * 
 * @author svend
 *
 */
public class PrivacySettingsPep {

	private final UserProfile profile;
	private final IProjectService projectService;

	public PrivacySettingsPep(UserProfile profile, IProjectService projectService) {
		super();
		this.profile = profile;
		this.projectService = projectService;
	}

	
	public boolean isAllowedToUpdateProfileActiveStatus() {
		if (profile == null) {
			return false;
		}
		
		if (!profile.getPrivacySettings().isProfileActive()) {
			// if a user is inactive, he may always get back to active
			return true;
		} else {
			// otherwise, he may only desactivate if he is not the owner of a project 
			if ( profile.isOwnerOfAtLeastOneProject()) {
				return false;
			}

			// we also have to prevent this user to desactivate his profile if he has received a proposal for ownership
			for (Participation participation :profile.getProjects()) {
				Project project = projectService.loadProject(participation.getProjectSummary().getProjectId(), false);
				if (project != null) {
					Participant participant = project.getParticipant(profile.getUsername());
					if (participant != null && participant.isOwnershipProposed()) {
						return false;
					}
				}
			}
			
			
			return true;
			
		}
		
	}
	
}
