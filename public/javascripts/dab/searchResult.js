
var dabSearchResultLib = {

		
		FilterController : function(refreshModel, languageMapper) {
			
			this.refreshModel = refreshModel;
			this.languageMapper = languageMapper;
			this.inputLocationController;
			
			this.init = function () {
				var self = this;

				this.inputLocationController = new dabInputLocationLib.InputLocationController($("div.searchResultsFilterInputLocation div"),
						this.refreshModel.refLatitude, this.refreshModel.refLongitude, this.refreshModel.refLocation(), 
						this.whenUserCancelUpdateFilterRefLocation, function(newRefLoc, newLat, newLong) {
							self.whenUserConfirmsUpdateFilterRefLocation(newRefLoc, newLat, newLong)
						});

				
				// click on "filter by" link
				$("span.searchResultFilterLink").click(function(event) {
					self.refreshModel.switchFilterByVisibility();
				});
				
				// click on "change filter ref location"
				$("span.searchResultFilterByProximityChangeRefLocationLink").click(function(event) {
					self.whenUserClicksOnChangeFilterRefPoint(self);
				});
				
				// this is present in dab.js
				dabUtils.makeInputDatePicker("#projectResultMaxDueDate", '-20:+100');
				
				this.setupSetLanguageAutoComplete();

			};
			
			this.whenUserClicksOnChangeFilterRefPoint = function() {
				var self = this;
				if ($("span.searchResultFilterByProximityChangeRefLocationLink").hasClass("dabLink")) {
					$("span.searchResultFilterByProximityChangeRefLocationLink").removeClass("dabLink").addClass("dabLinkDisabled");
					self.inputLocationController.showInput();
				}
			};
			
			this.whenUserCancelUpdateFilterRefLocation = function () {
				$("span.searchResultFilterByProximityChangeRefLocationLink").removeClass("dabLinkDisabled").addClass("dabLink");
			};
			
			this.whenUserConfirmsUpdateFilterRefLocation = function(newLocation, newLatitude, newLongitude) {
				$("span.searchResultFilterByProximityChangeRefLocationLink").removeClass("dabLinkDisabled").addClass("dabLink");
				this.refreshModel.updateRefLocation(newLocation, newLatitude, newLongitude);
			};
			
			
			this.setupSetLanguageAutoComplete = function() {
				if(this.languageMapper != null) {
					// null is ok: we re-use this controller for the group result search, for which there is not concept of "group language"
					$("span.searchResultFilterLanguage").autocomplete({
						source : this.languageMapper.allPossibleLanguagesList
					}
					);
				}
			}
		},


		SortByController : function(refreshModel) {

			this.refreshModel = refreshModel;
			this.inputLocationController;

			this.init = function(refreshModel) {
				var self = this;

				this.inputLocationController = new dabInputLocationLib.InputLocationController($("div.searchResultsSortInputLocation div"),
						this.refreshModel.refLatitude, this.refreshModel.refLongitude, this.refreshModel.refLocation(),
						this.whenUserCancelUpdateSortRefLocation, function(newRefLoc, newLat, newLong) {
							self.whenUserConfirmsUpdateSortRefLocation(newRefLoc, newLat, newLong)
						});

				// click on "sort by" link
				$("span.searchResultSortByLink").click(function(event) {
					self.refreshModel.switchSortByVisibility();
				});

				// click on "change sort ref location"
				$("span.searchResultSortByProximityChangeRefLocationLink").click(function(event) {
					self.whenUserClicksOnChangeSortRefPoint(self);
				});
			};

			this.whenUserClicksOnChangeSortRefPoint = function(self) {
				if ($("span.searchResultSortByProximityChangeRefLocationLink").hasClass("dabLink")) {
					$("span.searchResultSortByProximityChangeRefLocationLink").removeClass("dabLink").addClass("dabLinkDisabled");
					self.inputLocationController.showInput();
				}
			};

			this.whenUserCancelUpdateSortRefLocation = function() {
				$("span.searchResultSortByProximityChangeRefLocationLink").removeClass("dabLinkDisabled").addClass("dabLink");
			};

			// this is called back from the input location element
			this.whenUserConfirmsUpdateSortRefLocation = function(newLocation, newLatitude, newLongitude) {
				$("span.searchResultSortByProximityChangeRefLocationLink").removeClass("dabLinkDisabled").addClass("dabLink");
				this.refreshModel.updateRefLocation(newLocation, newLatitude, newLongitude);
			};
			
			this.init();

		},
		
		// data model containing the information for refreshing the search result page.
		// all parts of it are used for project search result pages, whereas only parts of it are used for group search results
		RefreshSearchResultModel : function() {
			
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
			
			////////////////////////
			// init of search location (from request)
			// this should only be called *after* the language mapper has been initialized
			// this also assumes that something like his exist in the html:
			//   	    var originalSearchRequestJson = ${originalSearchRequestJson.raw()};
			///////////////////////////////////
			this.init = function() {
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
			
			this.updateRefLocation = function(newLocation, newLatitude, newLongitude) {
				this.refLocation(newLocation);
				this.refLatitude = newLatitude;
				this.refLongitude = newLongitude;
			};
			
			this.init();
		},
};