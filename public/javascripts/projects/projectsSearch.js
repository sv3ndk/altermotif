///////////////////////////////////////

$(document).ready(function() {
	new dabProjectSearchLib.ProjectSearchRoot();
});

var dabProjectSearchLib =  {
		
	
	ProjectSearchRoot: function() {
		// sub-controllers
		this.modeSwitcher = new dabProjectSearchLib.ProjectModeSwitchController();
		this.simpleSearchController = new dabProjectSearchLib.ProjectSearchSimpleSearchController();
		this.advancedSearchController = new dabProjectSearchLib.ProjectSearchAdvancedSearchController();
	},
	
	// /////////////////////////////////
	// Controller for switching mode
	
	ProjectModeSwitchController: function() {
	
		this.isSimpleMode = true;
	
		// /////////////////////
		// public API
	
		this.init = function() {
			var self = this;
			$("#projectSearchSwitchToAdvancedModeLink").click(function(event) {
				self.switchMode(false);
			});
	
			$("#projectSearchSwitchToSimpleModeLink").click(function(event) {
				self.switchMode(true);
			});
	
		};
	
		this.switchMode = function(newMode) {
			this.isSimpleMode = newMode;
			if (this.isSimpleMode) {
				$("#projectSearch_modeAdvanced").hide(250);
				$("#projectSearch_modeSimple").show(250);
			} else {
				$("#projectSearch_modeSimple").hide(250);
				$("#projectSearch_modeAdvanced").show(250);
			}
		};
		
		this.init();
	
	},
	
	// /////////////////////////////////
	// Controller for the simple search mode
	ProjectSearchSimpleSearchController: function() {
	
		// "empty" URL of the search page (stil have to add parameters)
		this.searchPageLocation;
	
		this.init = function() {
			var self = this;
	
			this.searchPageLocation = $("#hiddenLinkToEmptySearch").attr("href");
	
			// click on a tag in the tag cloud
			$("#projectTagContainer").on("click", "a", function(event) {
				self.searchProjectByTags(self, $(event.target).text());
			});
	
			// selection of a category:
			$("#projectCategorieListOfDropboxes").on("change", function(event) {
				self.searchProjectByTheme(self, $(event.target).val());
			});
	
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
		
		this.init();
	},
	
	// /////////////////////////////////
	// Controller for the advanced search mode
	
	ProjectSearchAdvancedSearchController: function() {
	
		this.isGoButtonActive = ko.observable(false);
		this.inputMultiThemesController;
	
		// /////////////
		// public API
	
		this.init = function() {
	
			self = this;
	
			this.inputMultiThemesController = new dabInputMultiThemesLib.InputMultiThemesController($("#inputSearchProjectsThemes div.inputMultiThemes"),
					allThemes, function(newSelectedThemesValue) {
						self.updateAllThemesHiddenForm(newSelectedThemesValue);
					});
	
			ko.applyBindings(this, $("#searchLine")[0]);
	
			$("#projectAdvancedSearchInputText").on("change", function() {
				self.updateGoButtonState();
			});
	
		};
	
		// this is called back from projectThemes.js any time the list of chosen themes changes
		this.updateAllThemesHiddenForm = function(newSelectedThemesValue) {
			if (newSelectedThemesValue == null) {
				$("#hiddenAllThemesJson").val(JSON.stringify([]));
			}  else {
				$("#hiddenAllThemesJson").val(JSON.stringify(newSelectedThemesValue));
			}
			self.updateGoButtonState();
		};
	
		// ////////////////
		// internal methods
	
		this.updateGoButtonState = function() {
			var currentInput = $("#projectAdvancedSearchInputText").val();
			if (currentInput == undefined || currentInput == "") {
				var currentChosenProjectThemes = $("#hiddenAllThemesJson").val();
				this.isGoButtonActive(!(currentChosenProjectThemes == undefined || currentChosenProjectThemes == "" || currentChosenProjectThemes == "[]"));
			} else {
				this.isGoButtonActive(true);
			}
		};
	
		this.init();
	
	},

}
