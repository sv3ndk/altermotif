// simple controller to switch a visual display between two modes

var dualModeSwitcherLib = {

	ModeSwitchController : function(switchToSimpleLinkId,
			swichToAdvancedLinkId, simpleDivId, advancedDivId, simpleFocusId, advancedFocusId) {

		this.isSimpleMode = true;

		// /////////////////////
		// public API

		this.init = function() {
			var self = this;
			$(swichToAdvancedLinkId).click(function(event) {
				self.switchMode(false);
				$(advancedFocusId).focus();
			});

			$(switchToSimpleLinkId).click(function(event) {
				self.switchMode(true);
				$(simpleFocusId).focus();
			});

		};

		this.switchMode = function(newMode) {
			this.isSimpleMode = newMode;
			if (this.isSimpleMode) {
				$(advancedDivId).hide(250);
				$(simpleDivId).show(250);
			} else {
				$(simpleDivId).hide(250);
				$(advancedDivId).show(250);
			}
		};
		this.init();

	},
}