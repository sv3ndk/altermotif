///////////////////////////////////////
var projetSearchRoot;

$(document).ready(function() {
	projetSearchRoot = new ProjectSearchRoot();
	projetSearchRoot.init();
});


var ProjectSearchRoot = function() {
	
	// sub-controllers
	this.modeSwitcher = new ProjectModeSwitchController();
	this.simpleSearchController = new ProjectSearchSimpleSearchController();
	this.advancedSearchController = new ProjectSearchAdvancedSearchController();
	
	///////////////////////
	// public API
	
	this.init = function () {
		this.modeSwitcher.init();
		this.simpleSearchController.init();
		this.advancedSearchController.init();
	};
	
};




///////////////////////////////////
// Controller for switching mode


var ProjectModeSwitchController = function() {
	
	this.isSimpleMode = true;
	
	///////////////////////
	// public API
	
	this.init = function () {
		var self = this;
		$("#projectSearchSwitchToAdvancedModeLink").click(function(event) {
			self.switchMode(false);
		});

		$("#projectSearchSwitchToSimpleModeLink").click(function(event) {
			self.switchMode(true);
		});
		
	};
	
	this.switchMode = function (newMode) {
		this.isSimpleMode = newMode;
		if (this.isSimpleMode) {
			$("#projectSearch_modeAdvanced").hide(250);
			$("#projectSearch_modeSimple").show(250);
		} else {
			$("#projectSearch_modeSimple").hide(250);
			$("#projectSearch_modeAdvanced").show(250);
		}
	};
	
};



///////////////////////////////////
// Controller for the simple search mode
var ProjectSearchSimpleSearchController = function () {
	
	// "empty" URL of the search page (stil have to add parameters)
	this.searchPageLocation;
	
	this.init = function () {
		var self = this;
		
		this.searchPageLocation = $("#hiddenLinkToEmptySearch").attr("href");
		
		// click on a tag in the tag cloud
		$("#projectTagContainer").on("click", "a", function(event) { self.searchProjectByTags(self, $(event.target).text()); });
		
		// selection of a category:
		$("#projectCategorieListOfDropboxes").on("change", function(event) {self.searchProjectByTheme(self, $(event.target).val()); });
		
	};
	
	this.searchProjectByTags = function(self, clickedTag) {
		if (clickedTag != undefined && clickedTag != "") {
			window.location = self.searchPageLocation + "?r.tag=" + clickedTag;
		}
	};
	
	this.searchProjectByTheme = function(self, clickedThemeValue) {
		if (clickedThemeValue != undefined && clickedThemeValue != "") {
			window.location = self.searchPageLocation + "?r.themes=" + clickedThemeValue; 
		}
	};
}



///////////////////////////////////
// Controller for the  advanced search mode


var ProjectSearchAdvancedSearchController = function () {
	
	this.isGoButtonActive = ko.observable(false);
	
	///////////////
	// public API
	
	this.init = function () {
		
		self = this;
		
		// this is the init function defined in projectThemess.js
		initAddThemeLogic(undefined, function(newAllThemesValue) { self.updateAllThemesHiddenForm(self, newAllThemesValue); });
		
		ko.applyBindings(this, $("#projectSearch_modeAdvanced")[0]);
		
		$("#projectAdvancedSearchInputText").on("change", function() { self.updateGoButtonState(); });
		
	};
	
	//this is called back from projectThemes.js any time the list of chosen themes changes
	this.updateAllThemesHiddenForm = function(self, newAllThemesValue) {
		$("#hiddenAllThemesJson").val(JSON.stringify(newAllThemesValue));
		self.updateGoButtonState();
	};
	
	
	//////////////////
	// internal methods
	
	this.updateGoButtonState = function() {
		var currentInput = $("#projectAdvancedSearchInputText").val();
		if (currentInput == undefined || currentInput == "") {
			var currentChosenProjectThemes = $("#hiddenAllThemesJson").val();
			this.isGoButtonActive(! (currentChosenProjectThemes == undefined || currentChosenProjectThemes == ""  || currentChosenProjectThemes == "[]"));
		} else {
			this.isGoButtonActive(true);
		}
	};
	
}



