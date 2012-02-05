var dabInputMultiThemesLib = {

	// ////////////////////////////////////////
	// controllers

	InputMultiThemesController : function(inputMultiHtml, listOfThemesObject, initSelectedThemes, whenListOfSelectedThemesChangesCallback) {

		this.inputMultiHtml = inputMultiHtml;
		this.inputMultiThemesModel = new dabInputMultiThemesLib.InputMultiThemesModel(listOfThemesObject, initSelectedThemes);
		this.whenListOfSelectedThemesChangesCallback = whenListOfSelectedThemesChangesCallback;

		this.init = function() {

			var self = this;
			ko.applyBindings(this.inputMultiThemesModel, this.inputMultiHtml[0]);

			// click on "add theme link"
			this.inputMultiHtml.find("span.addThemeLink").click(function(event) {
				self.whenUserClicksOnAddTheme(event);
			});

			// click on "delete theme img"
			this.inputMultiHtml.on("click", "ul.selectedThemes img.deleteImageLink", function(event) {
				self.whenUserClicksOnDeleteTheme(event);
				self.whenListOfSelectedThemesChangesCallback(self.inputMultiThemesModel.selectedThemes());
			});

			// click on "add" (in edit mode)
			this.inputMultiHtml.find("div.addThemeLine span.addButton").click(function(event) {
				self.whenUserConfirmsAddTheme(event);
				self.whenListOfSelectedThemesChangesCallback(self.inputMultiThemesModel.selectedThemes());
			});

			// click on "cancel" (in edit mode)
			this.inputMultiHtml.find("div.addThemeLine span.cancelButton").click(function(event) {
				self.whenUserClicksOnCancel(event);
			});

			// selection on the first drop down
			this.inputMultiHtml.on("change", "div.addThemeLine select.mainSelect", function(event) {
				self.whenUserChangeMainDropDown(event);
			});

		};

		this.whenUserClicksOnAddTheme = function(event) {
			this.inputMultiThemesModel.switchToEditionMode();
		};

		this.whenUserClicksOnDeleteTheme = function(event) {
			var deletedThemeId = $(event.target).parent().find("span.hiddenThemeId").text();
			var deletedSubThemeId = $(event.target).parent().find("span.hiddenSubThemeId").text();
			this.inputMultiThemesModel.removeSelectedTheme(deletedThemeId, deletedSubThemeId);
		};

		this.whenUserConfirmsAddTheme = function(event) {
			var selectedMainThemeId = this.inputMultiHtml.find("select.mainSelect").val();
			var selectedSecondaryThemeId = this.inputMultiHtml.find("select.secondarySelect").val();
			this.inputMultiThemesModel.addToSelectedThemesList(selectedMainThemeId, selectedSecondaryThemeId);
			this.inputMultiThemesModel.switchToNormalMode();
		};

		this.whenUserClicksOnCancel = function(event) {
			this.inputMultiThemesModel.switchToNormalMode();
		};

		this.whenUserChangeMainDropDown = function(event) {
			var selectedMainTheme = this.inputMultiHtml.find("div.addThemeLine select.mainSelect").val();
			this.inputMultiThemesModel.setSecondaryDropTo(selectedMainTheme);
		};

		this.init();
	},

	// ////////////////////////////////
	// data models

	InputMultiThemesModel : function(listOfThemesObj, initSelectedThemes) {

		this.selectedThemes = ko.observableArray();
		this.listOfThemesObj = listOfThemesObj;
		this.inputMode = ko.observable("normal");

		this.mainListData = [];
		this.secondaryListData = ko.observableArray();

		this.init = function(listOfThemesObj, initSelectedThemes) {
			var self = this;
			_.each(listOfThemesObj, function(theme) {
				self.mainListData.push(theme);
			});
			this.setSecondaryDropTo(listOfThemesObj[0].id);
			
			if (initSelectedThemes != null) {
				_.each(initSelectedThemes, function(selectedTheme) {
					self.addToSelectedThemesList(selectedTheme.themeId, selectedTheme.subThemeId);
				});
			}
		};

		this.addToSelectedThemesList = function(addedThemeId, addedSubthemeId) {
			var existingTheme = this.getSelectedTheme(addedThemeId, addedSubthemeId);
			if (existingTheme == null) {
				var themeDefinition = this.getThemeDefFor(addedThemeId);
				if (themeDefinition != null) {
					var subThemeDefinition = this.getSubThemeDefFor(themeDefinition, addedSubthemeId);
					if (subThemeDefinition != null) {
						var newTheme = new dabInputMultiThemesLib.SelectedTheme(themeDefinition.id, themeDefinition.label, subThemeDefinition.id,
								subThemeDefinition.label);
						this.selectedThemes.push(newTheme);
					}
				}
			}
		};

		this.removeSelectedTheme = function(removedThemeId, removedSubthemeId) {
			var existingTheme = this.getSelectedTheme(removedThemeId, removedSubthemeId);
			if (existingTheme != null) {
				this.selectedThemes.remove(existingTheme);
			}
		};

		this.getSelectedTheme = function(themeId, subThemeId) {
			return _.find(this.selectedThemes(), function(oneTheme) {
				return oneTheme.themeId == themeId && oneTheme.subThemeId == subThemeId;
			});
		};

		this.getThemeDefFor = function(themeId) {
			return _.find(this.listOfThemesObj, function(oneTheme) {
				return oneTheme.id == themeId;
			});
		};

		this.getSubThemeDefFor = function(themeDefinition, subThemeId) {
			return _.find(themeDefinition.subThemes, function(oneSubTheme) {
				return oneSubTheme.id == subThemeId;
			});
		};

		this.switchToEditionMode = function() {
			this.inputMode("edition");
		};

		this.switchToNormalMode = function() {
			this.inputMode("normal");
		};

		this.setSecondaryDropTo = function(selectedMainThemeId) {

			var self = this;
			var themeDef = _.find(this.listOfThemesObj, function(theme) {
				return theme.id == selectedMainThemeId
			});
			this.secondaryListData.splice(0, this.secondaryListData().length - 1);

			_.each(themeDef.subThemes, function(subtheme) {
				self.secondaryListData.push(subtheme);
			});
		};

		this.init(listOfThemesObj, initSelectedThemes);
	},

	Theme : function(id, label) {
		this.id = id;
		this.label = label;
		this.subThemes = [];

		this.addSubTheme = function(subtheme) {
			this.subThemes.push(subtheme);
		};
	},

	SubTheme : function(id, label) {
		this.id = id;
		this.label = label;
	},

	SelectedTheme : function(themeId, themeLabel, subThemeId, subThemeLabel) {
		this.themeId = themeId;
		this.themeLabel = themeLabel;
		this.subThemeId = subThemeId;
		this.subThemeLabel = subThemeLabel;
		
		this.jsonId;
		
		this.init = function() {
			var self = this;
			this.jsonId =  encodeURI(JSON.stringify([{themeId: self.themeId, subThemeId: self.subThemeId}] ));
		};
		
		this.init();
		
	},

};