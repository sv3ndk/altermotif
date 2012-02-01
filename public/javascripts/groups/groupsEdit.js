var groupsEditController;

$(document).ready(function() {
	groupsEditController = new dabGroupsEditLib.GroupsEditController();
});

var dabGroupsEditLib = {

	// /////////////////////////////////////////////////////////
	// controller for the main forum widget
	GroupsEditController : function() {
		
		this.inputMultiLocationsController;
		
		this.init = function() {
			var self = this;

			var allLocations = dabUtils.parseJsonStringIntoObject("#hiddenAllLocationJson");
			inputMultiLocationsController = new dabInputMultiLocationsLib.InputMultiLocationsController($("#inputGroupLocations div"), allLocations);
			
			var allTags = dabUtils.parseJsonStringIntoObject("#hiddenAllTagsJson");
			inputMultiTextController = new dabInputMultiTextLib.InputMultiTextController($("#inputTags div.inputMultiText"), allTags);

			
			
			$("#groupsEditAddLocation").click(function() {
				self.inputLocationController.showInput();
			});
			
		};
		
		
		this.whenUserCancelAddLocation = function() {
			
		};
		
		this.whenUserConfirmsAddLocation = function(newRefLoc, newLat, newLong) {
			
		};
		
		this.init();
	},
	
}






