function (key, values) {
	
	var totalTagCount = 0;
	
	for (var i = 0; i < values.length; i++) {
		totalTagCount += values[i];
	}
	
	return totalTagCount;
}

