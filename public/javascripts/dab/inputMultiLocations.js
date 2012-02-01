var dabInputMultiLocationsLib = {

	// ////////////////////////////////////////
	// controllers

	InputMultiLocationsController : function(inputMultiHtml, initialLocations) {

		this.inputMultiHtml = inputMultiHtml;

		this.inputMultiLocationsModel = new dabInputMultiLocationsLib.InputMultiLocationsModel(initialLocations);
		this.inputLocationController;

		this.init = function() {
			var self = this;

			ko.applyBindings(this.inputMultiLocationsModel, inputMultiHtml[0]);

			this.inputLocationController = new dabInputLocationLib.InputLocationController($(inputMultiHtml.find(".editGroupLocation div")), 0, 0, "",
					this.whenUserCancelAddLocation, function(newRefLoc, newLat, newLong) {
						self.whenUserConfirmsAddLocation(newRefLoc, newLat, newLong)
					});

			this.inputMultiHtml.find(".addLocationLink").click(function() {
				self.inputLocationController.reset();
				self.inputLocationController.showInput();
			});

			this.inputMultiHtml.on("click", "ul.locationsList li img.deleteImageLink", function(event) {
				self.whenUserClickOnRemoveLocation(event);
			});

		};

		this.getAllLocationJson = function() {
			var cleanLocation = _.map(this.inputMultiLocationsModel.locations(), function(indexedLocation) {
				return new dabInputMultiLocationsLib.OneLocation(indexedLocation.location, indexedLocation.latitude, indexedLocation.longitude);
			});
			return JSON.stringify(cleanLocation);
		};

		this.whenUserCancelAddLocation = function() {
			// NOP
		};

		this.whenUserConfirmsAddLocation = function(newLocation, newLatitude, newLongitude) {
			this.inputMultiLocationsModel.addLocation(newLocation, newLatitude, newLongitude);
		};

		this.whenUserClickOnRemoveLocation = function(event) {
			var clickedLocationIndex = $(event.target).parent().find("span.hiddenLocationIndex").text();
			this.inputMultiLocationsModel.removeLocation(clickedLocationIndex);
		};

		this.init();
	},

	// ///////////////////////////////////////////////////
	// data models

	InputMultiLocationsModel : function(initialLocations) {
		this.locations = ko.observableArray();

		this.init = function(initialLocations) {
			var self = this;
			_.each(initialLocations, function(oneLocation) {
				self.addLocation(oneLocation.location, oneLocation.latitude, oneLocation.longitude)
			});

		};

		this.addLocation = function(newLocation, newLatitude, newLongitude) {
			var index = this.locations().length + 1;
			this.locations.push(new dabInputMultiLocationsLib.IndexedLocation(index, newLocation, newLatitude, newLongitude));
		};

		this.removeLocation = function(removedIndex) {
			this.locations.remove(this.getLocation(removedIndex));
		};

		this.getLocation = function(locationIndex) {
			return _.find(this.locations(), function(location) {
				return location.index == locationIndex
			});
		};

		this.init(initialLocations);

	},

	OneLocation : function(location, latitude, longitude) {
		this.location = location;
		this.latitude = latitude;
		this.longitude = longitude;
	},

	IndexedLocation : function(index, location, latitude, longitude) {
		this.index = index;
		this.location = location;
		this.latitude = latitude;
		this.longitude = longitude;
	}

};