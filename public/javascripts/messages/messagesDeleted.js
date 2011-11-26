//the user may only reply or forward a mesage after he has at least clicked on one (otherwise, there is no mesage to reply/forward to))
var messageReactionEnabled = false;
var currentMessageId;

// init ran at page load
function init() {
	loadCurrentMessagePage();

	registerClickOnInboxRow();
	
	$("#replyToLink").click(replyTo);
	$("#forwardLink").click(forwardMessage);

	initNextPrevious();

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

	if (addedMessage.toUser.isProfileActive) {
		oneMessage.find(".messageRowTo a").text(addedMessage.toUser.userName);
		oneMessage.find(".messageRowTo a").attr("href", "/profile/public?vuser=" + addedMessage.toUser.userName);
	} else {

	}

	if (addedMessage.fromUser.isProfileActive) {
		oneMessage.find(".messageRowFrom a").text(addedMessage.fromUser.userName);
		oneMessage.find(".messageRowFrom a").attr("href", "/profile/public?vuser=" + addedMessage.fromUser.userName);
	} else {

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

	while (htmlElem.attr("id") != "messagesListTable") {
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

		$('#messageDetailTo').text(message.toUser.userName);
		$('#messageDetailTo').attr("href", "/profile/public?vuser=" + message.toUser.userName);

		if (!messageReactionEnabled) {
			$('#replyToLink').removeClass("messagesReactionLinkDisabled").addClass("messagesReactionLinkEnabled");
			$('#forwardLink').removeClass("messagesReactionLinkDisabled").addClass("messagesReactionLinkEnabled");
		}
		messageReactionEnabled = true;
		currentMessageId = message.id;

	}
}



/////////////////////////////////////
//reply , forward

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



////////////////////////////////////////////

///////////////////////////////////////
//next, previous page

function initNextPrevious() {

	$("#messagesPreviousLink").click(
			function() {
				if (currentPage > 0
						&& $("#messagesPreviousLink").hasClass(
								"messagesReactionLinkEnabled")) {
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
