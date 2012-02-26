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
	
	public static void doIndexAllProjects() {
		BeanProvider.getAdminService().indexAllProjects();
		admin();
	}

	public static void doTestEmail() {
		BeanProvider.getSocialService().sendEmail("svend.vanderveken@gmail.com", null, "notification from Altermotif!", "test email from Altermotif");
		admin();
	}

}
