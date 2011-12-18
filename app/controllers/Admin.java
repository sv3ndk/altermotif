package controllers;

public class Admin extends DabLoggedController {

	public static void admin() {
		render();
	}

	public static void doUpdateCreationDateOfAllProjectSummaries() {
		BeanProvider.getAdminService().updateAllProjectsSummaries();
		admin();
	}

	public static void doLaunchCountTagsJob() {
		BeanProvider.getProjectDao().launchCountProjectTagsJob();
		admin();
	}

}
