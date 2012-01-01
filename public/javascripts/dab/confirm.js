// utility methods in order to display a confirmation method to the user and then to execution some method

var Confirm =  {

	/////////////////////// 
	// constructor for AskAndProceed: simple class to ask a confirmation to the end user before doing something 
	//
	// example usage:
	// 		new Confirm.AskAndProceed("#editProjectAssetsContainer", "img.deleteTaskLink", confirmRemoveProjectAssetText, undefined, this.afterUserConfirmsRemoveAssetFromProject).init();
    //
	/////////////////////// 
	AskAndProceed : function (callerObj, clickableJqParentSelector, clickableJqSubSelector, confirmationText, beforeAsking, onConfirmation) {
	
		this.callerObj = callerObj;
		this.clickableJqParentSelector = clickableJqParentSelector;
		this.clickableJqSubSelector = clickableJqSubSelector;
		this.confirmationText = confirmationText;
		this.beforeAskingFunction = beforeAsking;
		this.onConfirmationFunction = onConfirmation;
		
		////////////////////
		// init
		this.init = function () {
			
			var self = this;
			
			// first action: click on the trigger link
			$(self.clickableJqParentSelector).on("click", self.clickableJqSubSelector, function(event) {
				$("#confirmationPopup span").text(self.confirmationText);
				if (self.beforeAskingFunction != undefined) {
					self.beforeAskingFunction(callerObj, event);
				}
				$("#confirmationPopup").dialog("open");		
			});
	
			this.initDialogConstruction(this);
		};
		
		
		////////////////////////////
		this.initDialogConstruction = function (self) {
			// OK/Cancel dialog => when click OK, we callback the executeConfirmationFunction function otherwise we simply close
			$("#confirmationPopup").dialog({
				autoOpen : false,
				width: 400,
				"buttons" : [ 
				{
					text : okLabelValue,
					click : function(event){
						self.onConfirmationFunction(callerObj, event);
						self.closeDialog();
					}
				},
				{
					text : cancelLabelValue,
					click : self.closeDialog
				}
				]
			});
			
			// simple popup message displayed to the user
			$("#messagePopup").dialog({
				autoOpen : false,
				width: 400,
				"buttons" : [ 
		             {
		            	 text : okLabelValue,
		            	 click : function () {
		            		 $("#messagePopup").dialog("close");		
		            	 }
		             }
		           ]
			});
			
		};
		
		////////////////////////////
		this.closeDialog = function() {
			$("#confirmationPopup").dialog("close");		
		}	
	}

}


