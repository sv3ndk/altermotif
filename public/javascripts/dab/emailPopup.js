

var dabEmailPopupLib = {
		
	EmailPopupController : function (htmlPopupElement) {

		this.htmlPopupElement = htmlPopupElement;
		
		///////////////////////
		// public API
		this.open = function () {
			htmlPopupElement.dialog("open");
			htmlPopupElement.find(".toInput").focus();
		};

		this.close = function () {
			htmlPopupElement.dialog("close");
		};
		
		
		///////////////////////
		// internal API
		this.init = function() {
			var self = this;
			htmlPopupElement.dialog({
				autoOpen : false,
				width: 800,
				height: 480,
				"buttons" : [ {
					text : okLabelValue,
					click : function(event) {
						self.close();
					}
				},

				{
					text : cancelLabelValue,
					click : function() {
						self.close();
					}
				}
				]
			});
		};
		
		this.init();
	},
};