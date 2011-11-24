var messageReactionEnabled = false;

function initOutbox(okLabel, cancelLabel) {
	$('#hiddenRefreshOutboxMessagesForm\\:hiddenRefreshMessagesLink').trigger('click');
	$("#forwardLink").click(forwardMessage);
	initConfirmDeleteOutboxDialog(okLabel, cancelLabel);
}

function onRefreshMessageOutboxList(event) {
	try {
		if (event.status == "success") {

			registerClickOnOutboxRow();
			prepareNextLinkOutbox();
			preparePreviousLinkOutbox();
			registerClickOnOutboxCheckbox();
			$("#selectAllNoneOutbox").click(selectAllNoneOutbox);
			$("#messageDeleteSelectedOutbox").click(function() {
				var numberDeleted = $(".inboxOutboxDeleteCheckbox > :checkbox").filter(":checked").size();
				if (numberDeleted > 0) {
					$("#confirmDeleteOutboxMessages").dialog("open");
				}

			});
			eraseDisplayedMessageContentOutbox();
			$("#confirmDeleteOutboxMessages").dialog("close");
		}
	} catch (e) {
		alert(e);
	}
}

function initConfirmDeleteOutboxDialog(okLabel, cancelLabel) {
	$("#confirmDeleteOutboxMessages").dialog({
		autoOpen : false,
		modal : true,
		"buttons" : [ {
			text : okLabel,
			click : confirmDeleteOutboxMessages
		}, {
			text : cancelLabel,
			click : function() {
				$(this).dialog("close");
			}
		} ]
	});

}

// triggers the display of the message conent in the bottom panel
function registerClickOnOutboxRow() {
	$('.outboxRow').click(function(event) {

		var messageIndex = event.target.id.substring(2);

		// id of the 3 text items displyaing this message
		var contentContainerId = "c_" + messageIndex;
		var toUserElementId = "t_" + messageIndex;
		var subjectElementId = "s_" + messageIndex;
		var dateElementId = "d_" + messageIndex;

		var toUserElement = $("body").find('#' + toUserElementId);
		var subjectElement = $("body").find('#' + subjectElementId);
		var dateElement = $("body").find('#' + dateElementId);
		var contentContainer = $("body").find('#' + contentContainerId);

		// displays the message content
		$("#outboxMessageContent").val(contentContainer.html());
		$('#messageDetailTo').html(toUserElement.children(":first").html());
		$('#messageDetailTo').attr("href", toUserElement.children(":first").attr("href"));
		$('#messageDetailSubject').html(subjectElement.children(":first").html());
		$('#messageDetailDate').html(dateElement.children(":first").html());

		if (!messageReactionEnabled) {
			$('#forwardLink').removeClass("messagesReactionLinkDisabled").addClass("messagesReactionLinkEnabled");
		}
		messageReactionEnabled = true;

	});
}

function forwardMessage() {
	if (messageReactionEnabled) {
		$('#hiddenReactionForm\\:hiddenFrom').val($('#messageDetailFrom').text());
		$('#hiddenReactionForm\\:hiddenSubject').val($('#messageDetailSubject').text());
		$('#hiddenReactionForm\\:hiddenDate').val($('#messageDetailDate').text());
		$('#hiddenReactionForm\\:hiddenContent').val($('#outboxMessageContent').val());
		$('#hiddenReactionForm\\:hiddenForwardLink').trigger("click");
	}
}

function eraseDisplayedMessageContentOutbox() {
	$("#outboxMessageContent").val("");
	$('#messageDetailFrom').html("");
	$('#messageDetailFrom').attr("href", "#");

	$('#messageDetailSubject').html("");
	$('#messageDetailDate').html("");

	$('#forwardLink').removeClass("messagesReactionLinkEnabled").addClass("messagesReactionLinkDisabled");
}

function prepareNextLinkOutbox() {
	nextPageExists = $("#isNextOutboxPageLinkActive").text();
	if (nextPageExists == "true") {
		$("#messagesNextLinkOutbox").removeClass("messagesReactionLinkDisabled").addClass("messagesReactionLinkEnabled");
		$("#messagesNextLinkOutbox").click(function() {
			$("#hiddenNextOutboxPageForm\\:hiddenLink").click();
		});
	} else {
		$("#messagesNextLinkOutbox").removeClass("messagesReactionLinkEnabled").addClass("messagesReactionLinkDisabled");
	}
}

function preparePreviousLinkOutbox() {
	previousPageExists = $("#isPreviousOutboxPageLinkActive").text();
	if (previousPageExists == "true") {
		$("#messagesPreviousLinkOutbox").removeClass("messagesReactionLinkDisabled").addClass("messagesReactionLinkEnabled");
		$("#messagesPreviousLinkOutbox").click(function() {
			$("#hiddenPreviousOutboxPageForm\\:hiddenLink").click();
		});
	} else {
		$("#messagesPreviousLink").removeClass("messagesReactionLinkEnabled").addClass("messagesReactionLinkDisabled");
	}
}

function registerClickOnOutboxCheckbox() {
	$(".inboxOutboxDeleteCheckbox > :checkbox").click(refreshDeleteSelectedLinkState);
}

function refreshDeleteSelectedLinkState() {
	var numberSelected = $(".inboxOutboxDeleteCheckbox > :checkbox").filter(":checked").size();
	if (numberSelected > 0) {
		$("#messageDeleteSelectedOutbox").removeClass("messagesReactionLinkDisabled").addClass("messagesReactionLinkEnabled");
	} else {
		$("#messageDeleteSelectedOutbox").removeClass("messagesReactionLinkEnabled").addClass("messagesReactionLinkDisabled");
	}
}

function selectAllNoneOutbox() {
	$(".inboxOutboxDeleteCheckbox > :checkbox").attr("checked", $("#selectAllNoneOutbox").attr("checked"));
	refreshDeleteSelectedLinkState();
}

function confirmDeleteOutboxMessages() {
	var numberSelected = $(".inboxOutboxDeleteCheckbox > :checkbox").filter(":checked").size();

	var deletedIndices = "";
	var first = true;

	if (numberSelected > 0) {
		var selectedCk = $(".inboxOutboxDeleteCheckbox > :checkbox").filter(":checked").each(function() {
			if (!first) {
				deletedIndices += ",";
			}
			deletedIndices += $(this).attr("id").substring(2);
			first = false;
		});
	}

	$("#hiddenMarkMessagesAsDeletedOutboxForm\\:messageIds").val(deletedIndices);
	$("#hiddenMarkMessagesAsDeletedOutboxForm\\:hiddenLink").click();
}
