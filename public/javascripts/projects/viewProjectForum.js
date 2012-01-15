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
		this.clickedThreadId;

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

			// click on make private/make public/delete thread
			new Confirm.AskAndProceed(this, "#dynamicallyThreads", "span.dabLink.makeprivate", confirmMakePrivateText, this.recordClickThreadId,
					this.afterUserConfirmsMakeThreadPrivate).init();
			new Confirm.AskAndProceed(this, "#dynamicallyThreads", "span.dabLink.makepublic", confirmMakePublicText, this.recordClickThreadId,
					this.afterUserConfirmsMakeThreadPublic).init();
			new Confirm.AskAndProceed(this, "#dynamicallyThreads", "span.dabLink.deleteThread", confirmRemoveThreadtext, this.recordClickThreadId,
					this.afterUserConfirmsDeleteThread).init();

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

		this.recordClickThreadId = function(self, event) {
			self.clickedThreadId = $(event.target).parent().parent().find("span.hiddenThreadId").text();
		};

		this.afterUserConfirmsMakeThreadPrivate = function(self, event) {
			self.updateThreadVisibility(self, self.clickedThreadId, false);
		};

		this.afterUserConfirmsMakeThreadPublic = function(self, event) {
			self.updateThreadVisibility(self, self.clickedThreadId, true);
		};

		this.updateThreadVisibility = function(self, threadId, isPublic) {
			$.post(changeThreadVisibility({
				projectId : projectId,
				threadId : threadId,
				isThreadPublic : isPublic
			}), function(updatedThread) {
				self.projectViewForumModel.changeThreadVisibility(updatedThread.id, updatedThread.isThreadPublic);
			});
		};

		this.afterUserConfirmsDeleteThread = function(self, event) {
			$.post(deletedThread({
				projectId : projectId,
				threadId : self.clickedThreadId,
			}), function(response) {
				self.projectViewForumModel.removeThread(response.name);
			});
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
			self.projectViewForumModel.addServerThread(createdThread);
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
			_.each($("#forumData div"), function(htmlThread) {
				self.addThreadFromHtml(self, htmlThread);
			});
		}

		// add a thread, built with JSON data, according to format aligned with server-side definition of a thread
		this.addServerThread = function(serverThread) {
			if (serverThread != undefined && serverThread != "" && serverThread != "[]") {
				this.addThread(dabProjectForumLib.ProjectThreadFactory.buildFromServerThread(serverThread));
			}
		};

		this.addThread = function(thread) {
			this.listCreatedThread.push(thread);
		};

		this.removeThread = function(threadId) {
			var removedThread = _.find(this.listCreatedThread(), function(thread) {
				return thread.id == threadId;
			});
			if (removedThread != undefined) {
				this.listCreatedThread.remove(removedThread);
			}
		};

		this.changeThreadVisibility = function(threadId, isPublic) {
			var updatedTread = _.find(this.listCreatedThread(), function(thread) {
				return thread.id == threadId;
			});
			if (updatedTread != undefined) {
				updatedTread.isThreadPublic(isPublic);
			}
		};

		// animation callback when adding/removing a thread
		this.afterAddThread = commonKOStuff.genericAfterAddElement;
		this.beforeRemoveThread = commonKOStuff.genericBeforeRemoveElement;

		// //////////////////////////////
		// internal API
		this.addThreadFromHtml = function(self, htmlThread) {
			var threadId = $(htmlThread).find("span.threadId").text();
			var threadUrl = $(htmlThread).find("a.threadLink").attr("href");
			var isThreadPublic = $(htmlThread).find("span.threadIsPublic").text() == "true";
			var title = $(htmlThread).find("span.threadTitle").text();
			var creationDate = $(htmlThread).find("span.threadCreationDate").text();
			var numberOfPosts = $(htmlThread).find("span.numberOfPosts").text();
			var userMayUpdateVisibility = $(htmlThread).find("span.threadUserMayUpdateVisibility").text() == "true";
			var userMayDeleteThread = $(htmlThread).find("span.threadUserMayDeleteThread").text() == "true";

			self.addThread(new dabProjectForumLib.ProjectThread(threadId, threadUrl, projectId, isThreadPublic, title, creationDate, numberOfPosts,
					userMayUpdateVisibility, userMayDeleteThread));
		};

	},

	// simply data model for containing the dynamically created threads
	ProjectThread : function(id, url, projectId, isPublic, title, creationDate, numberOfPosts, userMayUpdateVisibility, userMayDeleteThread) {
		var self = this;
		this.id = id;
		this.projectId = projectId;
		this.url = url;
		this.isThreadPublic = ko.observable(isPublic);
		this.title = title;
		this.creationDate = creationDate;
		this.numberOfPosts = numberOfPosts;

		this.userMayUpdateVisibility = userMayUpdateVisibility;
		this.userMayDeleteThread = userMayDeleteThread;

		this.isMakePublicLinkVisible = ko.computed(function() {
			return self.userMayUpdateVisibility && !self.isThreadPublic();
		});

		this.isMakePrivateLinkVisible = ko.computed(function() {
			return self.userMayUpdateVisibility && self.isThreadPublic();
		});

		this.isAtLeastOneUpdateLinkIsVisible = ko.computed(function() {
			return self.userMayDeleteThread || self.isMakePublicLinkVisible() || self.isMakePrivateLinkVisible();
		});
	},

	ProjectThreadFactory : {
		buildFromServerThread : function(serverThread) {
			return new dabProjectForumLib.ProjectThread(serverThread.id, serverThread.threadUrl, serverThread.projectId, serverThread.isThreadPublic,
					serverThread.title, serverThread.creationDateStr, serverThread.numberOfPosts, serverThread.mayUserUpdateVisibility,
					serverThread.mayUserDeleteThisThread);
		}
	}
};