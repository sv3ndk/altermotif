// this is used as the "reduce" part of the map/reduce algo to count tags
// same implementation for both the group tags and he project tags

function (key, values) {
	
	var totalTagCount = 0;
	
	for (var i = 0; i < values.length; i++) {
		totalTagCount += values[i];
	}
	
	return totalTagCount;
}

