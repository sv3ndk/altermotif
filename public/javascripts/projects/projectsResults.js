var projectResultsCtrl;

$(document).ready(function() {
	projectResultsCtrl = new dabProjectResultsLib.ProjectResultsController();
	projectResultsCtrl.init();
});

var dabProjectResultsLib = {

	// /////////////////////////////////////////////////////////
	// controller for the main forum widget
	ProjectResultsController : function() {

		this.languageMapper = new dabLanguageLib.LanguageMapper();
		this.refreshModel = new dabProjectResultsLib.RefreshModel(this.languageMapper);
		
		this.filterController = new dabProjectResultsLib.FilterController(this.refreshModel, this.languageMapper);
		this.sortByController = new dabProjectResultsLib.SortByController(this.refreshModel);
		this.refreshPageController = new dabProjectResultsLib.RefreshPageController(this.refreshModel);
		

		this.init = function() {
			var self = this;
			
			this.languageMapper.init(allPossibleLanguages);
			this.refreshModel.init();
			this.filterController.init();
			this.sortByController.init();
			
			// click on "refresh result" button
			$("#projectResultUpdateResultButton").click(function(event) {
				self.refreshPageController.refresh();
			});

			// knockout bindings
			ko.applyBindings(this.refreshModel, $("#projectResultRefineQueryContainer")[0]);
		};
	},
	
	
	FilterController : function(refreshModel, languageMapper) {
		
		this.refreshModel = refreshModel;
		this.languageMapper = languageMapper;
		this.inputLocationController;
		
		
		this.init = function () {
			var self = this;

			this.inputLocationController = new dabInputLocationLib.InputLocationController($("#projectResultsFilterInputLocation div"),
					defaultReferenceLatitude, defaultReferenceLongitude, defaultRefenceLocation,
					this.whenUserCancelUpdateFilterRefLocation, function(newRefLoc, newLat, newLong) {
						self.whenUserConfirmsUpdateFilterRefLocation(newRefLoc, newLat, newLong)
					});

			
			// click on "filter by" link
			$("#projectResultFilterLink").click(function(event) {
				self.refreshModel.switchFilterByVisibility();
			});
			
			// click on "change filter ref location"
			$("#projectResultFilterByProximityChangeRefLocationLink").click(function(event) {
				self.whenUserClicksOnChangeFilterRefPoint(self);
			});
			
			// this is present in dab.js
			dabUtils.makeInputDatePicker("#projectResultMaxDueDate", '-0:+100');
			
			this.setupSetLanguageAutoComplete();

		};
		
		this.whenUserClicksOnChangeFilterRefPoint = function() {
			var self = this;
			if ($("#projectResultFilterByProximityChangeRefLocationLink").hasClass("dabLink")) {
				$("#projectResultFilterByProximityChangeRefLocationLink").removeClass("dabLink").addClass("dabLinkDisabled");
				self.inputLocationController.showInput();
			}
		};
		
		this.whenUserCancelUpdateFilterRefLocation = function () {
			$("#projectResultFilterByProximityChangeRefLocationLink").removeClass("dabLinkDisabled").addClass("dabLink");
		};
		
		this.whenUserConfirmsUpdateFilterRefLocation = function(newLocation, newLatitude, newLongitude) {
			$("#projectResultFilterByProximityChangeRefLocationLink").removeClass("dabLinkDisabled").addClass("dabLink");
			this.refreshModel.updateRefLocation(newLocation, newLatitude, newLongitude);
		};
		
		
		this.setupSetLanguageAutoComplete = function() {
			$("#projectResultFilterLanguage").autocomplete({
				source : this.languageMapper.allPossibleLanguagesList
			}
			);
		}
		
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
				self.refreshModel.switchSortByVisibility();
			});

			// click on "change sort ref location"
			$("#projectResultSortByProximityChangeRefLocationLink").click(function(event) {
				self.whenUserClicksOnChangeSortRefPoint(self);
			});
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
			
			$("#hiddenRefreshResultsForm form input.sortkey").val(this.refreshModel.sortKey());
			
			$("#hiddenRefreshResultsForm form input.reflocation").val(this.refreshModel.refLocation());
			$("#hiddenRefreshResultsForm form input.reflatitude").val(this.refreshModel.refLatitude);
			$("#hiddenRefreshResultsForm form input.reflongitude").val(this.refreshModel.refLongitude);
			
			$("#hiddenRefreshResultsForm form input.filterByProximity").val(this.refreshModel.isFilteredByProximity());
			$("#hiddenRefreshResultsForm form input.filterByDate").val(this.refreshModel.isFilteredByDueDate());
			$("#hiddenRefreshResultsForm form input.filterByLanguage").val(this.refreshModel.isFilteredByLanguage());
			
			$("#hiddenRefreshResultsForm form input.filterByProximityMaxDistance").val(this.refreshModel.filterByProximityMaxDistance());
			$("#hiddenRefreshResultsForm form input.filterByDateMaxDate").val(this.refreshModel.filterByDueDateMaxDate());
			$("#hiddenRefreshResultsForm form input.filterByLanguageCode").val(this.refreshModel.getIsoCodeOfFilteredByLanguge());
			
			
			$("#hiddenRefreshResultsForm form").submit();
		};
	},

	
	RefreshModel : function(languageMapper) {
		this.languageMapper = languageMapper;
		
		this.isSortByVisible = ko.observable(false);
		this.sortKey = ko.observable("alphabetic");
		
		this.isFilterByVisible = ko.observable(false);
		this.isFilteredByProximity = ko.observable(originalSearchRequestJson.filterProx);
		this.filterByProximityMaxDistance = ko.observable(10);
		this.isFilteredByDueDate = ko.observable(originalSearchRequestJson.filterDate);
		this.filterByDueDateMaxDate = ko.observable();
		this.isFilteredByLanguage = ko.observable(originalSearchRequestJson.filterLg);
		
		this.refLocation = ko.observable(defaultRefenceLocation);
		this.refLatitude = defaultReferenceLatitude;
		this.refLongitude = defaultReferenceLongitude;
		
		this.init = function() {
			// init of search location (from request)
			if (originalSearchRequestJson.rl != undefined) {
				if (originalSearchRequestJson.rl.location != undefined ) {
					this.refLocation(originalSearchRequestJson.rl.location);
				}
				if (originalSearchRequestJson.rl.latitude != undefined ) {
					this.refLatitude = originalSearchRequestJson.rl.latitude;
				}
				if (originalSearchRequestJson.rl.longitude != undefined ) {
					this.refLongitude = originalSearchRequestJson.rl.longitude;
				}
			}
			
			// init of max distance, max due date and/or specified language (from request)
			if (originalSearchRequestJson.maxDistance != null ) {
				this.filterByProximityMaxDistance(originalSearchRequestJson.maxDistance);
			}

			if (originalSearchRequestJson.maxDueDateStr != null ) {
				this.filterByDueDateMaxDate(originalSearchRequestJson.maxDueDateStr);
			}

			if (originalSearchRequestJson.lg != null ) {
				$("#projectResultFilterLanguage").val(this.languageMapper.resolveLanguageOfCode(originalSearchRequestJson.lg));
			}
			
			// init of sort key
			if (originalSearchRequestJson.sortkey != null && originalSearchRequestJson.sortkey != "") {
				this.sortKey(originalSearchRequestJson.sortkey);
			}
			
		};
		
		this.switchSortByVisibility = function(visibleValue) {
			this.isSortByVisible(!this.isSortByVisible());
		};
		
		this.switchFilterByVisibility = function(visibleValue) {
			this.isFilterByVisible(!this.isFilterByVisible());
		};
		
		this.getIsoCodeOfFilteredByLanguge = function () {
			if ($("#projectResultFilterLanguage").val() == "") {
				return undefined;
			} else {
				return this.languageMapper.resolveCodeOfLanguage($("#projectResultFilterLanguage").val());
			}
		};
		
		this.updateRefLocation = function(newLocation, newLatitude, newLongitude) {
			this.refLocation(newLocation);
			this.refLatitude = newLatitude;
			this.refLongitude = newLongitude;
		};
	},

};