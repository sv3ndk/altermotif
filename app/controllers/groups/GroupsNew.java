/**
 * 
 */
package controllers.groups;

import web.utils.Utils;
import controllers.DabLoggedController;

/**
 * @author svend
 *
 */
public class GroupsNew extends DabLoggedController {

	public static void groupsNew() {
		
		Utils.addProjectThemesToRenderArgs(getSessionWrapper(), renderArgs);

		render();
	}
	
	
	public static void doCreateGroup() {
		
	}
	
	
}
