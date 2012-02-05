package controllers.groups;

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

			renderArgs.put("visitedGroup", group);
			
			render();
		}
		
		
	}

}
