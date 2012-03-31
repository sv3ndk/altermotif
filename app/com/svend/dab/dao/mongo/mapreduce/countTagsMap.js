// this is used as the "map" part of the map/reduce algo to count tags
// same implementation for both the group tags and he project tags


function () {
	
	if (this.tags != undefined) {
		for (var i = 0; i < this.tags.length; i++) {
			emit(this.tags[i], 1);
		}
	}
}

