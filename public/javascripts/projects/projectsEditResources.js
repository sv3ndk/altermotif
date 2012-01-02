//////////////////////////
// ViewProjectResource constructor
// Observable version of a project resource (either a task or an asset)
// this contains all necessary binding related to Knockout
//////////////////////////

function ViewProjectResource(staticResource, isNew, maxNumAssignees, defaultName) {

	// ////////////////
	// members
	this.id = staticResource.id;
	this.name = ko.observable(staticResource.name);
	this.status = ko.observable(staticResource.status);
	this.dueDateStr = ko.observable(staticResource.dueDateStr);
	this.isAddAssigneeVisible = ko.observable(true);
	this.assignees = ko.observableArray(staticResource.assignees);
	this.description = ko.observable(staticResource.description);

	// this defines if the "add assignee" link is visible
	this.maxNumberOfAssignees = maxNumAssignees;
	this.defaultResourceName = defaultName;

	this.isModified = isNew;

	// ////////////////////////
	// public API

	this.init = function() {
		var self = this;

		// mechanism to prevent any empty resource name
		this.name.subscribe(function(updatedName) {
			if (updatedName == undefined || updatedName.length == 0) {
				self.name(self.defaultResourceName);
			}
		});

		if (this.name() == undefined || this.name().length == 0) {
			this.name(self.defaultResourceName);
		}

		// mechanism to flag any new or updated task (so we know what to submit back to the server)
		this.modificationDetector.subscribe(function(updatedDetector) {
			updatedDetector.updatedTask.isModified = true;
		});

		return this;
	}

	this.addAssignee = function(assigneeUserName) {
		var existingAssignee = this.getAssigneeByUserName(assigneeUserName);

		if (existingAssignee == undefined) {
			this.assignees.push({
				userName : assigneeUserName
			});
			this.isAddAssigneeVisible(this.mayAddMoreAssignee());
		}
	};

	this.mayAddMoreAssignee = function() {
		if (this.maxNumberOfAssignees < 1) {
			return false;
		} else {
			return this.assignees().length < this.maxNumberOfAssignees;
		}
	};

	this.getListOfAssigneesNames = function(clickedTaskId) {
		var allUsernames = [];
		_.each(this.assignees(), function(assignee, key) {
			allUsernames.push(assignee.userName);
		});
		return allUsernames;
	};

	this.getAssigneeByUserName = function(username) {
		return _.find(this.assignees(), function(assignee) {
			return assignee.userName == username
		});
	}

	// /////////////////////
	// internal methods

	this.modificationDetector = ko.computed(function() {
		return {
			updatedTask : this,
			// we do not care of the value of this sensor itself, we just need it to depend on all updatable fields so an event is fired for any update
			sensor : this.status() + this.dueDateStr() + this.assignees().length + this.description()
		};
	}, this);

	// animation callback when removing a assignee from a task
	this.beforeRemoveAssignee = commonKOStuff.genericBeforeRemoveElement;

	// animation callback when removing a assignee from a task
	this.afterAddAssignee = commonKOStuff.genericAfterAddElement;
}

// ////////////////////////////////////////
// StaticResource constructor
// static version of a project resource, as transmitted to and from server in JSON format (i.e. this is pure static data)
// ////////////////////////////////////////
function StaticResource(id, name, status, dueDateStr, assignees, description) {
	this.id = id;
	this.name = name;
	this.status = status;
	this.dueDateStr = dueDateStr;
	this.assignees = assignees;
	this.description = description;
}

// ////////////////////////////////////////
// common Knockout stuff
// ////////////////////////////////////////

var commonKOStuff = {

	genericBeforeRemoveElement : function(elem) {
		if (elem.nodeType === 1) {
			$(elem).slideUp(function() {
				$(elem).remove();
			});
		}
	},

	genericAfterAddElement : function t(elem) {
		if (elem.nodeType === 1) {
			$(elem).hide().slideDown();
		}
	}
}
