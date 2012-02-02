var dabInputMultiTextLib = {

	// ////////////////////////////////////////
	// controllers

	InputMultiTextController : function(inputMultiHtml, initialTextValues) {

		this.inputMultiHtml = inputMultiHtml;
		this.inputMultiTextModel = new dabInputMultiTextLib.InputMultiTextModel(initialTextValues);

		this.init = function() {
			var self = this;

			ko.applyBindings(this.inputMultiTextModel, this.inputMultiHtml[0]);

			this.inputMultiHtml.find(".addTextLink").click(function() {
				self.whenUserClicksOnAddTag();
			});

			this.inputMultiHtml.find("div.inputTextGroup .cancelButton").click(function() {
				self.whenUserClicksOnCancel();
			});

			this.inputMultiHtml.find("div.inputTextGroup .okButton").click(function() {
				self.whenUserClicksOnOk();
			});

			this.inputMultiHtml.on("click", "ul.textList li img.deleteImageLink", function(event) {
				self.whenUserClickOnRemoveText(event);
			});

		};

		this.getTextJson = function() {
			return JSON.stringify(this.inputMultiTextModel.getTextAsArray());
		};

		this.whenUserClicksOnAddTag = function() {
			if (this.inputMultiTextModel.inputMode() == "normal") {
				this.inputMultiHtml.find("input.inputText").val("");
				this.inputMultiTextModel.switchToEditionMode();
				this.inputMultiHtml.find("input.inputText").focus();
			}
		};

		this.whenUserClicksOnCancel = function() {
			this.inputMultiTextModel.switchToNormalMode();
		}

		this.whenUserClicksOnOk = function() {
			var addedText = this.inputMultiHtml.find("input.inputText").val();
			if (addedText != undefined && addedText != "") {
				this.inputMultiTextModel.addText(addedText);
			}
			this.inputMultiTextModel.switchToNormalMode();
		};

		this.whenUserClickOnRemoveText = function(event) {
			var clickedText = $(event.target).parent().find("span").text();
			this.inputMultiTextModel.removeText(clickedText);
		};

		this.init();
	},

	// ///////////////////////////////////////////////////
	// data models

	InputMultiTextModel : function(initialTexts) {
		this.texts = ko.observableArray();
		this.inputMode = ko.observable("normal");

		this.init = function(initialTexts) {
			var self = this;
			_.each(initialTexts, function(oneText) {
				self.addText(oneText);
			});
		};

		this.getTextAsArray = function() {
			var returned = [];
			_.each(this.texts(), function(oneText) {
				returned.push(oneText.text);
			});
			return returned;

		};

		this.switchToNormalMode = function() {
			this.inputMode("normal");
		};

		this.switchToEditionMode = function() {
			this.inputMode("edition");
		};

		this.addText = function(text) {
			var existing = this.getText(text);
			if (existing == null) {
				this.texts.push(new dabInputMultiTextLib.OneText(text));
			}
		};

		this.removeText = function(removedText) {
			this.texts.remove(this.getText(removedText));
		};

		this.getText = function(text) {
			return _.find(this.texts(), function(onText) {
				return onText.text == text;
			});
		};

		this.init(initialTexts);

	},

	OneText : function(text) {
		this.text = text;
	},

};