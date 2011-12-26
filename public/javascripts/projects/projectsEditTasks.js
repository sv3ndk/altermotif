var clickedTaskId;
var removedUserId_FromTask;
var lastCreatedTaskId = 0;
var listOfRemovedTasksIds = [];

// initialization (called from the main js file for this page)
function initEditTasks() {

	// putting in the model the translated data 
	editTasksModel.allTaskStatus = taskStatusLabels;
	
	// preparing knockout modelView
	ko.applyBindings(editTasksModel);
	updateProjectTasksTable();
	
	initAddRemoveTaskMechanics();
	initUserTaskAssignementMechanics();
	
	// this is in usersPopupList.js
	initUsersPopupList(whenAnAssigneetIsChosen);
}

////////////////////////////////////////
// building the view model


//View model used to back the whole task table
var editTasksModel = {
	allTaskStatus : [],
	projectTasks : ko.observableArray()
};


// gathers data from server and updates task table accordingly
function updateProjectTasksTable() {
	if (typeof projectId != "undefined") {
		// in case of project creation, projectId is still undefined, and there is no point going to the server
		$.post(getTaskList({projectId: projectId}), whenUpdatedTasksAreReceivedFromServer);
	}
}

function whenUpdatedTasksAreReceivedFromServer(data) {
	$(data).each(function (index, serverSideTask) {
		editTasksModel.projectTasks.push(new viewTasks(serverSideTask, false));
	});
	
	// this is present in dab.js
	makeInputDatePicker("#projectTaskTable input.dueDateInput", '-0:+100');
}


function determineIfAddAssigneeLinkIsVisibleForThoseAssignees(assigneesArray) {
	var numberOfProjectMemebers = countTotalNumberOfPopupUser();
	
	if (numberOfProjectMemebers < 1) {
		return false; 
	} else {
		if (assigneesArray == undefined) {
			return true; 
		} else {
			return assigneesArray.length < numberOfProjectMemebers; 
		}
	}	
}



//Observable version of the task, will necessary binding related to Knockout
var viewTasks = function (serverSideTask, isNew) {
	this.id =  serverSideTask.id;
	this.name = serverSideTask.name;
	this.status = ko.observable(serverSideTask.status);
	this.dueDateStr = ko.observable(serverSideTask.dueDateStr);
	this.isAddAssigneeVisible = ko.observable(determineIfAddAssigneeLinkIsVisibleForThoseAssignees(serverSideTask.assignees));
	this.assignees = ko.observableArray(serverSideTask.assignees);

	// mechanism to flag any new or updated task (so we know what to submit back to the server)
	this.isModified = isNew;
	this.modificationDetector = ko.computed(function() {
		return {
			updatedTask: this,
			// we do not care of the value of this sensor itself, we just need it to depend on all updatable fields so an event is fired for any update
			sensor: this.status() + this.dueDateStr() +  this.assignees().length
		};
	}, this);
	this.modificationDetector.subscribe(function(updatedDetector) {
		updatedDetector.updatedTask.isModified = true;
	});
	
	// animation callback when removing a assignee from a task
	this.beforeRemoveAssignee = genericBeforeRemoveElement;

	// animation callback when removing a assignee from a task
	this.afterAddAssignee = genericAfterAddElement;
}

// "static" version of the task, as transmitted to and from server in JSON format (i.e. this is pure static data)
var staticTask = function (id, name, status, dueDateStr, assignees) {
	this.id = id;
	this.name = name;
	this.status = status;
	this.dueDateStr = dueDateStr;
	this.assignees = assignees;
}


function getNextCreatedTaskIndex() {
	lastCreatedTaskId ++;
	return "new" + lastCreatedTaskId; 
}


function genericBeforeRemoveElement (elem) {
	if (elem.nodeType === 1) {
		$(elem).slideUp(function() { $(elem).remove(); }); 
	}
}


function genericAfterAddElement(elem) {
	if (elem.nodeType === 1) {
		$(elem).hide().slideDown(); 
	}
}

///////////////////////////////////////////////
// assignment of this task to one or more users

function initUserTaskAssignementMechanics() {
	
	// this is defined in simpleActions.js
	askAndAct_On("#projectTaskTable", "img.removeUserLink", confirmRemoveUserFromTask, beforeUserConfirmsRemoveUserFromTask);
	
	$("#projectTaskTable").on("click", ".addTaskAssigneeLink", function() {
		clickedTaskId = $(event.target).parent().parent().find(".hiddenTaskId").text();
		openFilteredUsersPopupList(getListOfAssigneesForTask(clickedTaskId), choseTaskAssigneePopupTitle);
	});

}

function beforeUserConfirmsRemoveUserFromTask(event) {
	clickedTaskId = $(event.target).parent().parent().parent().parent().find(".hiddenTaskId").text();
	removedUserId_FromTask = $(event.target).parent().find("span.taskUserName").text();
	setConfirmationFunction(afterUserConfirmsRemoveUserFromTask);
}

function afterUserConfirmsRemoveUserFromTask() {
	
	// updates the KO model: remove this user from the observable list
	$(editTasksModel.projectTasks()).each(function(index, task) {
		if (task.id == clickedTaskId) {
			var removedUser;
			$(task.assignees()).each(function(index, assignee) {
				if (assignee.userName == removedUserId_FromTask) {
					removedUser = assignee;
				}
			});
			task.assignees.remove(removedUser);
			task.isAddAssigneeVisible(determineIfAddAssigneeLinkIsVisibleForThoseAssignees(task.assignees()));
		}
	});
	
	closeConfirmationDialog();
}




//this is called back from the user popup list handler, after the user has chosen a contact (see usersPopupList.js)
function whenAnAssigneetIsChosen(addedUserName) {

	// updates the KO model: adds this user into the observable list, if he is not yet present
	$(editTasksModel.projectTasks()).each(function(index, task) {
		if (task.id == clickedTaskId) {
			var userAlreadyExists = false;

			$(task.assignees()).each(function(index, assignee) {
				if (assignee.userName == addedUserName) {
					userAlreadyExists = true;
				}
			});
			
			if (!userAlreadyExists) {
				task.assignees.push({
					userName: addedUserName
				});
				task.isAddAssigneeVisible(determineIfAddAssigneeLinkIsVisibleForThoseAssignees(task.assignees()));
			}
		}
	});
	
	closeFilteredUsersPopupList();
}



function getListOfAssigneesForTask(clickedTaskId) {
	var allUsernames = [];
	$(editTasksModel.projectTasks()).each(function(index, task) {
		if (task.id == clickedTaskId) {
			$(task.assignees()).each(function(index, task) {
				allUsernames.push(task.userName);				
			});
		}
	});
	return allUsernames;
}


///////////////////////////////////////
// add/remove task

function initAddRemoveTaskMechanics() {
	
	// this is defined in simpleActions.js
	askAndAct_On("#projectTaskTable", "img.deleteTaskLink", confirmRemoveProjectTaskText, beforeUserConfirmsRemoveTaskFromProject);
	
	$("#addProjectTaskLink").click(function() {
		$("#addProjectTaskPopup input.inputDescription").val(""); 
		$("#addProjectTaskPopup input.inputDate").val(""); 
		$("#addProjectTaskPopup").dialog("open");
	});

	$("#addProjectTaskPopup").dialog({
		autoOpen : false,
		width: 400,
		"buttons" : [ {
			text : okLabelValue,
			click : function(event) {
				
				var taskName = $("#addProjectTaskPopup input.inputDescription").val(); 
				var taskDate = $("#addProjectTaskPopup input.inputDate").val(); 
				
				if (taskName != "" && taskName != undefined) {
					sTask = new staticTask(getNextCreatedTaskIndex(), taskName, "todo", taskDate, []);
					editTasksModel.projectTasks.push(new viewTasks(sTask, true));
					
					$(this).dialog("close");
					makeInputDatePicker("#projectTaskTable input.dueDateInput", '-0:+100');
				}
			}
		},

		{
			text : cancelLabelValue,
			click : function() {
				$(this).dialog("close");
			}
		}
		]
	});

	// this is present in dab.js
	makeInputDatePicker("#addProjectTaskPopup input.inputDate", '-0:+100');
}


function beforeUserConfirmsRemoveTaskFromProject() {
	clickedTaskId = $(event.target).parent().parent().find(".hiddenTaskId").text();
	setConfirmationFunction(afterUserConfirmsRemoveTaskFromProject);
}


function afterUserConfirmsRemoveTaskFromProject() {

	var removedTask;
	$(editTasksModel.projectTasks()).each(function(index, task) {
		if (task.id == clickedTaskId) {
			removedTask = task;
		}
	});
	
	editTasksModel.projectTasks.remove(removedTask);
	listOfRemovedTasksIds.push(clickedTaskId);
	
	closeConfirmationDialog();
}


//////////////////////////////////////////////////
// this is called by the listener on the submit button on the edition form, just before actually submitting the form

function updateSubmittedTasks() {
	
	var submittedTasks = [];
	$(editTasksModel.projectTasks()).each(function(index, task) {
		if (task.isModified) {
			$(task.assignees()).each(function(index, assignee) {
				// we have some issues unmarshalling those on server side, and we do not use them anyway...
				delete assignee.isProfileActive;
				delete assignee.mainPhoto;
				delete assignee.location;
			});
			
			submittedTasks.push(new staticTask(task.id, task.name, task.status(), task.dueDateStr(), task.assignees()));
		}
	});
	
	$("#hiddenUpdatedTasksJson").val(JSON.stringify(submittedTasks));
	$("#hiddenRemovedTasksIdJson").val(JSON.stringify(listOfRemovedTasksIds));
	
}