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
		this.inputLocationController;
		this.refreshPageController;

		this.init = function(refreshPageController) {
			var self = this;

			this.refreshPageController = refreshPageController;
			this.sortByModel.init();
			this.inputLocationController = new dabInputLocationLib.InputLocationController($("#projectResultsInputLocation div"),
					defaultSortByProximityReference, defaultSortByProximityReferenceLatitude, defaultSortByProximityReferenceLongitude,
					this.whenUserCancelUpdateSortRefLocation, function(newValue) {
						self.whenUserConfirmsUpdateSortRefLocation(newValue)
					});

			// click on "sort by" link
			$("#projectResultSortByLink").click(function(event) {
				self.whenUserClicksOnSortByLink(event);
			});

			// click on "change sort ref location"
			$("#projectResultSortByProximityChangeRefLocationLink").click(function(event) {
				self.whenUserClicksOnChangeSortRefPoint(self);
			});

			// click on the radio button to change the sort key
			self.sortByModel.sortKey.subscribe(function(newValue) {
				self.whenSortKeyIsUpdated(newValue);
			});

			// knockout bindings
			ko.applyBindings(this.sortByModel, $("#projectResultSortByDetails")[0]);

			// click on "refresh result" button
			$("#projectResultUpdateResultSortByButton").click(function(event) {
				self.refreshPageController.refresh();
			});
		};

		this.whenUserClicksOnSortByLink = function(event) {
			this.sortByModel.switchVisibility();
		};

		this.whenSortKeyIsUpdated = function(newValue) {
			$("#hiddenRefreshResultsForm form input.sortkey").val(newValue);
		};

		this.whenUserClicksOnChangeSortRefPoint = function(self) {
			if ($("#projectResultSortByProximityChangeRefLocationLink").hasClass("dabLink")) {
				$("#projectResultSortByProximityChangeRefLocationLink").removeClass("dabLink").addClass("dabLinkDisabled");
				self.inputLocationController.showInput();
			}
		};

		this.whenUserCancelUpdateSortRefLocation = function() {
			$("#projectResultSortByProximityChangeRefLocationLink").removeClass("dabLinkDisabled").addClass("dabLink");
		};

		// this is called back from the input location element
		this.whenUserConfirmsUpdateSortRefLocation = function(newLocation, newLatitude, newLongitude) {
			$("#projectResultSortByProximityChangeRefLocationLink").removeClass("dabLinkDisabled").addClass("dabLink");
			this.sortByModel.setSortByProximityRefLocation(newLocation, newLatitude, newLongitude);
		};

	},

	SortByModel : function() {
		this.isVisible = ko.observable(false);
		this.sortKey = ko.observable("alphabetic");
		this.sortByProximityRefLocation = ko.observable(defaultSortByProximityReference);

		this.init = function() {

			if (originalSearchRequestJson.sortkey != null && originalSearchRequestJson.sortkey != "") {
				this.sortKey(originalSearchRequestJson.sortkey);
			}
		};

		this.switchVisibility = function(visibleValue) {
			var self = this;
			this.isVisible(!this.isVisible());
		};

		this.setSortByProximityRefLocation = function(newLocation, newLatitude, newLongitude) {
			this.sortByProximityRefLocation(newLocation);
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