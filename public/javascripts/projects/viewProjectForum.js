var projectViewForumCtrl;

$(document).ready(function() {
	projectViewForumCtrl = new ProjectViewForumController();
	projectViewForumCtrl.init();
});



var ProjectViewForumController = function() {
	
	//////////////////////////
	// members
	
	this.createNewThreadDialog;
	this.projectViewForumModel = new ProjectViewForumModel();
	
	
	//////////////////////
	// public API
	this.init = function() {
		var self = this;
		
		// click on add thread
		$("#viewProjectForumAddThreadLink").click(function() {self.whenUserClicksOnAddThread(self);});
		this.initCreateNewThreadDialog();
		
		// click on a thread name (=> showing the posts of this thread)
		$("#threadContainer").click(function(event) {self.whenUserClicksOnAddThreadName(self, $(event.target).next().val());});
	};
	
	this.whenUserClicksOnAddThreadName = function(self, clickedThreadId) {
		$.post(getAllPosts({threadId : clickedThreadId}), function (listOfPosts) { self.whenThreadPostsAreReceived(self, listOfPostsJson);} );
	};
	
	this.whenThreadPostsAreReceived = function (self, listOfPosts) {
		projectViewForumModel.setListOfPosts(listOfPosts);
		projectViewForumModel.setThreadMode(false);
	};
	
	this.whenUserClicksOnAddThread = function(self) {
		self.createNewThreadDialog.dialog("open");
	};
	
	this.whenUserConfirmsThreadCreation = function() {
		$("#addNewThreadHiddenFormProjectId").val(projectId);
		$("#addNewThreadHiddenFormThreadTitle").val($("#addNewThreadCreationPopup input").val());
		$("#addNewThreadHiddenForm form").submit();
	};
	
	this.initCreateNewThreadDialog = function(){

		this.createNewThreadDialog = ${"#addNewThreadCreationPopup"};
	
		var self = this;
		this.createNewThreadDialog.dialog({
			autoOpen : false,
			width: 400,
			"buttons" : [ 
			             {
			            	 text : okLabelValue,
			            	 click : whenUserConfirmsThreadCreation
			             },
			             {
			            	 text : cancelLabelValue,
			            	 click : function () {
			            		 this.createNewThreadDialog.dialog("close");
			            	 }
			             }
			             ]
		});
		
	};
};



var ProjectViewForumModel = function () {
	
	this.isThreadMode = ko.observable(false);
	this.listOfPosts = ko.observableArray();
	
	////////////////////////////////
	// public API
	this.setThreadMode = function (newMode) {
		this.isThreadMode(newMode);
	};
	
	this.setListOfPosts = function (listOfPosts) {
		// TODO
	};
	
};







