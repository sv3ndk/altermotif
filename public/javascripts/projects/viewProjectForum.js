var projectViewForumCtrl;

$(document).ready(function() {
	projectViewForumCtrl = new dabProjectForumLib.ProjectViewForumController();
	projectViewForumCtrl.init();
});

var dabProjectForumLib = {

	// /////////////////////////////////////////////////////////
	// controller for the main forum widget
	ProjectViewForumController : function() {

		// ////////////////////////
		// members

		this.projectViewForumModel = new dabProjectForumLib.ProjectViewForumModel();
		this.createNewThreadDialogController = new dabProjectForumLib.CreateNewThreadDialogController(this.projectViewForumModel);

		// ////////////////////
		// public API
		this.init = function() {
			var self = this;

			this.projectViewForumModel.init();
			this.createNewThreadDialogController.init();

			// click on add thread
			$("#viewProjectForumAddThreadLink").click(function() {
				self.whenUserClicksOnAddThread(self);
			});

			ko.applyBindings(this.projectViewForumModel, $("#dynamicallyCreatedThreads")[0]);
		};

		// ////////////////////////////
		// internal functions

		this.whenThreadPostsAreReceived = function(self, listOfPosts) {
			projectViewForumModel.setListOfPosts(listOfPosts);
			projectViewForumModel.setThreadMode(false);
		};

		this.whenUserClicksOnAddThread = function(self) {
			self.createNewThreadDialogController.open();
		};

		
		
	},

	// /////////////////////////////////////////////////////////
	// controller for the dialog for adding a new forum thread
	CreateNewThreadDialogController : function(projectViewForumModel) {

		this.projectViewForumModel = projectViewForumModel;

		// ///////////////////////////
		// members

		this.htmlDialogDialog;

		this.init = function() {
			var self = this;

			this.htmlDialogDialog = $("#addNewThreadCreationPopup");
			this.htmlDialogDialog.dialog({
				autoOpen : false,
				width : 400,
				height : 170,
				"buttons" : [ {
					text : okLabelValue,
					click : function() {
						self.whenUserConfirmsThreadCreation();
					}
				}, {
					text : cancelLabelValue,
					click : function() {
						self.close();
					}
				} ]
			});

		};

		this.open = function() {
			$("#addNewThreadCreationPopup input").val("");
			$("#addNewThreadCreationPopup input.threadVisibility").attr("checked", "checked");
			this.htmlDialogDialog.dialog("open");
		};

		this.close = function() {
			this.htmlDialogDialog.dialog("close");
		};

		// ////////////////////////////
		// internal functions

		this.whenUserConfirmsThreadCreation = function() {
			var self = this;
			var createdThreadName = $("#addNewThreadCreationPopup input.threadName").val();
			var createdThreadIsPublic = $("#addNewThreadCreationPopup input.threadVisibility").attr("checked") == "checked";
			if (createdThreadName != undefined && createdThreadName != "") {
				$.post(addNewThread({
					projectId : projectId,
					threadTitle : createdThreadName,
					isThreadPublic : createdThreadIsPublic
				}), function(createdThread) {
					self.whenThreadCreatedResponseIsReceived(self, createdThread);
				});
			}
			this.close();
		};

		this.whenThreadCreatedResponseIsReceived = function(self, createdThread) {
			self.projectViewForumModel.addThread(createdThread);
		};

	},

	// /////////////////////////////////////////////////////////
	// main View Model
	ProjectViewForumModel : function() {

		this.listCreatedThread = ko.observableArray();

		// //////////////////////////////
		// public API
		
		
		this.init = function() {

			// pre-fills the KO model based on thread data present in HTML
			var self = this;
			$("#forumData").hide();
			_.each($("#forumData div"), function(htmlThread) { self.addThreadFromHtml(self, htmlThread);});
		}
		

		// add a thread, built with JSON data, according to format aligned with server-side definition of a thread
		this.addServerThread = function(serverThread) {
			if (serverThread != undefined && serverThread != "" && serverThread != "[]") {
				this.addThread(new dabProjectForumLib.ProjectThread(serverThread));
			}
		};
		
		this.addThread = function(thread) {
			this.listCreatedThread.push(thread);
		}

		this.afterAddThread = commonKOStuff.genericAfterAddElement;
		
		// //////////////////////////////
		// internal API
		this.addThreadFromHtml = function(self, htmlThread) {
			var thread = new dabProjectForumLib.ProjectThread();
			thread.id = $(htmlThread).find("span.threadId").text();
			thread.projectId = projectId;
			thread.isThreadPublic = $(htmlThread).find("span.threadIsPublic").text() == "true";
			thread.title = $(htmlThread).find("span.threadTitle").text();
			thread.creationDate = $(htmlThread).find("span.threadCreationDate").text();
			thread.numberOfPosts = $(htmlThread).find("span.numberOfPosts").text();
			self.addThread(thread);
		};
		
		
		
	},

	// simply data model for containing the dynamically created threads
	ProjectThread : function(serverThread) {
		if (serverThread != undefined) {
			this.id = serverThread.id;
			this.projectId = serverThread.projectId;
			this.isThreadPublic = serverThread.isThreadPublic;
			this.title = serverThread.title;
			this.creationDate = serverThread.creationDateStr;
			this.numberOfPosts = serverThread.numberOfPosts;
		}

	}
};