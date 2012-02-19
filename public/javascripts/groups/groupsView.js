$(document).ready(function() {
	new dabGroupsViewLib.GroupsViewController();
});

var dabGroupsViewLib = {

	// /////////////////////////////////////////////////////////
	// controller for the main forum widget
	GroupsViewController : function() {
		
		this.init = function() {
			var self = this;
			
			if (isCloseGroupLinkEffective) {
				new Confirm.AskAndProceed(this, "#groupToolBox", "#closeGroupLink", confirmCloseGroupText, null, self.afterUserConfirmsCloseGroup).init();
			} else {
				alert("todo: non effective close group...");
			}
			
			
		};
		
		
		
		this.afterUserConfirmsCloseGroup = function() {
			$("#hiddenCloseGroupForm #groupIdInput").val(visitedGroupId);
			$("#hiddenCloseGroupForm form").submit();
		};
		
		this.init();
	},
};





