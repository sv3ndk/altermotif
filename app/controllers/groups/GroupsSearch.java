package controllers.groups;

import web.utils.Utils;
import controllers.BeanProvider;
import controllers.DabLoggedController;

/**
 * @author svend
 *
 */
public class GroupsSearch extends DabLoggedController {

	public static void groupsSearch () {

		renderArgs.put("popularTags", BeanProvider.getGroupService().getPopularTags());

		Utils.addThemesToRenderArgs(getSessionWrapper(), renderArgs);
		Utils.addJsonThemesToRenderArgs(getSessionWrapper(), renderArgs, getSessionWrapper().getSelectedLg());

		render();
	}
	
}
