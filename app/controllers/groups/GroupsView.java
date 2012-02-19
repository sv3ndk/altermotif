package controllers.groups;

import models.altemotif.groups.GroupViewVisibility;

import com.svend.dab.core.beans.groups.GroupPep;
import com.svend.dab.core.beans.groups.ProjectGroup;

import controllers.Application;
import controllers.BeanProvider;
import controllers.DabLoggedController;

public class GroupsView extends DabLoggedController {
	
	
	
	public static void groupsView(String groupid) {
		
		ProjectGroup group = BeanProvider.getGroupService().loadGroupById(groupid, true);
		
		if (group == null) {
			Application.index();
		} else {
			
			renderArgs.put("groupViewVisibility", new  GroupViewVisibility(new GroupPep(group), getSessionWrapper().getLoggedInUserProfileId()));
			renderArgs.put("visitedGroup", group);
			
			render();
		}
		
		
	}

}
