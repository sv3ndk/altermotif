
function init() {
	initUncleanNavigationAwayWarning();
	initWarningSetProfileInactive();
}

/**
 * 
 */
function initUncleanNavigationAwayWarning() {
	$(':input').bind("change", function() {
		updateDirtyState(true);
	});

	$("#updatePrivacySettingsButton").click(submitPrivacySetting);
	$("#cancelPrivacySettingsButton").click(submitCancelPrivacySetting);

}

function updateDirtyState(on) {
	if (on) {
		window.onbeforeunload = unloadMessage;
		$("#visibleButtons input").removeAttr('disabled');
	} else {
		window.onbeforeunload = null;
		$("#visibleButtons input").attr("disabled", "disabled");
	}
}

// this is called when the user clicks on "update" => in that case we want to
// allow the navigation
function submitPrivacySetting() {
	updateDirtyState(false);
	$("#editForm form").submit();
}

function submitCancelPrivacySetting() {
	updateDirtyState(false);
	$("#cancelForm form").submit();
}

/**
 * @param on
 */
function unloadMessage(event) {

	e = event || window.event;

	// For IE and Firefox prior to version 4
	if (e) {
		e.returnValue = warningUnsavedChangesTextValue;
	}

	// For Safari
	return warningUnsavedChangesTextValue;

	// FF > 4 refuse to take this message into account...

}

function initWarningSetProfileInactive() {

	$("#editProfileContainer input.profileActiveCheckbx").click(
	function(event) {
		if ($(event.target).attr("checked") == null) {
			$("#confirmDeactivateProfilePopup").dialog("open");
		}
	});

	$("#confirmDeactivateProfilePopup").dialog({
		autoOpen : false,
		modal : true,
		"buttons" : [ {
			text : okLabelValue,
			click : function() {
				$(this).dialog("close");
			}
		},

		{
			text : cancelLabelTextValue,
			click : function() {
				$("#editProfileContainer input.profileActiveCheckbx").attr('checked', 'checked');
				$(this).dialog("close");
			}
		}

		]
	});

}
