$(document).ready(function() {
	new dabProjectSearchLib.ProjectSearchRoot();
});

var dabProjectSearchLib =  {
		
	
	ProjectSearchRoot: function() {
		this.modeSwitcher = new dualModeSwitcherLib.ModeSwitchController("#projectSearchSwitchToSimpleModeLink", "#projectSearchSwitchToAdvancedModeLink", 
				"#projectSearch_modeSimple", "#projectSearch_modeAdvanced");
		
		var searchResultBaseUrl = $("#hiddenLinkToEmptyProjectSearchResultPage").attr("href"); 
		this.simpleSearchController = new dabSearchLib.SimpleSearchController(searchResultBaseUrl);
		
		this.advancedSearchController = new dabProjectSearchLib.ProjectSearchAdvancedSearchController();
	},
	
	
	
	// /////////////////////////////////
	// Controller for the advanced search mode
	
	ProjectSearchAdvancedSearchController: function() {
	
		this.inputMultiThemesController;
	
		// /////////////
		// public API
	
		this.init = function() {
	
			self = this;
	
			this.inputMultiThemesController = new dabInputMultiThemesLib.InputMultiThemesController($("#inputSearchProjectsThemes div.inputMultiThemes"),
					allThemes, null, function(newSelectedThemesValue) {
						self.updateAllThemesHiddenForm(newSelectedThemesValue);
					});
		};
	
		// this is called back from projectThemes.js any time the list of chosen themes changes
		this.updateAllThemesHiddenForm = function(newSelectedThemesValue) {
			if (newSelectedThemesValue == null) {
				$("#hiddenAllThemesJson").val(JSON.stringify([]));
			}  else {
				$("#hiddenAllThemesJson").val(JSON.stringify(newSelectedThemesValue));
			}
		};
	
		this.init();
	
	},

}
