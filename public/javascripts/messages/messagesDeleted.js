// init ran at page load
function initDeleted(okLabel, cancelLabel) {
	$('#hiddenRefreshMessagesForm\\:hiddenLink').trigger('click');
	initConfirmUndeleteDialog(okLabel, cancelLabel);

}

function onRefreshDeletedMessageList(event) {
	try {
		if (event.status == "success") {
			$("#selectAllNone").click(selectAllNone);
			registerClickOnDeletedCheckbox();
			
			$("#undeleteSelected").click(function() {
				var numberUndeleted = $(".inboxOutboxDeleteCheckbox > :checkbox").filter(":checked").size();
				if (numberUndeleted > 0) {
					$("#confirmUndeleteMessages").dialog("open");
				}
			});

			prepareNextLink();
			preparePreviousLink();
			
			registerClickOnDeletedRow();
			
			$("#confirmUndeleteMessages").dialog("close");
			
			eraseDisplayedMessageContentDeleted();
			
		}
	} catch (e) {
		alert(e);
	}

}

function registerClickOnDeletedCheckbox() {
	$(".inboxOutboxDeleteCheckbox > :checkbox").click(refreshUndeleteSelectedLinkState);

}

function refreshUndeleteSelectedLinkState() {
	var numberSelected = $(".inboxOutboxDeleteCheckbox > :checkbox").filter(":checked").size();
	if (numberSelected > 0) {
		$("#undeleteSelected").removeClass("messagesReactionLinkDisabled").addClass("messagesReactionLinkEnabled");
	} else {
		$("#undeleteSelected").removeClass("messagesReactionLinkEnabled").addClass("messagesReactionLinkDisabled");
	}
}



function initConfirmUndeleteDialog(okLabel, cancelLabel) {
	$("#confirmUndeleteMessages").dialog({
		autoOpen : false,
		modal : true,
		"buttons" : [ {
			text : okLabel,
			click : confirmUndeleteMessages
		}, {
			text : cancelLabel,
			click : function() {
				$(this).dialog("close");
			}
		} ]
	});
}

function confirmUndeleteMessages() {
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

	$("#hiddenUndeleteForm\\:messageIds").val(deletedIndices);
	$("#hiddenUndeleteForm\\:hiddenLink").click();
}


function selectAllNone() {
	$(".inboxOutboxDeleteCheckbox > :checkbox").attr("checked", $("#selectAllNone").attr("checked"));
	refreshUndeleteSelectedLinkState();
}

function prepareNextLink() {
	nextPageExists = $("#isNextPageLinkActive").text();
	if (nextPageExists == "true") {
		$("#messagesNextLink").removeClass("messagesReactionLinkDisabled").addClass("messagesReactionLinkEnabled");
		$("#messagesNextLink").click(function() {
			$("#hiddenNextPageForm\\:hiddenLink").click();
		});
	} else {
		$("#messagesNextLink").removeClass("messagesReactionLinkEnabled").addClass("messagesReactionLinkDisabled");
	}
}

function preparePreviousLink() {
	previousPageExists = $("#isPreviousPageLinkActive").text();
	if (previousPageExists == "true") {
		$("#messagesPreviousLink").removeClass("messagesReactionLinkDisabled").addClass("messagesReactionLinkEnabled");
		$("#messagesPreviousLink").click(function() {
			$("#hiddenPreviousPageForm\\:hiddenLink").click();
		});
	} else {
		$("#messagesPreviousLink").removeClass("messagesReactionLinkEnabled").addClass("messagesReactionLinkDisabled");
	}
}

function registerClickOnDeletedRow() {
	
	$('.outboxRow, .inboxRow, .inboxRowUnread').click(function(event) {

		var messageIndex = event.target.id.substring(2);

		// id of the 3 text items displyaing this message
		var contentContainerId = "c_" + messageIndex;
		var fromUserElementId = "f_" + messageIndex;
		var toUserElementId = "t_" + messageIndex;
		var subjectElementId = "s_" + messageIndex;
		var dateElementId = "d_" + messageIndex;

		var toUserElement = $("body").find('#' + toUserElementId);
		var fromUserElement = $("body").find('#' + fromUserElementId);
		var subjectElement = $("body").find('#' + subjectElementId);
		var dateElement = $("body").find('#' + dateElementId);
		var contentContainer = $("body").find('#' + contentContainerId);

		// displays the message content
		$("#deletedMessageContent").val(contentContainer.html());
		$('#messageDetailTo').html(toUserElement.children(":first").html());
		$('#messageDetailTo').attr("href", toUserElement.children(":first").attr("href"));
		$('#messageDetailFrom').html(fromUserElement.children(":first").html());
		$('#messageDetailFrom').attr("href", fromUserElement.children(":first").attr("href"));

		$('#messageDetailSubject').html(subjectElement.children(":first").html());
		$('#messageDetailDate').html(dateElement.children(":first").html());

	});	
}




function eraseDisplayedMessageContentDeleted() {
	$("#deletedMessageContent").val("");
	$('#messageDetailTo').html("");
	$('#messageDetailTo').attr("href", "");
	$('#messageDetailFrom').html("");
	$('#messageDetailFrom').attr("href", "");
	
	$('#messageDetailSubject').html("");
	$('#messageDetailDate').html("");
}
