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

		this.addThread = function(createdThread) {
			if (createdThread != undefined && createdThread != "" && createdThread != "[]") {
				this.listCreatedThread.push(new dabProjectForumLib.ProjectThread(createdThread));
			}
		};

		this.afterAddThread = commonKOStuff.genericAfterAddElement;
	},

	// simply data model for containing the dynamically created threads
	ProjectThread : function(serverThread) {
		this.id = serverThread.id;
		this.projectId = serverThread.projectId;
		this.isThreadPublic = serverThread.isThreadPublic;
		this.title = serverThread.title;
		this.creationDate = serverThread.creationDateStr;
		this.numberOfPosts = serverThread.numberOfPosts;

	}
};