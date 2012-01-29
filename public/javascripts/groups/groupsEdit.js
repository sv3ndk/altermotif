var groupsEditController;

$(document).ready(function() {
	groupsEditController = new dabGroupsEditLib.GroupsEditController();
});

var dabGroupsEditLib = {

	// /////////////////////////////////////////////////////////
	// controller for the main forum widget
	GroupsEditController : function() {
		
		this.inputLocationController;
		
		this.init = function() {
			var self = this;
			this.inputLocationController = new dabInputLocationLib.InputLocationController($("#editGroupLocation div"),
					0, 0, "", 
					this.whenUserCancelAddLocation, function(newRefLoc, newLat, newLong) {
				self.whenUserConfirmsAddLocation(newRefLoc, newLat, newLong)
			});
			
			
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






