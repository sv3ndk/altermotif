var dabInputMultiThemesLib = {

	// ////////////////////////////////////////
	// controllers

	InputMultiThemesController : function(inputMultiHtml, listOfThemesObject) {

		this.inputMultiHtml = inputMultiHtml;
		this.inputMultiThemesModel = new dabInputMultiThemesLib.InputMultiThemesModel(listOfThemesObject);

		this.init = function() {
		
			var self = this;
			ko.applyBindings(this.inputMultiThemesModel, this.inputMultiHtml[0]);

			
			// click on "add theme"
			this.inputMultiHtml.find("span.addThemeLink").click(function(event) {self.whenUserClicksOnAddTheme(event);});
			
			// click on "cancel"
			this.inputMultiHtml.find("div.addThemeLine span.cancelButton").click(function(event) {self.whenUserClicksOnCancel(event);});
			
			// selection on the first drop down
			this.inputMultiHtml.on("change", "div.addThemeLine select.mainSelect", function(event) {self.whenUserChangeMainDropDown(event);});
			
			
		};
		
		
		this.whenUserClicksOnAddTheme = function(event) {
			this.inputMultiThemesModel.switchToEditionMode();
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
	
	
	InputMultiThemesModel : function(listOfThemesObj) {
		
		this.listOfThemesObj = listOfThemesObj;
		this.inputMode = ko.observable("normal");
		
		this.mainListData = [];
		this.secondaryListData = ko.observableArray();
		
		this.init = function(listOfThemesObj) {
			var self = this;
			_.each(listOfThemesObj, function(theme) {
				self.mainListData.push(theme);
			});
			
			
			this.setSecondaryDropTo(listOfThemesObj[0].id);
		};
		
		
		this.switchToEditionMode = function() {
			this.inputMode("edition");
		};
		
		this.switchToNormalMode = function() {
			this.inputMode("normal");
		};
		
		this.setSecondaryDropTo = function(selectedMainThemeId) {
			
			var self = this;
			var themeDef = _.find(this.listOfThemesObj, function(theme) {return theme.id == selectedMainThemeId});
			this.secondaryListData.splice(0, this.secondaryListData().length-1);
			
			_.each(themeDef.subThemes, function(subtheme) {
				self.secondaryListData.push(subtheme);
			});
			
			
			
		};
		
		
		this.init(listOfThemesObj);
		
	},
	

};
