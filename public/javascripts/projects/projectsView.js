function init() {

	// this is present in languages.js
	initAllPossibleLanguagesMap();

	// this is present in simpleActions.js
	initAskAndAct();
	
	insertLangugeName();
	initPhotoGallery();
	initApplicationMecanics();
	initParticipantsMecanics();
	refreshApplyLinkVisibility();
	
//	setInterval(refreshApplicationsAndPartipants, 2000);
	
}

function initPhotoGallery() {
	// photo gallery
	$("a[rel='profilePhotos']").colorbox({
		transition : "none",
		width : "75%",
		height : "75%",
		photo : true
	});
}


function insertLangugeName() {
	var languageCode = $(".languageCell").find("span.hidden").text();
	$(".languageCell span.langugeLabel").text(allPossibleLanguagesMap[languageCode]);
}



//////////////////////////////////
// project participantions

function initApplicationMecanics() {
	
	initViewParticipationMotivationText();
	initAcceptRejectAppliction();
	
	$('#applyToProjectLink').click(function() {
		$("#confirmApplyToProjectDialog").dialog("open");
		$("#confirmApplyToProjectDialog textarea").val("");
	});
	
	$("#confirmApplyToProjectDialog").dialog({
		autoOpen : false,
		width: 400,
		"buttons" : [ {
			text : okLabelValue,
			click : function(event) {
				
				$.post(applytoProject(
						{projectId: projectId, applicationText: $("#confirmApplyToProjectDialog textarea").val()}
						), 
						function(data) {
							//
						}
				);
				
				setTimeout(function() {
					isApplyLinkVisible = false;
					isCancelApplicationLinkVisible = true;
					refreshApplyLinkVisibility();
					$("#confirmApplyToProjectDialog").dialog("close");
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
	
	$('#cancelApplicationToProjectLink').click(function() {
		$("#confirmCancelApplicationToProjectDialog").dialog("open");
	});
	
	$("#confirmCancelApplicationToProjectDialog").dialog({
		autoOpen : false,
		width: 400,
		"buttons" : [ {
			text : okLabelValue,
			click : function(event) {
				
				$.post(cancelApplicationToProject(
						{projectId: projectId}
						), 
						function(data) {
							//
						}
				);
				
				setTimeout(function() {
					isApplyLinkVisible = true;
					isCancelApplicationLinkVisible = false;
					refreshApplyLinkVisibility();
					$("#confirmCancelApplicationToProjectDialog").dialog("close");
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
	
function refreshApplyLinkVisibility() {
	
	if (isApplyLinkVisible) {
		$("#applyToProjectLink").show()
	} else {
		$("#applyToProjectLink").hide()
	}
	
	if (isCancelApplicationLinkVisible) {
		$("#cancelApplicationToProjectText, #cancelApplicationToProjectLink").show()
	} else {
		$("#cancelApplicationToProjectText, #cancelApplicationToProjectLink").hide()
	}
}


function initViewParticipationMotivationText() {

	// this "reject" link is visible when the user visits his own profile: among the list of "pending requests you received"
	$("span.viewInvitationText").click(function(event) {
		var invitationText = $(event.target).next().text();
		$("#applicationMotivationPopup textarea").val(invitationText);
		$("#applicationMotivationPopup").dialog('open');
	});

	$("#applicationMotivationPopup").dialog({
		autoOpen : false,
		modal : true,
		width : 372,
		height : 214,
	});
}


//////////////////////////////////////////////////////////////////
// applications
	
var applicantId;
var applicationDiv;

function initAcceptRejectAppliction() {
	// this is defined in simpleActions.js
	askAndAct_On("#projectApplications", "span.acceptApplication", confirmAcceptApplicationText, whenUserConfirmsAcceptApplication);
	askAndAct_On("#projectApplications", "span.rejectApplication", confirmRejectApplicationText, whenUserConfirmsRejectApplication);
}

function whenUserConfirmsAcceptApplication() {
	applicantId = $(event.target).next().next().next().text();
	setConfirmationFunction(onConfirmAcceptApplication);
}
	
function onConfirmAcceptApplication() {
	$.post(acceptApplicationToProject(
			{projectId: projectId, applicant: applicantId}
		), 
		function(data) {
			setTimeout(function() {
				refreshApplicationsAndPartipants();
				closeConfirmationDialog();
			},400);
		}
	);
}
	
function whenUserConfirmsRejectApplication() {
	applicantId = $(event.target).next().next().next().text();
	setConfirmationFunction(onConfirmRejectApplication);
}	
				
function onConfirmRejectApplication() {
	$.post(rejectApplicationToProject(
		{projectId: projectId, applicant: applicantId}
		), 
		function(data) {
			refreshApplicationsAndPartipants();
			closeConfirmationDialog();
		}
	);
}


//////////////////////////////////////

var participantId;

function initParticipantsMecanics() {
	// this is defined in simpleActions.js
	askAndAct_On("#projectParticipants", "span.removeParticipant", confirmRemoveParticipantText, whenUserConfirmsRemoveParticipant);
	askAndAct_On("#projectParticipants", "span.leaveProject", confirmLeaveProjectText, whenUserConfirmsLeavesProject);
	askAndAct_On("#projectParticipants", "span.makeAdmin", confirmMakeAdminText, whenUserConfirmsMakeAdmin);
	askAndAct_On("#projectParticipants", "span.makeMember", confirmMakeMemberText, whenUserConfirmsMakeMember);
	askAndAct_On("#projectParticipants", "span.giveOwnership", confirmGiveOwnershipText, whenUserConfirmsGiveOwnership);
	askAndAct_On("#projectParticipants", "span.cancelGiveOwnership", confirmCancelGiveOwnershipText, whenUserConfirmsCancelGiveOwnership);
	askAndAct_On("#projectParticipants", "span.acceptOwnership", confirmAcceptOwnershipText, whenUserAcceptsOwnership);
	askAndAct_On("#projectParticipants", "span.refuseOwnership", confirmRefuseOwnershipText, whenUserRefusesOwnership);
}

// remove participant
function whenUserConfirmsRemoveParticipant(event) {
	recordActionedParticipantId();
	setConfirmationFunction(onConfirmRemoveParticipant);
}

function onConfirmRemoveParticipant() {
	$.post(removeParticipantOfProject(
			{projectId: projectId, participant: participantId}
		), 
		function(data) {
			setTimeout(function() {
				refreshApplicationsAndPartipants();
				closeConfirmationDialog();
			}, 400);
		}
	);
}


// leave project
function whenUserConfirmsLeavesProject() {
	setConfirmationFunction(onConfirmLeaveProject);
}

function onConfirmLeaveProject() {
	$.post(leaveProject(
			{projectId: projectId}
		), 
		function(data) {
			setTimeout(function() {
				refreshApplicationsAndPartipants();
				closeConfirmationDialog();
				
				// TODO: this is a bit ugly: we should check errors from server side rather than assuming the exit is successful	
				isApplyLinkVisible = true;
				isCancelApplicationLinkVisible = false;
				refreshApplyLinkVisibility();
			}, 400);
		}
	);
}

//make admin 
function whenUserConfirmsMakeAdmin() {
	recordActionedParticipantId();
	setConfirmationFunction(onConfirmMakeAdmin);
}

function onConfirmMakeAdmin() {
	$.post(makeAdmin(
			{projectId: projectId, participant:participantId}
	), 
	
		function(data) {
			setTimeout(function() {
				updateParticipantOneLineContainer(participantId);
				closeConfirmationDialog();
			}, 400);
		}
	);
}


// make member
function whenUserConfirmsMakeMember() {
	recordActionedParticipantId();
	setConfirmationFunction(onConfirmMakeMember);
}

function onConfirmMakeMember() {
	$.post(makeMember(
			{projectId: projectId, participant:participantId}
	), 
	
		function(data) {
			setTimeout(function() {
				updateParticipantOneLineContainer(participantId);
				closeConfirmationDialog();
			}, 400);
		}
	);
}

// transfer ownership
function whenUserConfirmsGiveOwnership() {
	recordActionedParticipantId();
	setConfirmationFunction(onConfirmGiveOwnership);
}

function onConfirmGiveOwnership() {
	$.post(giveOwnership(
			{projectId: projectId, participant:participantId}
	), 
		function(data) {
			setTimeout(function() {
				updateParticipantOneLineContainer(participantId);
				
				// also re-display the block for the previoulsy proposed ownership
				if (proposedOwnerId != undefined && proposedOwnerId != "") {
					updateParticipantOneLineContainer(proposedOwnerId);
				}
				
				proposedOwnerId = participantId;
				
				closeConfirmationDialog();
			}, 400);
		}
	);
}


// cancel transfer ownnership
function whenUserConfirmsCancelGiveOwnership() {
	recordActionedParticipantId();
	setConfirmationFunction(onConfirmCancelGiveOwnership);
}

function onConfirmCancelGiveOwnership() {
	$.post(cancelGiveOwnership(
			{projectId: projectId, participant:participantId}
	), 
		function(data) {
			setTimeout(function() {
				updateParticipantOneLineContainer(participantId);
				closeConfirmationDialog();
			}, 400);
		}
	);
}

// accept/refuse ownership transfer

function whenUserAcceptsOwnership() {
	recordActionedParticipantId();
	setConfirmationFunction(onAcceptOwnership);
}

function onAcceptOwnership() {
	// this must be a regular form POST: we must refresh the whole page because his rights have changed a lot now 
	
}


function whenUserRefusesOwnership() {
	recordActionedParticipantId();
	setConfirmationFunction(onRefuseOwnership);
}


function onRefuseOwnership() {
	$.post(refuseOwnership(
			{projectId: projectId, participant:participantId}
	), 
		function(data) {
			setTimeout(function() {
				updateParticipantOneLineContainer(participantId);
				closeConfirmationDialog();
			}, 400);
		}
	);
}

// 
function recordActionedParticipantId() {
	participantId = $(event.target).parent().find("span.hidden").text();
}


	
	/////////////////////////////////////////////
// refreshing the participants and applications


function refreshApplicationsAndPartipants() {

	var knownParticipantUsernames = computedKnownParticipantUsernames();
	var knownApplicationUsernames = computedKnownApplicationUsernames();

	refreshRemovedApplicationsAndParticipants(knownParticipantUsernames, knownApplicationUsernames);
	refreshAddedApplicationsAndParticipants(knownParticipantUsernames, knownApplicationUsernames);
}


function refreshRemovedApplicationsAndParticipants(knownParticipantUsernames, knownApplicationUsernames) {

	partJson = JSON.stringify(knownParticipantUsernames);
	appJson = JSON.stringify(knownApplicationUsernames);
	
	$.post(determineRemovedParticipantsAndApplications(
			{projectId: projectId, knownParticipantUsernames: partJson, knownApplicationUsernames: appJson}
			), 
			function(data) {
				removeObsoletParticipants(data.confirmedParticipants);
				removeObsoletApplications(data.unconfirmedParticipants);
			}
		);
}

function refreshAddedApplicationsAndParticipants(knownParticipantUsernames, knownApplicationUsernames) {
	$.post(determineAddedParticipantsAndApplications(
			{projectId: projectId, knownParticipantUsernames: partJson, knownApplicationUsernames: appJson}
		), 
		function(htmlData) {
			addNewParticipantsAndApplications(htmlData);
		}
	);
	
}

function computedKnownParticipantUsernames() {
	var knownParticipantUsernames = [];
	$("#projectParticipants .oneUserSummary .oneLinerContentContainer .hiddenUserName").each(
			function(index, element) {
				knownParticipantUsernames.push($(element).text());
			}
	);
	return knownParticipantUsernames;
}

function computedKnownApplicationUsernames() {
	var knownApplicationUsernames = [];
	$("#projectApplications .oneLinerContentContainer .hiddenUserName").each(
			function(index, element) {
				knownApplicationUsernames.push($(element).text());
			}
	);
	return knownApplicationUsernames;
}


function removeObsoletParticipants(removedConfirmedParticipants) {
	for (var key in removedConfirmedParticipants) {
		var divToRemove = $("#participant" + removedConfirmedParticipants[key]);
		$(divToRemove).slideUp(250);
	}
}

function removeObsoletApplications(removedUnconfirmedParticipants) {
	for (var key in removedUnconfirmedParticipants) {
		var divToRemove = $("#application" + removedUnconfirmedParticipants[key] );
		$(divToRemove).slideUp(250);
	}
}


function addNewParticipantsAndApplications(htmlData) {
	$("#numberOfParticipantSpan").text($(htmlData).find("#numberOfParticipantSpan").text());
	$("#numberOfApplicantsSpan").text($(htmlData).find("#numberOfApplicantsSpan").text());
	
	$(htmlData).find("#projectParticipants .oneUserSummary").each(
			function(index, element){
				$(element).addClass("hidden");
				$("#projectParticipants .toggleContainer").append(element);
				$("#projectParticipants .toggleContainer .oneUserSummary.hidden").slideDown(250);
			}
		);

	if ($(htmlData).find("#projectApplications") == undefined || $(htmlData).find("#projectApplications").length == 0) {
		$("#projectApplications").remove();
	} else {
		$(htmlData).find("#projectApplications .oneUserSummary").each(
			function(index, element){
				$("#projectApplications .toggleContainer").append(element);
			}
		);
	}
}


function updateParticipantOneLineContainer(participantId) {
	// oneLinerContentContainer
	
	$.post(retrieveUpdatedParticipantContentData(
			{projectId: projectId, participant:participantId}
			), 
			function(htmlData) {
				$("#participant"+participantId).find(".oneLinerContentContainer").replaceWith($(htmlData).find(".oneLinerContentContainer"));
			}
		);
}


