
// DEPRECATED, please use the inputLocation

var geocoder;
var map;
var marker;

// call this to initize the autocomplete logic between an input field (
// inputTextJQSelector), a div when google will put the map (canvasId) and two
// field where the lat and long are recorded
function initializeGeoCoder(inputTextJQSelector, canvasId,
		locationLatJQSelector, locationLongJQSelector, initLat, initLong) {

	if (typeof google != "undefined") {

		geocoder = new google.maps.Geocoder();

		var options = {
			zoom : 8,
			mapTypeId : google.maps.MapTypeId.ROADMAP
		};

		map = new google.maps.Map(document.getElementById(canvasId), options);

		marker = new google.maps.Marker({
			map : map,
			draggable : false
		});

		if (initLat != undefined && initLong != undefined) {
			var latlng = new google.maps.LatLng(initLat, initLong);

			marker.setPosition(latlng);
			map.setCenter(latlng);
		}

		$(inputTextJQSelector).autocomplete(
				{
					// This bit uses the geocoder to fetch address values
					source : function(request, response) {
						geocoder.geocode({
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
						chosenLatLng = new google.maps.LatLng(ui.item.latitude,
								ui.item.longitude);

						$(locationLatJQSelector).val(ui.item.latitude);
						$(locationLongJQSelector).val(ui.item.longitude);
						$(inputTextJQSelector).val(ui.item.label);

						marker.setPosition(chosenLatLng);
						map.setCenter(chosenLatLng);

					}
				});

	}
}
