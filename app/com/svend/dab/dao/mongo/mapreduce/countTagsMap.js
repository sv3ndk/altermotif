function () {
	
	if (this.tags != undefined) {
		for (var i = 0; i < this.tags.length; i++) {
			emit(this.tags[i], 1);
		}
	}
}

