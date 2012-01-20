var projectResultsCtrl;

$(document).ready(function() {
	projectResultsCtrl = new dabProjectResultsLib.ProjectResultsController();
	projectResultsCtrl.init();
});

var dabProjectResultsLib = {

	// /////////////////////////////////////////////////////////
	// controller for the main forum widget
	ProjectResultsController : function() {

		this.sortByController = new dabProjectResultsLib.SortByController();
		this.refreshPageController = new dabProjectResultsLib.RefreshPageController();

		this.init = function() {
			this.refreshPageController.init();
			this.sortByController.init(this.refreshPageController);
		};

	},

	SortByController : function() {

		this.sortByModel = new dabProjectResultsLib.SortByModel();
		this.refreshPageController;

		this.init = function(refreshPageController) {
			var self = this;
			
			this.refreshPageController = refreshPageController;
			this.sortByModel.init();
			
			// click on "sort by" link
			$("#projectResultSortByLink").click(function(event) {
				self.whenUserClicksOnSortByLink(event);
			});

			// click on "refresh result" button
			$("#projectResultUpdateResultSortByButton").click(function(event) {
				self.refreshPageController.refresh();
			});
			
			self.sortByModel.sortKey.subscribe(function (newValue) {self.whenSortKeyIsUpdated(newValue);});
			
			// knockout bindings
			ko.applyBindings(this.sortByModel, $("#projectResultSortByDetails")[0]);
		};

		this.whenUserClicksOnSortByLink = function (event) {
			this.sortByModel.switchVisibility();
		};
		
		this.whenSortKeyIsUpdated = function(newValue) {
			$("#hiddenRefreshResultsForm form input.sortkey").val(newValue);
		};
	},
	
	
	SortByModel : function () {
		this.isVisible = ko.observable(false);
		this.sortKey = ko.observable("alphabetic");
		
		this.init  = function() {
			
			if (originalSearchRequestJson.sortkey != null) {
				this.sortKey(originalSearchRequestJson.sortkey);
			}

		};
		
		this.switchVisibility = function (visibleValue) {
			var self = this;
			this.isVisible(! this.isVisible());
		};
		
	},

	
	RefreshPageController : function() {
		this.init = function() {
			$("#hiddenRefreshResultsForm form input.term").val(originalSearchRequestJson.term);
			$("#hiddenRefreshResultsForm form input.tag").val(originalSearchRequestJson.tag);
			$("#hiddenRefreshResultsForm form input.allThemesJson").val(originalSearchRequestJson.themes);
		};
		
		this.refresh = function() {
			$("#hiddenRefreshResultsForm form").submit();
		};
	},
	
	
	
	FilterController : function() {

	},

}