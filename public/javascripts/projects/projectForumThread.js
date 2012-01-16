var projectForumThreadCtrl;

$(document).ready(function() {
	projectForumThreadCtrl = new dabProjectForumThreadLib.ForumThreadController();
	projectForumThreadCtrl.init();
});

var dabProjectForumThreadLib = {

	// ////////////////////////////////////////
	// CONTROLLERS
	// ////////////////////////////////////////

	ForumThreadController : function() {

		this.threadModel = new dabProjectForumThreadLib.ForumThreadModel();
		this.moveThreadPopupController = new dabProjectForumThreadLib.MoveThreadPopupController();
		this.clickedPostId;

		// ////////////////////////
		// public API
		this.init = function() {
			var self = this;

			this.threadModel.init();
			this.moveThreadPopupController.init();
			ko.applyBindings(this.threadModel, $("#forumPosts")[0]);

			// click on "post new comment"
			$("#forumThreadPostButton").click(function(event) {
				self.whenUserClicksPostComment(self, event);
			});

			// click on "delete post"
			new Confirm.AskAndProceed(this, "#forumPosts", "input.deleteThreadButton", confirmDeleteForumThreadText, this.recordClickPostId,
					this.afterUserConfirmsDeletePost).init();
			
			// click on "move post"
			$("#forumPosts").on("click", "input.moveThreadButton", function(event) {
				self.recordClickPostId(self, event);
				self.moveThreadPopupController.openDialog(self.clickedPostId, function() {self.updateDisplayedPosts();});
			});
		};

		// refresh the list of displayed posts
		this.updateDisplayedPosts = function() {
			var currentlyDisplayedPostIds = JSON.stringify(this.threadModel.getAllPostIds());
			var self = this;

			$.post(getNewAndDeletedPosts({
				threadId : threadId,
				currentlyKnownIds : currentlyDisplayedPostIds
			}), function(updatedData) {
				self.threadModel.removePosts(updatedData.deletedPostIds);
				self.threadModel.addPosts(updatedData.newPosts);
			});
		};

		// ////////////////////////
		// internal API
		this.whenUserClicksPostComment = function(self, event) {
			var postContent = $("#forumThreadPostText").val();
			if (postContent != undefined && postContent != "") {
				$.post(postNewComment({
					threadId : threadId,
					postContent : encodeURI(postContent)
				}), function(unusedData) {
					$("#forumThreadPostText").val("");
					self.updateDisplayedPosts(self);
				});
			}
		};

		// internal version of updateDisplayedPosts (with a specified self)
		this.updateDisplayedPosts_self = function(self) {
			self.updateDisplayedPosts();
		};

		this.recordClickPostId = function(self, event) {
			self.clickedPostId = $(event.target).parent().find(".hiddenPostId").text();
		};

		this.afterUserConfirmsDeletePost = function(self, event) {
			$.post(deletePost({
				threadId : threadId,
				postId : self.clickedPostId
			}), function(unusedData) {
				self.updateDisplayedPosts(self);
			});
		};

	},

	MoveThreadPopupController : function() {
		
		this.clickedPostId;
		this.whenServerConfirms;

		this.init = function() {
			var self = this;
			$("#projectForumThreadMoveThreadPopup").dialog({
				autoOpen : false,
				width : 550,
				"buttons" : [ {
					text : okLabelValue,
					click : function(event) {
						self.onConfirmMoveThread(self, event);
					}
				}, {
					text : cancelLabelValue,
					click : function() {
						self.closeDialog();
					}
				} ]
			});
		};

		this.openDialog = function(postId, callbackFunc) {
			this.clickedPostId = postId;
			this.whenServerConfirms = callbackFunc;
			$("#projectForumThreadMoveThreadPopup").dialog("open");
		};

		this.closeDialog = function() {
			$("#projectForumThreadMoveThreadPopup").dialog("close");
		};

		this.onConfirmMoveThread = function(self, event) {
			$.post(movePost({
				originalThreadId: threadId,
				targetThreadId : $("#projectForumThreadMoveThreadPopup select").val(),
				postId : self.clickedPostId
			}), function(unusedData) {
				self.closeDialog();
				self.whenServerConfirms();
			});
		};
	},

	// ////////////////////////////////////////
	// DATA MODEL
	// ////////////////////////////////////////

	ForumThreadModel : function() {

		// KO observable list of ThreadPost (see below)
		this.allPosts = ko.observableArray();

		// /////////////////////
		// public API

		this.init = function() {
			var self = this;
			$("#forumThreadData").hide();
			_.each($("#forumThreadData .onePost"), function(domElement) {
				self.addPostFromHtml(self, domElement);
			});
		};

		this.getAllPostIds = function() {
			return _.map(this.allPosts(), function(onePost) {
				return onePost.id
			});
		};

		this.addPost = function(threadPost) {
			this.allPosts.push(threadPost);
		};

		this.addPostInBeginning = function(threadPost) {
			this.allPosts.unshift(threadPost);
		};

		this.removePosts = function(listOfRemoveIds) {

			var self = this;

			_.each(listOfRemoveIds, function(threadId) {
				var removedThread = _.find(self.allPosts(), function(oneThread) {
					return oneThread.id == threadId;
				});
				if (removedThread != undefined) {
					self.allPosts.remove(removedThread);
				}
			});
		};

		this.addPosts = function(listOfServerPosts) {

			var self = this;
			_.each(listOfServerPosts, function(onePost) {

				var authorPhotoLink;
				if (onePost.author.mainPhoto.thumbLink == undefined) {
					authorPhotoLink = "/public/images/defaultProfileThumb.jpg";
				} else {
					authorPhotoLink = onePost.author.mainPhoto.thumbLink.url;
				}

				self.addPostInBeginning(new dabProjectForumThreadLib.ThreadPost(onePost.id, onePost.creationDateStr, onePost.content, onePost.author.userName,
						onePost.authorProfilLink, authorPhotoLink, onePost.author.profileActive, onePost.userMayDelete, onePost.userMayMove));
			});
		};

		this.beforeRemovePost = commonKOStuff.genericBeforeRemoveElement;

		this.afterAddPost = commonKOStuff.genericAfterAddElement;

		// //////////////////////////////
		// internal API
		this.addPostFromHtml = function(self, domElement) {
			var id = $(domElement).find("span.postId").text();
			var creationDate = $(domElement).find("span.postCreationDate").text();
			var content = $(domElement).find("span.postContent").text();
			var authorId = $(domElement).find("span.postAuthorId").text();
			var authorProfileLink = $(domElement).find("a.profileLink").attr("href");
			var authorPhotoUrl = $(domElement).find("span.postAuthorPhotoUrl").text();
			var authorIsActive = $(domElement).find("span.postAuthorIsActive").text() == "true";

			var userMayDelete = $(domElement).find("span.visitorMayDelete").text() == "true";
			var userMayMove = $(domElement).find("span.visitorMayMove").text() == "true";

			if (authorPhotoUrl == undefined || authorPhotoUrl == "") {
				authorPhotoUrl = "/public/images/defaultProfileThumb.jpg";
			}

			self.addPost(new dabProjectForumThreadLib.ThreadPost(id, creationDate, content, authorId, authorProfileLink, authorPhotoUrl, authorIsActive,
					userMayDelete, userMayMove));
		};

	},

	ThreadPost : function(id, creationDate, content, authorId, authorProfileLink, authorPhotoUrl, authorIsActive, userMayDelete, userMayMove) {
		this.id = id;
		this.creationDate = creationDate;
		this.content = content;

		this.authorId = authorId;
		this.authorProfileLink = authorProfileLink;
		this.authorPhotoUrl = authorPhotoUrl;
		this.authorIsActive = authorIsActive;

		this.userMayDelete = userMayDelete;
		this.userMayMove = userMayMove;
	},

};