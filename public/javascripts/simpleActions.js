// utility methods in order to display a confirmation method to the user and then to execution some method

var onConfirmationFunction;

// "on" version of the ask and act: uses the "on" mechanism of jquery (instead of a click handler on a single click target)
function askAndAct_On(clickableJqParentSelector, clickableJqSubSelector, confirmationText, beforeAsking) {

	$(clickableJqParentSelector).on("click", clickableJqSubSelector, function(event) {
		$("#confirmationPopup span").text(confirmationText);
		beforeAsking(event);
		$("#confirmationPopup").dialog("open");		
	});

}

function initAskAndAct() {
	
	$("#confirmationPopup").dialog({
		autoOpen : false,
		width: 400,
		"buttons" : [ 
		{
			text : okLabelValue,
			click : executeConfirmationFunction
		},
		{
			text : cancelLabelValue,
			click : closeConfirmationDialog
		}
		]
	});

}

function setConfirmationFunction(onConfirmation) {
	onConfirmationFunction = onConfirmation;
}


function executeConfirmationFunction() {
	// this method is usually undefined when jquery sets up the dialog above ( is is only defined when setConfirmationFunction is called, after the user has clicked a particular link)
	onConfirmationFunction();
}


function closeConfirmationDialog() {
	$("#confirmationPopup").dialog("close");		
}
