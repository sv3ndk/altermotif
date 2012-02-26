$(document).ready(function() {
	new dabGroupsEditLib.GroupsEditController();
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

			// page "new group" only
			$("#startGroupButton").click(function(event){self.whenUserClickStartGroup()});
			
			// page "edit group" only
			$("#editGroupButton").click(function(event){self.whenUserClickConfirmEditGroup()});
			$("#cancelEditGroupButton").click(function(event){self.whenUserClickCancelEditGroup()});
			
		};
		
		
		/////////////////////////////////
		// public API
		
		this.whenUserClickStartGroup = function() {
			this.submitEditionForm();
		};
		
		this.whenUserClickConfirmEditGroup = function() {
			this.submitEditionForm();
		};
		
		
		this.whenUserClickCancelEditGroup = function() {
			window.location.href = $("#hiddenNavigateToProfileHome").attr("href");
		};
		
		this.updateAllThemesHiddenForm = function(newSelectedThemesValue) {
			// NOP (only updated when the form is submitted)
		};
		
		///////////////////////
		// internal logic
		
		this.submitEditionForm = function () {
			$("#hiddenLocationsJson").val(this.inputMultiLocationsController.getAllLocationJson());
			$("#hiddenAllTagsJson").val(this.inputTagController.getTextJson());
			$("#hiddenThemesJson").val(this.inputMultiThemesController.getSelectedThemesJson());
			$(".groupsEditionContainer form").submit();
		};
		
		this.init();
	},
	
};






