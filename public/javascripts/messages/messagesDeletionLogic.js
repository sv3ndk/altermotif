
///////////////////////////////////////
// deletion logic


function initDeletionLogic() {
	initConfirmDeleteDialog();
	registerClickOnInboxCheckbox();
}


function isAtLeastOneCheckBoxSelected() {
	return getAllNormalCheckBoxes().filter(":checked").size() > 0;
}

function areAllCheckBoxSelected() {
	return getAllNormalCheckBoxes().filter(":not(:checked)").size() == 0;
}

// normal means not the "master" one and not the hiiden one which is used to generate the message at load time
function getAllNormalCheckBoxes() {
	return $("#messagesListTable :checkbox").filter(":not(#masterCheckbox)").filter(":not(#hiddenRowTemplate :checkbox)");
}



function registerClickOnInboxCheckbox() {
	$("#messagesListTable").on("change", ":checkbox:not(#masterCheckbox)", function(target) {
			
			// as soon as one checkbox is unselected, the master check box should also be deselected 
			if ($(target).attr("checked") == undefined) {
				$("#masterCheckbox").removeAttr("checked");
			}
			
			// as soon as they are all selected, the master checkbox is selected as well
			if (areAllCheckBoxSelected()) {
				$("#masterCheckbox").attr("checked", "checked");
			}
			
			refreshDeleteSelectedLinkState();
		}); 
			
			
	
	// click on the top level checkbox: 
	$("#masterCheckbox").click(function()  {
		if ($("#masterCheckbox").attr("checked") == undefined) {
			getAllNormalCheckBoxes().removeAttr("checked");
		} else {
			getAllNormalCheckBoxes().attr("checked", "checked");
		}
		refreshDeleteSelectedLinkState();
	});
	
}







function refreshDeleteSelectedLinkState() {
	if (isAtLeastOneCheckBoxSelected()) {
		$("#messageDeleteSelected").removeClass("messagesReactionLinkDisabled").addClass("messagesReactionLinkEnabled");
	} else {
		$("#messageDeleteSelected").removeClass("messagesReactionLinkEnabled").addClass("messagesReactionLinkDisabled");
	}
}


function initConfirmDeleteDialog() {
	$("#confirmDeleteMessages").dialog({
		autoOpen : false,
		modal : true,
		"buttons" : [ {
			text : okLabelValue,
			click : doDeleteSelectedMessages
		}, {
			text : cancelLabelValue,
			click : function() {
				$(this).dialog("close");
			}
		}]
	});
	
	$("#messageDeleteSelected").click(function() {
		if (isAtLeastOneCheckBoxSelected()) {
			$("#confirmDeleteMessages").dialog("open");
		}
	});
	
}


function doDeleteSelectedMessages() {
	
	var allSelectedCheckBoxes = getAllNormalCheckBoxes().filter(":checked");
	
	var deletedMessageIds = [];

	if (allSelectedCheckBoxes != undefined && allSelectedCheckBoxes.length  > 0) {
		allSelectedCheckBoxes.each(function() {
			var deletedMessage = $(this).parent().parent().data("fullMessage");
			deletedMessageIds.push(deletedMessage.id);
		});
		
		$.post(
				deleteMessageAction({messageIds: JSON.stringify(deletedMessageIds)}), 
				function(response) {
					loadCurrentMessagePage();
					$("#confirmDeleteMessages").dialog("close");
				}
			);
	} else {
		$("#confirmDeleteMessages").dialog("close");
	}

}