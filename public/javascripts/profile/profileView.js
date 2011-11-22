var okLabelValue;
var cancelLabelValue;
var visitedProfileId;

function init(okLabel, cancelLabel, isProfileActive) {

	okLabelValue = okLabel;
	cancelLabelValue = cancelLabel;
	visitedProfileId = $("#vuser").text();

	initLeaveAReferenceForm(okLabel, cancelLabel);

	// contact invitation mechanisms
	initContactMechanics();

	initYourProfileIsInactiveNotification(isProfileActive);
	
	insertDefaultPhotosWhereNeeded();
	
	updateAllTexts();

	// this cannot be called inside updateAllText(), becasuse it is based on data in the page that are not reloaded by Ajax
	updateContactAddedToCOntactText($("#isAddToContactLinkVisible").text() == "true", $("#isAddedToContactLinkVisible").text() == "true");

	
}

function updateAllTexts() {
	updateReceivedReferencesText();
	updateSentReferencesText();
	updateContactText();
}

function initYourProfileIsInactiveNotification(isProfileActive) {

	$("#yourProfileIsInactiveNotificationContainer").notify();

	if (isProfileActive != "true") {
		setTimeout(function() {
			$("#yourProfileIsInactiveNotificationContainer").notify("create", {}, {
				expires : false,
				speed : 750
			});
		}, 500);
	}
}

function sendMessageToUser() {
	$("#hiddenContactUserForm\\:hiddenContactVisitedUserLink").click();
}


////////////////////////////////////////////////////////////////
// Leave a reference
////////////////////////////////////////////////////////////////


// this method is bound directly from 
function showLeaveAReferencePopup() {
	$('#leaveAReferencePopup').dialog('open');
}


var deletedReferenceId;
var referenceToUserId;

function showConfirmRemoveReferencePopup(deletedRefId, referenceToUser) {
	// saved here and set to the hidden form field when the dialog is open (see open event listener)
	deletedReferenceId = deletedRefId;
	referenceToUserId = referenceToUser;

	$('#confirmRemoveWrittenReferenceDialog').dialog('option', 'position', 'center');
	$('#confirmRemoveWrittenReferenceDialog').dialog('open');

}


function initLeaveAReferenceForm(okLabel, cancelLabel) {
	
	$('#hiddenLeaveAReferenceForm\\:hiddenleaveAReferenceVisistedProfileId').val(visitedProfileId);
	
	$('#profileLeaveAReferenceLink').click(function() {
		showLeaveAReferencePopup();
	});
	
	$("#leaveAReferencePopup").dialog({
		autoOpen : false,
		height : 305,
		width : 460,
		modal : true,
		open : function(event, ui) {
			$('#leaveAReferenceTextArea').val('');
		},
		"buttons" : [ {
			text : okLabel,
			click : function(event) {
				
				$.post(postAReferenceAction(
						{createdReferenceText: $('#leaveAReferenceTextArea').val(), vuser: visitedProfileId}), 
						function(data) {
							//
						}
				);
				
				setTimeout(function() {
					updateReceivedReferences();
				}, 340);
				
				setTimeout(function() {
					$("#leaveAReferencePopup").dialog("close");
				}, 350);
				
			}
		},

		{
			text : cancelLabel,
			click : function() {
				$(this).dialog("close");
			}
		}

		]
	});

	$("#confirmRemoveWrittenReferenceDialog").dialog({
		autoOpen : false,
		modal : true,
		position : 'top',
		"buttons" : [ {
			text : okLabel,
			click : function() {
				
				$.post(removeAReferenceAction(
						{deletedReferenceId :deletedReferenceId, vuser: referenceToUserId}), 
						function(data) {
							//
						}
				);
				setTimeout(function() {
					updateReceivedReferences();
					updateSentReferences();
				}, 340);

				// refresh written references here
				
				setTimeout(function() {
					$("#confirmRemoveWrittenReferenceDialog").dialog("close");
				}, 350);
			}
		},

		{
			text : cancelLabel,
			click : function() {
				$(this).dialog("close");
			}
		} ]
	});
}


// -----------------------------
// methods for updating the received references

function updateReceivedReferences() {
	var alreadyKnownRefs = [];
	
	$("#receivedReferenceContainer .hiddenRefid").each(function(index, element) {
		alreadyKnownRefs.push($(element).text());
	});
	
	var jsonListOfKnownRefs = JSON.stringify(alreadyKnownRefs);

	pollNewReceivedReferences(jsonListOfKnownRefs);
	pollRemovedReceivedReferences(jsonListOfKnownRefs);
}

function pollRemovedReceivedReferences(jsonListOfKnownRefs) {
	$.get(getRemovedReceivedReferencesJsonAction(
			{alreadyKnownRefs:jsonListOfKnownRefs, vuser:visitedProfileId}), 
				function(removedRefJson) {
					removeThoseReferencesOrContacts(removedRefJson)
				});

}

function pollNewReceivedReferences(jsonListOfKnownRefs) {
	
	$.get(pollNewReceivedReferencesAction(
			{alreadyKnownRefs:jsonListOfKnownRefs, vuser:visitedProfileId}), 
			function(htmlRef) {
				$("#receivedReferenceContainer").append(htmlRef);
				insertDefaultPhotosWhereNeeded();
				$("#receivedReferenceContainer div.hidden").slideDown();
				updateAllTexts();
			}
	);
}


// -----------------------------
// methods for updating the sent references

function updateSentReferences() {
	var alreadyKnownRefs = [];
	
	$("#writtendReferenceContainer .hiddenRefid").each(function(index, element) {
		alreadyKnownRefs.push($(element).text());
	});
	
	var jsonListOfKnownRefs = JSON.stringify(alreadyKnownRefs);
	
	pollRemovedSentReferences(jsonListOfKnownRefs);
	pollNewSentReferences(jsonListOfKnownRefs);
}

function pollRemovedSentReferences(jsonListOfKnownRefs) {
	$.get(getRemovedSentReferencesJsonAction(
			{alreadyKnownRefs:jsonListOfKnownRefs, vuser:visitedProfileId}), 
			function(removedRefJson) {
				removeThoseReferencesOrContacts(removedRefJson)
			});
}

function pollNewSentReferences(jsonListOfKnownRefs) {
	
	$.get(pollNewSentReferencesAction(
			{alreadyKnownRefs:jsonListOfKnownRefs, vuser:visitedProfileId}), 
			function(htmlRef) {
				$("#writtendReferenceContainer").append(htmlRef);
				insertDefaultPhotosWhereNeeded();
				$("#writtendReferenceContainer div.hidden").slideDown();
				updateAllTexts();
			}
	);
}


// -----------------------
// 
function removeThoseReferencesOrContacts(removedRefJson) {
	if (removedRefJson != undefined && removedRefJson.length != 0) {
		
		for (oneKey in removedRefJson) {
			if (oneKey != undefined && oneKey != "") {
				$("#" + removedRefJson[oneKey]).parent().slideUp("slow", function() {
					$(this).remove();
					updateAllTexts();
				});
			}
			
		}
	}
}


function updateReceivedReferencesText() {
	$('#profileReceivedReferencesLink').text($("#profileReceivedReferences").text() + ' (' + $("#receivedReferenceContainer div.profileReference").length  + ')');
}

function updateSentReferencesText() {
	$('#profileSentReferencesLink').text($("#profileWrittenReferences").text() + ' (' + $("#writtendReferenceContainer div.profileReference").length  + ')');
}

function updateContactText() {
	$('#profileContactsLink').text($("#profileContacts").text() + ' (' + $("#contactsContainer div.confirmedProfileContact").length  + ')');
	
	// deletes the "you have pending received contact requests" if necessary
	if ($("#contactsContainer div.pendingReceivedContact").length == 0) {
		$("#profilePendingInvitations").hide();
	}
	
}

function updateContactAddedToCOntactText(isAddToContactLinkVisible, isAddedToContactLinkVisible) {
	
	if (isAddToContactLinkVisible ) {
		$("#addToContact").show();
	} else {
		$("#addToContact").hide();
	}

	if (isAddedToContactLinkVisible ) {
		$("#cancelAddedToContact").show();
	} else {
		$("#cancelAddedToContact").hide();
	}
	
}


////////////////////////////////////////////////////////////////
// contacts
////////////////////////////////////////////////////////////////


function initContactMechanics() {

	$("#contactThisUser").click(sendMessageToUser);

	// initializing the photo gallery...
	$("a[rel='profilePhotos']").colorbox({
		transition : "none",
		width : "75%",
		height : "75%",
		photo : true
	});

	initAddToContactPopup();
	initCancelContactRequestPopup();
	initRejectContactRequestPopup();
	initAcceptContactRequestPopup();
	initRemoveContactPopup();

}

function initAddToContactPopup() {

	$("#addToContactLink").click(function() {
		$('#addToContactPopup textarea').val('');
		$("#addToContactPopup").dialog('open');
	});

	$("#addToContactPopup").dialog({
		autoOpen : false,
		modal : true,
		width : 400,
		height : 325,
		"buttons" : [ {
			text : okLabelValue,
			click : function() {
				
				$.post(addToMyContactsAction(
						{invitationText: $('#addToContactPopup textarea').val(), otherUser: visitedProfileId} ), 
						function(data) {
							//
						}
				);
				
				$(this).dialog("close");
				$("#addedToContactConfirmationPopup").dialog('open');
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

	$("#addedToContactConfirmationPopup").dialog({
		autoOpen : false,
		modal : true,
		"buttons" : [ {
			text : okLabelValue,
			click : function() {
				setTimeout(function() {
					updateAnyContact();
					$("#addedToContactConfirmationPopup").dialog("close");
				}, 350);
			}
		} ]
	});
}


var otherUsername;

function initCancelContactRequestPopup() {

	// this "cancel" link is visible when the user visits another user's profile and if he has sent a request to this user
	$("#cancelAddedToContact_visited_Link").click(function() {
		otherUsername = visitedProfileId;
		$("#cancelAddedToContactPopup").dialog('open');
	});

	// this "cancel" link is visible when the user visits his own profile: among the list of "pending requests you sent"
	$("span.cancelRequestedContact").click(function(event) {
		otherUsername = $(event.target).siblings(".hiddenUserName").text();
		$("#cancelAddedToContactPopup").dialog('open');
	});

	$("#cancelAddedToContactPopup").dialog({
		autoOpen : false,
		modal : true,
		"buttons" : [ {
			text : okLabelValue,
			click : function() {
				
				$.post(cancelContactRequestAction(
						{otherUser: otherUsername} ), 
						function(data) {
							//
						}
				);
				
				// closes the cancel popup in 350ms, so that most of the time
				setTimeout(function() {
					$("#cancelAddedToContactPopup").dialog("close");
					// TODO: refresh also the text "cancel request"
					updateAnyContact();
				}, 350);
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

function initRejectContactRequestPopup() {

	// this "reject" link is visible when the user visits his own profile: among the list of "pending requests you received"
	$("span.rejectRequestedContact").click(function(event) {
		otherUsername = $(event.target).siblings(".hiddenUserName").text();
		$("#rejectAddedToContactPopup").dialog('open');
	});

	$("#rejectAddedToContactPopup").dialog({
		autoOpen : false,
		modal : true,
		"buttons" : [ {
			text : okLabelValue,
			click : function() {
				
				$.post(rejectContactRequestAction(
						{otherUser: otherUsername} ), 
						function(data) {
							//
						}
				);
				// closes the cancel popup in 350ms, so that most of the time
				setTimeout(function() {
					$("#rejectAddedToContactPopup").dialog("close");
					updateAnyContact();
				}, 350);
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

function initRemoveContactPopup() {

	$(".profileContactLink img.deleteImageLink").click(function(event) {
		otherUsername = $(event.target).siblings(".hiddenUserName").text();
		$("#removeContactPopup").dialog('open');
	});

	$("#removeContactPopup").dialog({
		autoOpen : false,
		modal : true,
		"buttons" : [ {
			text : okLabelValue,
			click : function() {
				
				$.post(removeContactAction(
						{otherUser: otherUsername} ), 
						function(data) {
							//
						}
				)
				// closes the cancel popup in 350ms, so that most of the time
				setTimeout(function() {
					updateAnyContact();
					$("#removeContactPopup").dialog("close");
				}, 350);
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

function initAcceptContactRequestPopup() {

	// this "reject" link is visible when the user visits his own profile: among the list of "pending requests you received"
	$("span.acceptRequestedContact").click(function(event) {
		otherUsername = $(event.target).siblings(".hiddenUserName").text();
		$("#acceptAddedToContactPopup").dialog('open');
	});

	$("#acceptAddedToContactPopup").dialog({
		autoOpen : false,
		modal : true,
		"buttons" : [ {
			text : okLabelValue,
			click : function() {
				$.post(acceptContactRequestAction(
						{otherUser: otherUsername} ), 
						function(data) {
							//
						}
				)
				// closes the cancel popup in 350ms, so that most of the time
				setTimeout(function() {
					updateAnyContact();
					$("#acceptAddedToContactPopup").dialog("close");
				}, 350);
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

	initViewContactInvitationText();
}

function initViewContactInvitationText() {

	// this "reject" link is visible when the user visits his own profile: among the list of "pending requests you received"
	$("span.viewInvitationLink").click(function(event) {
		var invitationText = $(event.target).next().text();
		$("#contactInvitationPopup textarea").val(invitationText);
		$("#contactInvitationPopup").dialog('open');
	});

	$("#contactInvitationPopup").dialog({
		autoOpen : false,
		modal : true,
		width : 372,
		height : 214,
	});
}

/////////////////////////////////////
// mechanics for updating the contacts

function updateAnyContact() {
	pollRemovedAnyContacts();
	updateReceivedContactRequests();
	updateSentContactRequests();
	updateConfirmedContacts();
}


function pollRemovedAnyContacts() {
	$.get(getRemovedContactsJsonAction(
			{vuser: visitedProfileId, knownPendingReceivedIds: collectKnowPendingReceivedContactIds(), knownPendingSentIds: collectKnowPendingSentContactIds(), knownContactsIds: collectKnowConfirmedContactIds()} ), 
			function(responseJson) {
				removeThoseReferencesOrContacts(responseJson.removedIds);
				updateContactAddedToCOntactText(responseJson.isAddToContactLinkVisible, responseJson.isAddedToContactLinkVisible);
				
			}
	);
}


function updateReceivedContactRequests() {
	$.get(pollNewPendingReceivedContacsAction(
			{alreadyKnownContacts: collectKnowPendingReceivedContactIds(), vuser: visitedProfileId} ), 
			function(responseHtml) {
				$("#pendingReceivedContactRequestsContainer").append(responseHtml);
				insertDefaultPhotosWhereNeeded();
				$("#pendingReceivedContactRequestsContainer div.hidden").slideDown();
				updateAllTexts();
			}
	);
}

function updateSentContactRequests() {
	$.get(pollNewPendingReceivedContacsAction(
			{alreadyKnownContacts: collectKnowPendingSentContactIds(), vuser: visitedProfileId} ), 
			function(responseHtml) {
				$("#pendingSentContactRequestsContainer").append(responseHtml);
				insertDefaultPhotosWhereNeeded();
				$("#pendingSentContactRequestsContainer div.hidden").slideDown();
				updateAllTexts();
		
		}
	);
}

function updateConfirmedContacts() {
	$.get(pollNewContacsAction(
			{alreadyKnownContacts: collectKnowConfirmedContactIds(), vuser: visitedProfileId} ), 
			function(responseHtml) {
				$("#confirmedContactContainer").append(responseHtml);
				insertDefaultPhotosWhereNeeded();
				$("#confirmedContactContainer div.hidden").slideDown();
				updateAllTexts();
		
		}
	);
}


function collectKnowPendingReceivedContactIds() {
	var listOfKnowPendingReceivedContactIds = [];
	$("#contactsContainer .hiddenReceivedContactRequestId").each(function(index, element) {
		listOfKnowPendingReceivedContactIds.push($(element).text());
	});
	return listOfKnowPendingReceivedContactIds;
}

function collectKnowPendingSentContactIds() {
	var listOfKnowPendingSentContactIds = [];
	$("#contactsContainer .hiddenSentContactRequestId").each(function(index, element) {
		listOfKnowPendingSentContactIds.push($(element).text());
	});
	return listOfKnowPendingSentContactIds;
}

function collectKnowConfirmedContactIds() {
	var listOfKnowConfirmedContactIds = [];
	$("#contactsContainer .hiddenContactId").each(function(index, element) {
		listOfKnowConfirmedContactIds.push($(element).text());
	});
	return listOfKnowConfirmedContactIds;
}


/////////////////////////////////////
/////////////////////////////////////

function insertDefaultPhotosWhereNeeded() {
	var hiddenDefaultProfileImageSrc = $("#defaultProfileThumbImage").attr("src");

	// default profile photo
	$("img.userProfilePhoto[src='']").attr("src", $("#defaultProfileImage").attr("src"))
	
	// default thumbnail to all contacts
	$("img.profileContactThumb[src='']").each(function(index, element) {
		$(element).attr("src", hiddenDefaultProfileImageSrc);
	});
	
}

