// the user may only reply or forward a mesage after he has at least clicked on one (otherwise, there is no mesage to reply/forward to))
var messageReactionEnabled = false;

// init ran at page load
function init() {
	
	$("#replyToLink").click(replyTo);
	$("#forwardLink").click(forwardMessage);
	
	loadPage(0);
	
	
	initConfirmDeleteDialog();
}

function loadPage(pageNumber) {
	
	
	
	$.post(
			loadOnePageAction(
					{pageNumber: pageNumber}
					), 

			function(messagePage) {

				removeAllDisplayedMessages();
				
				if (messagePage.messages.length > 0) {
					for (oneKey in messagePage.messages) {
						if (oneKey != undefined && oneKey != "") {
							addOneDisplayedMessage(messagePage.messages[oneKey]);
						}
					}
				}
				
			}
		);
	
}











function removeAllDisplayedMessages() {
	
	
	
}



function addOneDisplayedMessage(addedMessage) {
	
	var oneMessage = $("#hiddenInboxRowTemplate").clone().show();
	
	if (addedMessage.fromUser.isProfileActive) {
		oneMessage.find(".messageRowFrom a").text(addedMessage.fromUser.userName);
		oneMessage.find(".messageRowFrom a").attr("href", "/profile/public?vuser=" + addedMessage.fromUser.userName);
	} else {
		
	}
	
	oneMessage.find(".messageRowSubject span").text(addedMessage.subject);

	oneMessage.find(".messageRowCreationTime span").text(addedMessage.creationDate);
	
	
	if (addedMessage.read) {
		oneMessage.find("td").addClass("inboxRow");
	} else {
		oneMessage.find("td").addClass("inboxRowUnread");
	}
	
	oneMessage.insertBefore($("#topMessageListLastRow"));
	
}




// init ran at each message navigation
function onRefreshMessageList(event) {

	try {
		if (event.status == "success") {
			registerClickOnInboxRow();
			prepareNextLink();
			preparePreviousLink();
			registerClickOnInboxCheckbox();
			$("#messageDeleteSelected").click(function() {
				var numberDeleted = $(".inboxOutboxDeleteCheckbox > :checkbox").filter(":checked").size();
				if (numberDeleted > 0) {
					$("#confirmDeleteInboxMessages").dialog("open");
				}
			});
			$("#selectAllNone").click(selectAllNone);
			eraseDisplayedMessageContent();
			$("#confirmDeleteInboxMessages").dialog("close");
		}
	} catch (e) {
		alert(e);
	}
}

function initConfirmDeleteDialog(okLabel, cancelLabel) {
	$("#confirmDeleteInboxMessages").dialog({
		autoOpen : false,
		modal : true,
		"buttons" : [ {
			text : okLabel,
			click : confirmDeleteMessages
		}, {
			text : cancelLabel,
			click : function() {
				$(this).dialog("close");
			}
		}

		]
	});

}

function replyTo() {
	if (messageReactionEnabled) {
		$('#hiddenReactionForm\\:hiddenFrom').val($('#messageDetailFrom').text());
		$('#hiddenReactionForm\\:hiddenSubject').val($('#messageDetailSubject').text());
		$('#hiddenReactionForm\\:hiddenDate').val($('#messageDetailDate').text());
		$('#hiddenReactionForm\\:hiddenContent').val($('#messageContent').val());
		$('#hiddenReactionForm\\:hiddenReplyToLink').trigger("click");
	}
}

function forwardMessage() {
	if (messageReactionEnabled) {
		$('#hiddenReactionForm\\:hiddenFrom').val($('#messageDetailFrom').text());
		$('#hiddenReactionForm\\:hiddenSubject').val($('#messageDetailSubject').text());
		$('#hiddenReactionForm\\:hiddenDate').val($('#messageDetailDate').text());
		$('#hiddenReactionForm\\:hiddenContent').val($('#messageContent').val());
		$('#hiddenReactionForm\\:hiddenForwardLink').trigger("click");
	}
}

function registerClickOnInboxRow() {
	$('.inboxRow').click(function(event) {
		updateDisplayedMessage(event.target.id.substring(2), false);
	});

	$('.inboxRowUnread').click(function(event) {
		// id of the hidden field containing the message content

		// if the user clicked on the link, we do not want to view the message
		// nor to mark it as read
		var clickedId5 = event.target.id.substring(0, 4);
		if (clickedId5 != "link") {
			updateDisplayedMessage(event.target.id.substring(2), true);
		}
	});
}

function registerClickOnInboxCheckbox() {
	$(".inboxOutboxDeleteCheckbox > :checkbox").click(refreshDeleteSelectedLinkState);
}

function refreshDeleteSelectedLinkState() {
	var numberSelected = $(".inboxOutboxDeleteCheckbox > :checkbox").filter(":checked").size();
	if (numberSelected > 0) {
		$("#messageDeleteSelected").removeClass("messagesReactionLinkDisabled").addClass("messagesReactionLinkEnabled");
	} else {
		$("#messageDeleteSelected").removeClass("messagesReactionLinkEnabled").addClass("messagesReactionLinkDisabled");
	}
}

function confirmDeleteMessages() {
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

	$("#hiddenMarkMessagesAsDeletedForm\\:messageIds").val(deletedIndices);
	$("#hiddenMarkMessagesAsDeletedForm\\:hiddenLink").click();
}

function selectAllNone() {
	$(".inboxOutboxDeleteCheckbox > :checkbox").attr("checked", $("#selectAllNone").attr("checked"));
	refreshDeleteSelectedLinkState();
}

function updateDisplayedMessage(messageIndex, markAsUnread) {
	var contentContainerId = "c_" + messageIndex;

	// id of the 3 text items displyaing this message
	var fromUserElementId = "f_" + messageIndex;
	var subjectElementId = "s_" + messageIndex;
	var dateElementId = "d_" + messageIndex;
	var fromUserElement = $("body").find('#' + fromUserElementId);
	var subjectElement = $("body").find('#' + subjectElementId);
	var dateElement = $("body").find('#' + dateElementId);

	// displays the message content
	var contentContainer = $("body").find('#' + contentContainerId);
	$("#messageContent").val(contentContainer.html());
	$('#messageDetailFrom').html(fromUserElement.children(":first").html());
	$('#messageDetailFrom').attr("href", fromUserElement.children(":first").attr("href"));

	$('#messageDetailSubject').html(subjectElement.children(":first").html());
	$('#messageDetailDate').html(dateElement.children(":first").html());

	if (!messageReactionEnabled) {
		$('#replyToLink').removeClass("messagesReactionLinkDisabled").addClass("messagesReactionLinkEnabled");
		$('#forwardLink').removeClass("messagesReactionLinkDisabled").addClass("messagesReactionLinkEnabled");
	}
	messageReactionEnabled = true;

	if (markAsUnread) {
		// message is now read: updating class
		fromUserElement.removeClass('inboxRowUnread').addClass("inboxRow");
		fromUserElement.children(":first").removeClass('inboxRowUnread').addClass("inboxRow");
		subjectElement.removeClass('inboxRowUnread').addClass("inboxRow");
		subjectElement.children(":first").removeClass('inboxRowUnread').addClass("inboxRow");
		dateElement.removeClass('inboxRowUnread').addClass("inboxRow");
		dateElement.children(":first").removeClass('inboxRowUnread').addClass("inboxRow");

		// message is now read: set the message id in the hidden form and
		// trigger submisstion to mark the message as read
		var messageIdId = "i_" + messageIndex;
		var messageIdEl = $("body").find('#' + messageIdId);
		$("#hiddenMarkMessagesAsReadForm\\:messageId").val(messageIdEl.text());
		$("#hiddenMarkMessagesAsReadForm\\:hiddenRefreshMessagesLink").trigger("click");
	}
}

function eraseDisplayedMessageContent() {
	$("#messageContent").val("");
	$('#messageDetailFrom').html("");
	$('#messageDetailFrom').attr("href", "#");

	$('#messageDetailSubject').html("");
	$('#messageDetailDate').html("");

	$('#replyToLink').removeClass("messagesReactionLinkEnabled").addClass("messagesReactionLinkDisabled");
	$('#forwardLink').removeClass("messagesReactionLinkEnabled").addClass("messagesReactionLinkDisabled");
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
