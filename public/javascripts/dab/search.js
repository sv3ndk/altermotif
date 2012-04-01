var dabSearchLib =  {
		
	// /////////////////////////////////
	// Controller for the simple search mode
	SimpleSearchController: function(searchPageLocation) {

	// "empty" URL of the search result page (query parameters will be appended based on user input)
	this.searchPageLocation = searchPageLocation;
	this.simpleSearchModel = new dabSearchLib.SimpleSearchModel();

	this.init = function() {
		var self = this;

		ko.applyBindings(this.simpleSearchModel, $("#categorieListOfDropboxes")[0]);

		// click on a tag in the tag cloud
		$("#tagCloudContainer").on("click", "a", function(event) {
			self.searchByTags(self, $(event.target).text());
		});

		// selection of a category:
		$("#categorieListOfDropboxes").on("change", "select", function(event) {
			self.searchByTheme(self, $(event.target).val());
		});

	};

	this.searchByTags = function(self, clickedTag) {
		if (clickedTag != undefined && clickedTag != "") {
			window.location = self.searchPageLocation + "?r.tag=" + clickedTag;
		}
	};

	this.searchByTheme = function(self, clickedThemeValue) {
		if (clickedThemeValue != undefined && clickedThemeValue != "") {
			window.location = self.searchPageLocation + "?r.themes=" + clickedThemeValue;
			}
		};
		
		this.init();
	},
	
	
	SimpleSearchModel : function() {
		
		this.allSingleThemeSubTheme = [];
		
		this.init = function() {
			var self = this;
			_.each(allThemes, function(oneTheme) {
				var theme = new dabInputMultiThemesLib.Theme(oneTheme.id, oneTheme.label);
				_.each(oneTheme.subThemes, function(oneSubTheme) {
					theme.addSubTheme(new dabInputMultiThemesLib.SelectedTheme(oneTheme.id, oneTheme.label, oneSubTheme.id, oneSubTheme.label));
				});
				self.allSingleThemeSubTheme.push(theme);
			});
			
		};
		
		this.init();
	},
	
};