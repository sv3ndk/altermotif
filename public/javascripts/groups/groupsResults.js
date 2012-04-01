$(document).ready(function() {
	new dabGroupResultsLib.GroupResultsController();
});

var dabGroupResultsLib = {
		

	// /////////////////////////////////////////////////////////
	// controller for the main forum widget
	GroupResultsController : function() {
		
		this.refreshModel = new dabSearchResultLib.RefreshSearchResultModel();
		this.filterController = new dabSearchResultLib.FilterController(this.refreshModel, null);
		this.sortByController = new dabSearchResultLib.SortByController(this.refreshModel);
		
		this.refreshPageController = new dabGroupResultsLib.GroupRefreshPageController(this.refreshModel);

		
		this.init = function() {
			var self = this;
			
			this.filterController.init();
			
			// click on "refresh result" button
			$("input.searchResultUpdateResultButton").click(function(event) {
				self.refreshPageController.refresh();
			});

			ko.applyBindings(this.refreshModel, $("#groupResultRefineQueryContainer")[0]);
		};
		
		
		this.init();

	},
	
	GroupRefreshPageController : function(refreshModel) {
		
		this.refreshModel = refreshModel;
		
		this.refresh = function() {
			$("#hiddenRefreshResultsForm form input.term").val(originalSearchRequestJson.term);
			$("#hiddenRefreshResultsForm form input.tag").val(originalSearchRequestJson.tag);
			$("#hiddenRefreshResultsForm form input.allThemesJson").val(originalSearchRequestJson.themes);
			
			$("#hiddenRefreshResultsForm form input.sortkey").val(this.refreshModel.sortKey());
			
			$("#hiddenRefreshResultsForm form input.reflocation").val(this.refreshModel.refLocation());
			$("#hiddenRefreshResultsForm form input.reflatitude").val(this.refreshModel.refLatitude);
			$("#hiddenRefreshResultsForm form input.reflongitude").val(this.refreshModel.refLongitude);
			
			$("#hiddenRefreshResultsForm form input.filterByProximity").val(this.refreshModel.isFilteredByProximity());
			$("#hiddenRefreshResultsForm form input.filterByProximityMaxDistance").val(this.refreshModel.filterByProximityMaxDistance());
			
			$("#hiddenRefreshResultsForm form").submit();
		};
	},


}