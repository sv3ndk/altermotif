
// to be used togheter with /altermotifPlay/app/views/tags/dab/inputLocation.html
var dabInputLocationLib = {
		
	InputLocationController : function(inputHtmlElement, initLat, initLong, initLocation,  whenInputIsCancelledCallback, whenInputIsConfirmedCallback) {
		
		this.inputHtmlElement = inputHtmlElement;
		this.whenInputIsCancelledCallback = whenInputIsCancelledCallback;
		this.whenInputIsConfirmedCallback = whenInputIsConfirmedCallback;
		
		this.geocoder = new google.maps.Geocoder();
		this.map;
		this.marker;
		
		this.latitude = initLat;
		this.longitude = initLong;
		this.location = initLocation;
		
		this.init = function() {
			var self = this;
			$(this.inputHtmlElement).find(".addLocationCancelButton").click(function() {
				self.whenInputIsCancelledCallback();
				self.hideInput();
			});

			$(this.inputHtmlElement).find(".addLocationOkButton").click(function () {
				self.whenInputIsConfirmedCallback(self.location, self.latitude, self.longitude);
				self.hideInput();
			});
			
			inputHtmlElement.find("input.addLocationInput").val(this.location);

			this.initializeGeoCoder();
		};
		
		this.showInput = function () {
			$(this.inputHtmlElement).show();
			google.maps.event.trigger(this.map, "resize");
			this.centerMapOnCurrentData();
			$(this.inputHtmlElement).find("input.addLocationInput").focus();
		};
		
		this.hideInput = function () {
			$(this.inputHtmlElement).hide();
		};
		
		
		this.centerMapOnCurrentData = function() {
			if (this.latitude != undefined && this.longitude != undefined) {
				var latlng = new google.maps.LatLng(this.latitude, this.longitude);
				this.marker.setPosition(latlng);
				this.map.setCenter(latlng);
			}
		}
		
		// ///////////////////////////////
		// internal API
		
		this.initializeGeoCoder = function() {
			
			var self = this;
			var options = {
					zoom : 8,
					mapTypeId : google.maps.MapTypeId.ROADMAP
			};
		
			this.map = new google.maps.Map(inputHtmlElement.find("div.inputLocation_city_map_canvas")[0], options);
			this.marker = new google.maps.Marker({
				map : self.map,
				draggable : false
			});
			
			this.centerMapOnCurrentData();
		
			inputHtmlElement.find("input.addLocationInput").autocomplete({
				// This bit uses the geocoder to fetch address values
				source : function(request, response) {
					self.geocoder.geocode({
						'address' : request.term
					}, function(results, status) {
						response($.map(results, function(item) {
							return {
								label : item.formatted_address,
								value : item.formatted_address,
								latitude : item.geometry.location.lat(),
								longitude : item.geometry.location.lng()
							};
						}));
					});
				},
				// This bit is executed upon selection of an address
				select : function(event, ui) {
					var chosenLatLng = new google.maps.LatLng(ui.item.latitude, ui.item.longitude);
					self.latitude = ui.item.latitude;
					self.longitude = ui.item.longitude;
					self.location = ui.item.label;
					self.marker.setPosition(chosenLatLng);
					self.map.setCenter(chosenLatLng);
				}
			});
		
		};
		
		this.init();
	},
}