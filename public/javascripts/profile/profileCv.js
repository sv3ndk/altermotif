function init() {
	initDeleteCv();
	initUploadCv();
}

function initDeleteCv(okLabel, cancelLabel) {

	// only registering the "delete cv" listener if the link is active (i.e. if
	// there is a cv to be deleted)
	if ($("#removeCvButton").hasClass("iconLink")) {

		$("#removeCvButton, #removeCvLink").click(function() {
			$("#confirmDeleteCvPopup").dialog("open");
		});

	}

	// initializing the dialog outside the "if", so that the corresponding div
	// is always invisible
	$("#confirmDeleteCvPopup").dialog({
		autoOpen : false,
		width : 450,
		modal : true,
		"buttons" : [ {
			text : okLabelValue,
			click : function() {
				$('#hiddenDeleteCvForm\\:hiddenDeleteCvButton').trigger('click');
				$(this).dialog("close");
			}
		},

		{
			text : cancelLabelValue,
			click : function() {
				$(this).dialog("close");
			}
		}

		]
	});

}

function initUploadCv() {

	$("#pleaseWaitDialog").dialog({
		autoOpen : false,
		closeOnEscape : false,
		modal : true,
		beforeClose : function(event, ui) {
			// prevents the dialog to close
			return false;
		}
	});

	$("#uploadCvLink, #uploadCvButton").click(function() {
		// on some browser, this simulated click won't have any effect if s
		$("#hiddenForm").show();
		$("#theFile").click();
		$("#hiddenForm").hide();
	});

	$("#theFile").change(function() {
		$("#pleaseWaitDialog").dialog("open");
		$("#hiddenUploadCvForm").submit();
	});

}