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
		Utils.addProjectJsonThemesToRenderArgs(getSessionWrapper(), renderArgs, getSessionWrapper().getSelectedLg());

		render();
	}
	
	
	public static void doCreateGroup() {
		
	}
	
	
}
