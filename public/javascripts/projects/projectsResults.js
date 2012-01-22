var projectResultsCtrl;

$(document).ready(function() {
	projectResultsCtrl = new dabProjectResultsLib.ProjectResultsController();
	projectResultsCtrl.init();
});

var dabProjectResultsLib = {

	// /////////////////////////////////////////////////////////
	// controller for the main forum widget
	ProjectResultsController : function() {

		this.refreshModel = new dabProjectResultsLib.RefreshModel();
		this.sortByController = new dabProjectResultsLib.SortByController(this.refreshModel);
		this.refreshPageController = new dabProjectResultsLib.RefreshPageController(this.refreshModel);

		this.init = function() {
			var self = this;
			
			this.refreshModel.init();
			this.sortByController.init();
			
			// click on "refresh result" button
			$("#projectResultUpdateResultButton").click(function(event) {
				self.refreshPageController.refresh();
			});

			// knockout bindings
			ko.applyBindings(this.refreshModel, $("#projectResultRefineQueryContainer")[0]);
		};
	},

	SortByController : function(refreshModel) {

		this.refreshModel = refreshModel;
		this.inputLocationController;

		this.init = function(refreshModel) {
			var self = this;

			this.inputLocationController = new dabInputLocationLib.InputLocationController($("#projectResultsSortInputLocation div"),
					defaultReferenceLatitude, defaultReferenceLongitude, defaultRefenceLocation,
					this.whenUserCancelUpdateSortRefLocation, function(newRefLoc, newLat, newLong) {
						self.whenUserConfirmsUpdateSortRefLocation(newRefLoc, newLat, newLong)
					});

			// click on "sort by" link
			$("#projectResultSortByLink").click(function(event) {
				self.whenUserClicksOnSortByLink(event);
			});

			// click on "change sort ref location"
			$("#projectResultSortByProximityChangeRefLocationLink").click(function(event) {
				self.whenUserClicksOnChangeSortRefPoint(self);
			});

		};

		this.whenUserClicksOnSortByLink = function(event) {
			this.refreshModel.switchSortByVisibility();
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
			this.refreshModel.updateRefLocation(newLocation, newLatitude, newLongitude);
		};

	},


	RefreshPageController : function(refreshModel) {
		
		this.refreshModel = refreshModel;
		
		this.refresh = function() {
			$("#hiddenRefreshResultsForm form input.term").val(originalSearchRequestJson.term);
			$("#hiddenRefreshResultsForm form input.tag").val(originalSearchRequestJson.tag);
			$("#hiddenRefreshResultsForm form input.allThemesJson").val(originalSearchRequestJson.themes);
			
			$("#hiddenRefreshResultsForm form input.sortkey").val(refreshModel.sortKey());
			
			$("#hiddenRefreshResultsForm form input.reflocation").val(refreshModel.refLocation());
			$("#hiddenRefreshResultsForm form input.reflatitude").val(refreshModel.refLatitude);
			$("#hiddenRefreshResultsForm form input.reflongitude").val(refreshModel.refLongitude);
			
			$("#hiddenRefreshResultsForm form").submit();
		};
	},

	FilterController : function() {

	},
	
	RefreshModel : function() {
		this.isSortByVisible = ko.observable(false);
		this.sortKey = ko.observable("alphabetic");
		this.refLocation = ko.observable(defaultRefenceLocation);
		this.refLatitude = defaultReferenceLatitude;
		this.refLongitude = defaultReferenceLongitude;
		
		
		this.init = function() {
			if (originalSearchRequestJson.sortkey != null && originalSearchRequestJson.sortkey != "") {
				this.sortKey(originalSearchRequestJson.sortkey);
			}
		};
		
		this.switchSortByVisibility = function(visibleValue) {
			var self = this;
			this.isSortByVisible(!this.isSortByVisible());
		};
		
		this.updateRefLocation = function(newLocation, newLatitude, newLongitude) {
			this.refLocation(newLocation);
			this.refLatitude = newLatitude;
			this.refLongitude = newLongitude;
		};
		
	},

};