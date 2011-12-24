// View model used to back the whole task table
var editTasksModel = {
	allTaskStatus : [],
	projectTasks : ko.observableArray(),
};

var clickedTaskId;
var removedUserId_FromTask;

// initialization (called from the main js file for this page)
function initEditTasks() {

	editTasksModel.allTaskStatus = taskStatusLabels;
	
	ko.applyBindings(editTasksModel);
	updateProjectTasksTable();
	
	// this is in usersPopupList.js
	initUsersPopupList(whenAnAssigneetIsChosen);
	
	initUserTaskAssignementMechanics();
}


// gathers data from server and updates task table accordingly
function updateProjectTasksTable() {
	
	$.post(getTaskList(
			{projectId: projectId}
			), 
			function(data) {
		
				$(data).each(function (index, element) {
					editTasksModel.projectTasks.push(new viewTasks(element));
				});
				
				$("#projectTaskTable").find(".dueDateInput").datepicker({
					changeMonth : true,
					changeYear : true,
					dateFormat : "dd/mm/yy",
					yearRange : '-0:+100',
					showAnim : "blind"
				});

			}
	);
}

///////////////////////////////////////////////
// assignment 

function initUserTaskAssignementMechanics() {
	
	// this is defined in simpleActions.js
	askAndAct_On("#projectTaskTable", "img.deleteImageLink", confirmRemoveUserFromTask, whenUserConfirmsRemoveUserFromTask);
	
	$("#projectTaskTable").on("click", ".addTaskAssigneeLink", function() {
		clickedTaskId = $(event.target).parent().parent().parent().find(".hiddenTaskId").text();
		openFilteredUsersPopupList(getListOfAssigneesForTask(clickedTaskId), choseTaskAssigneePopupTitle);
	});

}

function whenUserConfirmsRemoveUserFromTask(event) {
	clickedTaskId = $(event.target).parent().parent().parent().parent().find(".hiddenTaskId").text();
	removedUserId_FromTask = $(event.target).parent().find("span.taskUserName").text();
	setConfirmationFunction(onConfirmRemoveUserFromTasks);
}

function onConfirmRemoveUserFromTasks() {
	
	// updates the KO model: remove this user from the observable list
	$(editTasksModel.projectTasks()).each(function(index, element) {
		if (element.id == clickedTaskId) {
			var removedUser;
			$(element.assignees()).each(function(index, element) {
				if (element.userName == removedUserId_FromTask) {
					removedUser = element;
				}
			});
			element.assignees.remove(removedUser);
		}
	});
	
	closeConfirmationDialog();
}


// we have to transform the server-side tasks into obvervable ones
var viewTasks = function (serverSideTask) {
	this.id =  serverSideTask.id;
	this.name = serverSideTask.name;
	this.status = serverSideTask.status;
	this.dueDateStr = serverSideTask.dueDateStr;
	this.assignees = ko.observableArray(serverSideTask.assignees);
	
	// animation callback when removing a assignee from a task
	this.removeTaskAssigneeElement = function (elem) {
		if (elem.nodeType === 1) {
			$(elem).slideUp(function() { $(elem).remove(); }); 
			
			
		}
	};

	// animation callback when removing a assignee from a task
	this.addTaskAssigneeElement = function (elem) {
		if (elem.nodeType === 1) {
			$(elem).hide().slideDown(); 
		}
	};
	
}

//this is called back from the user popup list handler, after the user has chosen a contact (see usersPopupList.js)
function whenAnAssigneetIsChosen(addedUserName) {

	// updates the KO model: adds this user into the observable list, if he is not yet present
	$(editTasksModel.projectTasks()).each(function(index, element) {
		if (element.id == clickedTaskId) {
			var userAlreadyExists = false;

			$(element.assignees()).each(function(index, element) {
				if (element.userName == addedUserName) {
					userAlreadyExists = true;
				}
			});
			
			if (!userAlreadyExists) {
				element.assignees.push({
					userName: addedUserName
				});
			}
		}
	});
	
	closeFilteredUsersPopupList();
}



function getListOfAssigneesForTask(clickedTaskId) {
	var allUsernames = [];
	$(editTasksModel.projectTasks()).each(function(index, element) {
		if (element.id == clickedTaskId) {
			$(element.assignees()).each(function(index, element) {
				allUsernames.push(element.userName);				
			});
		}
	});
	
	return allUsernames;
}