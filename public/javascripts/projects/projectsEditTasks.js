var editTasksCtrl;

$(document).ready(function() {
	editTasksCtrl = new EditTaskController();
	editTasksCtrl.init();
	editTasksCtrl.updateProjectTasksTable();
})

// ////////////////////////
// EditAssetController class
// ////////////////////////

function EditTaskController() {

	// ///////////
	// members
	this.koModel = new EditTaskViewModel();
	this.listOfRemovedTasksIds = [];
	this.clickedTaskId;
	this.userListPopup;

	// ///////////
	// public API

	this.init = function() {
		var self = this;

		// knockout bindings
		ko.applyBindings(this.koModel, $("#editProjectTasksContainer")[0]);

		// click on add task
		$("#addProjectTaskLink").click(function() {
			self.koModel.addEmptyTask();
		});

		// click on remove task
		new Confirm.AskAndProceed(this, "#editProjectTasksContainer", "img.deleteTaskLink", confirmRemoveProjectTaskText, this.onClickOnDeleteTask,
				this.afterUserConfirmsRemoveTask).init();

		// click "add assignee"
		this.userListPopup = new dabUserPopupLib.UserListPopup(this, $("#editProjectTasksContainer .usersPopupList"), choseTaskAssigneePopupTitle, this.afterUserSelectsAssignee);
		this.koModel.maxNumOfAssigneePerTask = this.userListPopup.countTotalNumberOfPopupUser();
		$("#editProjectTasksContainer").on("click", ".addTaskAssigneeLink", function(event) {
			self.clickedTaskId = $(event.target).parent().parent().find(".hiddenTaskId").text();
			var excludedUsernames = self.koModel.getTask(self.clickedTaskId).getListOfAssigneesNames();
			self.userListPopup.openFiltered(excludedUsernames);
		});
		
		// click on "remove assignee"
		$("#editProjectTasksContainer").on("click", ".removeTaskAssignee", function(event) {
			self.clickedTaskId = $(event.target).parent().parent().parent().parent().find(".hiddenTaskId").text();
			var clickedUserId = $(event.target).parent().find(".taskUserName").text();
			self.koModel.removeAssigneeFromTask(self.clickedTaskId, clickedUserId);
		});
		
	};

	// this is typically called from proejctEdit.js, when the user clicks on "submit", just before actually submitting data back to the server
	this.updateSubmittedTasks = function () {
		
		var removeNotSubmittedData = function () {
			_.each(this.assignees(), function(assignee) {
				delete assignee.isProfileActive;
				delete assignee.mainPhoto;
				delete assignee.location;
			});
			return this;
		};
		
		var submittedTasks = _(this.koModel.tasks()).chain()
			.filter(function (task) {return task.isModified})
			.invoke(removeNotSubmittedData)
			.map(function (task) { return new StaticResource(task.id, task.name(), task.status(), task.dueDateStr(), task.assignees(), task.description())})
			.value();
		
		$("#hiddenUpdatedTasksJson").val(JSON.stringify(submittedTasks));
		$("#hiddenRemovedTasksIdJson").val(JSON.stringify(this.listOfRemovedTasksIds));

	}
	
	// ///////////
	// internal methods

	this.onClickOnDeleteTask = function(self, event) {
		self.clickedTaskId = $(event.target).parent().parent().find(".hiddenTaskId").text();
	};

	this.afterUserConfirmsRemoveTask = function(self, event) {
		self.koModel.removeTask(self.clickedTaskId);
		self.listOfRemovedTasksIds.push(self.clickedTaskId);
	};

	this.afterUserSelectsAssignee = function(self, username) {
		self.koModel.addAssignee(self.clickedTaskId, username);
	};

	this.updateProjectTasksTable = function() {
		var self = this;

		if (typeof projectId != "undefined") {
			// in case of project creation, projectId is still undefined, and there is no point going to the server
			$.post(getProjectTasksList({
				projectId : projectId
			}), function (listOfTasksJson) {
				if (listOfTasksJson != undefined) {
					_.each(listOfTasksJson, function(task) {
						self.koModel.addStaticTask(task, false);
					});
				}
			});
		}
	};

}

// //////////////////////////////////
// EditAssetViewModel controller
// KO root ModelView instance for this page
// //////////////////////////////////

function EditTaskViewModel() {

	// ///////////////////////
	// members
	this.tasks = ko.observableArray();
	this.lastCreatedTaskId = 0;
	this.maxNumOfAssigneePerTask;

	// ///////////////////////
	// public API

	this.addEmptyTask = function() {
		this.addStaticTask(new StaticResource(this.getNextCreatedTaskIndex(), "", "todo", "", []), true);
	};

	this.addStaticTask = function(staticTask, isNew) {
		this.addTask(new ViewProjectResource(staticTask, isNew, this.maxNumOfAssigneePerTask, "default task name").init());
	};

	this.addTask = function(dynamicTask) {
		this.tasks.push(dynamicTask);
		dabUtils.makeInputDatePicker("#editProjectTasksContainer input.dueDateInput", '-0:+100');
	};

	this.removeTask = function(taskId) {
		this.tasks.remove(this.getTask(taskId));
	};

	this.addAssignee = function(taskId, username) {
		this.getTask(taskId).addAssignee(username);
	};

	this.removeAssigneeFromTask = function(taskId, username) {
		this.getTask(taskId).removeAssignee(username);
	};

	this.getTask = function(taskId) {
		return _.find(this.tasks(), function(task) {
			return task.id == taskId
		});
	};

	// /////////////////////////
	// internal methods

	this.getNextCreatedTaskIndex = function() {
		this.lastCreatedTaskId++;
		return "new" + this.lastCreatedTaskId;
	};

}