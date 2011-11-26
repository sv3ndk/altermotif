// the user may only reply or forward a mesage after he has at least clicked on one (otherwise, there is no mesage to reply/forward to))
var messageReactionEnabled = false;
var currentMessageId;


// init ran at page load
function init() {
	
	$("#replyToLink").click(replyTo);
	$("#forwardLink").click(forwardMessage);
	
	loadCurrentMessagePage();

	registerClickOnInboxRow();
	
	registerClickOnInboxCheckbox();
	
	initConfirmDeleteDialog();
	
	initNextPrevious();
}


////////////////////////////////////
////////////////////////////////////


function loadCurrentMessagePage() {
	$.post(
			loadOnePageAction(
					{pageNumber: currentPage}
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
				
				
			}
		);
}



function removeAllDisplayedMessages() {
	$("#messagesListTable tr.inboxRowTr").filter(":not(#hiddenInboxRowTemplate)").remove();
}


function addOneDisplayedMessage(addedMessage) {
	
	var oneMessage = $("#hiddenInboxRowTemplate").clone().show();
	
	oneMessage.removeAttr("id");
	
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

// when the user clicks on a message, we show the content
function registerClickOnInboxRow() {
	$("#messagesListTable").on("click", '.inboxRow, .inboxRowUnread', function(event) {
		updateDisplayedMessage($(event.target));
	});
}

function updateDisplayedMessage(eventTarget) {
	
	// if we clicked on the user name link or on the checkbox: no need to refresh the message content
	if (eventTarget.hasClass("dabLink")  || eventTarget.attr("type") == "checkbox") {
		return ;
	}

	var htmlElem = eventTarget;
	var message;
	var markMessageAsUnread;
	
	while (htmlElem.attr("id") != "messagesListTable") {
		if (htmlElem.hasClass("inboxRowUnread")) {
			markMessageAsUnread = true;
		}
		message = htmlElem.data("fullMessage");
		
		// breaking now makes sure htmlElem points to the table row, whatever the user clicked
		if (message != undefined) {
			break;
		}
		
		htmlElem = htmlElem.parent();
	}
	
	
	if (message != undefined ) {
		
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
			$.post(
					markAsReadAction({messageId: message.id}), 
					function(response) {
						// NOP
					}
				);
		}
	
	}
}


/////////////////////////////////////
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



////////////////////////////////////////////
// show message content when clicking on top table


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


///////////////////////////////////////
// next, previous page

function initNextPrevious() {
	
	$("#messagesPreviousLink").click(function () {
		if (currentPage > 0 && $("#messagesPreviousLink").hasClass("messagesReactionLinkEnabled")) {
			currentPage--;
			loadCurrentMessagePage();
		}
	});

	$("#messagesNextLink").click(function () {
		if ( $("#messagesNextLink").hasClass("messagesReactionLinkEnabled")) {
			currentPage++;
			loadCurrentMessagePage();
		}
	});

	
}







///////////////////////////////////////
// deletion logic


function isAtLeastOneCheckBoxSelected() {
	return getAllNormalCheckBoxes().filter(":checked").size() > 0;
}

function areAllCheckBoxSelected() {
	return getAllNormalCheckBoxes().filter(":not(:checked)").size() == 0;
}

// normal means not the "master" one and not the hiiden one which is used to generate the message at load time
function getAllNormalCheckBoxes() {
	return $("#messagesListTable :checkbox").filter(":not(#masterCheckbox)").filter(":not(#hiddenInboxRowTemplate :checkbox)");
}


function refreshDeleteSelectedLinkState() {
	if (isAtLeastOneCheckBoxSelected()) {
		$("#messageDeleteSelected").removeClass("messagesReactionLinkDisabled").addClass("messagesReactionLinkEnabled");
	} else {
		$("#messageDeleteSelected").removeClass("messagesReactionLinkEnabled").addClass("messagesReactionLinkDisabled");
	}
}


function initConfirmDeleteDialog() {
	$("#confirmDeleteInboxMessages").dialog({
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
			$("#confirmDeleteInboxMessages").dialog("open");
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
				deleteInboxMessageAction({messageIds: JSON.stringify(deletedMessageIds)}), 
				function(response) {
					loadCurrentMessagePage();
					$("#confirmDeleteInboxMessages").dialog("close");
				}
			);
	} else {
		$("#confirmDeleteInboxMessages").dialog("close");
	}

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
