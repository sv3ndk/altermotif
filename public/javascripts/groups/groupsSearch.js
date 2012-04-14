$(document).ready(function() {
	new groupsSearchLib.GroupsSearchController();
});

var groupsSearchLib = {

	GroupsSearchController : function() {
		this.modeSwitcher = new dualModeSwitcherLib.ModeSwitchController("#groupSearchSwitchToSimpleModeLink", "#groupSearchSwitchToAdvancedModeLink", 
				"#groupSearch_modeSimple", "#groupSearch_modeAdvanced", "", "#groupAvancedSearchInputText");
		
		var searchResultBaseUrl = $("#hiddenLinkToEmptyGroupSearchResultPage").attr("href"); 
		this.simpleSearchController = new dabSearchLib.SimpleSearchController(searchResultBaseUrl);
		
		this.GroupSearchAdvancedSearchController = new groupsSearchLib.GroupSearchAdvancedSearchController();

	},

	// /////////////////////////////////
	// Controller for the advanced search mode
	
	GroupSearchAdvancedSearchController: function() {
	
		this.inputMultiThemesController;
	
		// /////////////
		// public API
	
		this.init = function() {
	
			self = this;
	
			this.inputMultiThemesController = new dabInputMultiThemesLib.InputMultiThemesController($("#inputSearchGroupThemes div.inputMultiThemes"),
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


};