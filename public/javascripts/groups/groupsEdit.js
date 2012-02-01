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






