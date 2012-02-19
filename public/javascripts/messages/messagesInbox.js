// the user may only reply or forward a mesage after he has at least clicked on one (otherwise, there is no mesage to reply/forward to))
var messageReactionEnabled = false;
var currentMessageId;

$(document).ready(function() {
	init();
});


// init ran at page load
function init() {

	loadCurrentMessagePage();

	registerClickOnInboxRow();

	$("#replyToLink").click(replyTo);
	$("#forwardLink").click(forwardMessage);

	initNextPrevious();

	// this is present in messagesDeletionLogic.js
	initDeletionLogic();
}

// //////////////////////////////////
// //////////////////////////////////

function loadCurrentMessagePage() {
	$.post(loadOnePageAction({
		pageNumber : currentPage
	}),

	function(messagePage) {

		removeAllDisplayedMessages();

		if (messagePage.messages.length > 0) {
			for (oneKey in messagePage.messages) {
				if (oneKey != undefined && oneKey != "") {
					addOneDisplayedMessage(messagePage.messages[oneKey]);
				}
			}
		}

		$("#masterCheckbox").removeAttr("checked");

		if (messagePage.previousPageExists) {
			$("#messagesPreviousLink").addClass("messagesReactionLinkEnabled").removeClass("messagesReactionLinkDisabled");
		} else {
			$("#messagesPreviousLink").addClass("messagesReactionLinkDisabled").removeClass("messagesReactionLinkEnabled");
		}

		if (messagePage.nextPageExists) {
			$("#messagesNextLink").addClass("messagesReactionLinkEnabled").removeClass("messagesReactionLinkDisabled");
		} else {
			$("#messagesNextLink").addClass("messagesReactionLinkDisabled").removeClass("messagesReactionLinkEnabled");
		}

	});
}

function removeAllDisplayedMessages() {
	$("#messagesListTable tr.inboxRowTr").filter(":not(#hiddenRowTemplate)").remove();
}

function addOneDisplayedMessage(addedMessage) {

	var oneMessage = $("#hiddenRowTemplate").clone().show();

	oneMessage.removeAttr("id");

	if (addedMessage.fromUser.isProfileActive) {
		oneMessage.find(".messageRowFrom a.dabLink").text(addedMessage.fromUser.userName);
		oneMessage.find(".messageRowFrom a.dabLink").attr("href", "/profile/" + addedMessage.fromUser.userName + "/public");
	} else {
		oneMessage.find(".messageRowFrom span.dabLinkDisabled").text(addedMessage.fromUser.userName);
	}

	oneMessage.find(".messageRowSubject span").text(addedMessage.subject);
	oneMessage.find(".messageRowCreationTime span").text(addedMessage.creationDate);
	oneMessage.data("fullMessage", addedMessage);

	if (addedMessage.read) {
		oneMessage.find("td").addClass("inboxRow");
	} else {
		oneMessage.find("td").addClass("inboxRowUnread");
	}

	oneMessage.insertBefore($("#topMessageListLastRow"));

}

// //////////////////////////////////////////
// show message content when clicking on top table

function registerClickOnInboxRow() {
	$("#messagesListTable").on("click", '.inboxRow, .inboxRowUnread', function(event) {
		updateDisplayedMessage($(event.target));
	});
}

function updateDisplayedMessage(eventTarget) {

	// if we clicked on the user name link or on the checkbox: no need to
	// refresh the message content
	if (eventTarget.hasClass("dabLink") || eventTarget.attr("type") == "checkbox") {
		return;
	}

	var htmlElem = eventTarget;
	var message;
	var markMessageAsUnread;

	while (htmlElem.attr("id") != "messagesListTable") {
		if (htmlElem.hasClass("inboxRowUnread")) {
			markMessageAsUnread = true;
		}
		message = htmlElem.data("fullMessage");

		// breaking now makes sure htmlElem points to the table row, whatever
		// the user clicked
		if (message != undefined) {
			break;
		}

		htmlElem = htmlElem.parent();
	}

	if (message != undefined) {

		$("#messageContent").val(message.content);
		$('#messageDetailSubject').text(message.subject);
		$('#messageDetailDate').text(message.creationDate);

		$('#messageDetailFrom').text(message.fromUser.userName);
		$('#messageDetailFrom').attr("href", "/profile/public?vuser=" + message.fromUser.userName);

		if (!messageReactionEnabled) {
			$('#replyToLink').removeClass("messagesReactionLinkDisabled").addClass("messagesReactionLinkEnabled");
			$('#forwardLink').removeClass("messagesReactionLinkDisabled").addClass("messagesReactionLinkEnabled");
		}
		messageReactionEnabled = true;
		currentMessageId = message.id;

		if (markMessageAsUnread) {

			htmlElem.find("td").removeClass("inboxRowUnread").addClass("inboxRow");
			$.post(markAsReadAction({
				messageId : message.id
			}), function(response) {
				// NOP
			});
		}

	}
}

// ///////////////////////////////////
// reply , forward

function replyTo() {
	if (messageReactionEnabled && currentMessageId != undefined) {
		$("#hiddenReplyToForm input.hiddenSubmit").val(currentMessageId);
		$("#hiddenReplyToForm form").submit();
	}
}

function forwardMessage() {
	if (messageReactionEnabled) {
		$("#hiddenForwardForm input.hiddenSubmit").val(currentMessageId);
		$("#hiddenForwardForm form").submit();
	}
}

// /////////////////////////////////////
// next, previous page

function initNextPrevious() {

	$("#messagesPreviousLink").click(function() {
		if (currentPage > 0 && $("#messagesPreviousLink").hasClass("messagesReactionLinkEnabled")) {
			currentPage--;
			loadCurrentMessagePage();
		}
	});

	$("#messagesNextLink").click(function() {
		if ($("#messagesNextLink").hasClass("messagesReactionLinkEnabled")) {
			currentPage++;
			loadCurrentMessagePage();
		}
	});
}
