var groupsEditController;

$(document).ready(function() {
	groupsEditController = new dabGroupsEditLib.GroupsEditController();
});

var dabGroupsEditLib = {

	// /////////////////////////////////////////////////////////
	// controller for the main forum widget
	GroupsEditController : function() {
		
		this.inputMultiLocationsController;
		this.inputTagController;
		this.inputMultiThemesController;
		
		this.init = function() {
			var self = this;

			var initLocations = dabUtils.parseJsonStringIntoObject("#hiddenLocationsJson");
			this.inputMultiLocationsController = new dabInputMultiLocationsLib.InputMultiLocationsController($("#inputGroupLocations div"), initLocations);
			
			var initTags = dabUtils.parseJsonStringIntoObject("#hiddenAllTagsJson");
			this.inputTagController = new dabInputMultiTextLib.InputMultiTextController($("#inputTags div.inputMultiText"), initTags);

			var initThemes = dabUtils.parseJsonStringIntoObject("#hiddenThemesJson");
			this.inputMultiThemesController = new dabInputMultiThemesLib.InputMultiThemesController($("#inputGroupThemes div.inputMultiThemes"),
					allThemes, initThemes, function(newSelectedThemesValue) {
						self.updateAllThemesHiddenForm(newSelectedThemesValue);
					});

			
			$("#groupsEditAddLocation").click(function() {
				self.inputLocationController.showInput();
			});
			
			$("#startGroupButton").click(function(event){self.whenUserClickStartGroup()});
			
		};
		
		
		this.whenUserClickStartGroup = function() {
			
			$("#hiddenLocationsJson").val(this.inputMultiLocationsController.getAllLocationJson());
			$("#hiddenAllTagsJson").val(this.inputTagController.getTextJson());
			$("#hiddenThemesJson").val(this.inputMultiThemesController.getSelectedThemesJson());
			
			$(".groupsEditionContainer form").submit();
		};
		
		this.updateAllThemesHiddenForm = function(newSelectedThemesValue) {
			// NOP (only updated when the form is submitted)
		};
		
		this.init();
	},
	
}






