$(document).ready(function() {
	new dabProjectResultsLib.ProjectResultsController();
});

var dabProjectResultsLib = {

	// /////////////////////////////////////////////////////////
	// controller for the main forum widget
	ProjectResultsController : function() {

		this.languageMapper = new dabLanguageLib.LanguageMapper();
		this.refreshModel = new dabSearchResultLib.RefreshSearchResultModel();
		
		this.filterController = new dabSearchResultLib.FilterController(this.refreshModel, this.languageMapper);
		this.sortByController = new dabSearchResultLib.SortByController(this.refreshModel);
		this.refreshPageController = new dabProjectResultsLib.ProjectRefreshPageController(this.refreshModel, this.languageMapper);

		this.init = function() {
			var self = this;
			
			this.languageMapper.init(allPossibleLanguages);
			this.filterController.init();
			$("#projectResultFilterLanguage").val(this.languageMapper.resolveLanguageOfCode(originalSearchRequestJson.lg));
			
			$("#projectResultFilterLanguage").autocomplete({
				source : self.languageMapper.allPossibleLanguagesList
			}
			);
			
			// click on "refresh result" button
			$("input.searchResultUpdateResultButton").click(function(event) {
				self.refreshPageController.refresh();
			});

			ko.applyBindings(this.refreshModel, $("#projectResultRefineQueryContainer")[0]);
		};
		
		this.init();
	},
	
	ProjectRefreshPageController : function(refreshModel, languageMapper) {
		
		this.refreshModel = refreshModel;
		this.languageMapper = languageMapper;
		
		this.refresh = function() {
			$("#hiddenRefreshResultsForm form input.term").val(originalSearchRequestJson.term);
			$("#hiddenRefreshResultsForm form input.tag").val(originalSearchRequestJson.tag);
			$("#hiddenRefreshResultsForm form input.allThemesJson").val( originalSearchRequestJson.themes);
			
			$("#hiddenRefreshResultsForm form input.sortkey").val(this.refreshModel.sortKey());
			
			$("#hiddenRefreshResultsForm form input.reflocation").val(this.refreshModel.refLocation());
			$("#hiddenRefreshResultsForm form input.reflatitude").val(this.refreshModel.refLatitude);
			$("#hiddenRefreshResultsForm form input.reflongitude").val(this.refreshModel.refLongitude);
			
			$("#hiddenRefreshResultsForm form input.filterByProximity").val(this.refreshModel.isFilteredByProximity());
			$("#hiddenRefreshResultsForm form input.filterByDate").val(this.refreshModel.isFilteredByDueDate());
			$("#hiddenRefreshResultsForm form input.filterByLanguage").val(this.refreshModel.isFilteredByLanguage());
			
			$("#hiddenRefreshResultsForm form input.filterByProximityMaxDistance").val(this.refreshModel.filterByProximityMaxDistance());
			$("#hiddenRefreshResultsForm form input.filterByDateMaxDate").val(this.refreshModel.filterByDueDateMaxDate());
			$("#hiddenRefreshResultsForm form input.filterByLanguageCode").val(this.languageMapper.resolveCodeOfLanguage($("#projectResultFilterLanguage").val()));
			
			$("#hiddenRefreshResultsForm form").submit();
		};
	},

};



