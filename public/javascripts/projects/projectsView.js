function init() {

	// this is present in languages.js
	initAllPossibleLanguagesMap();
	
	initToolBoxLinks();
	
	insertLangugeName();
	initPhotoGallery();
	initApplicationMecanics();
	initParticipantsMecanics();
	refreshApplyLinkVisibility();
	
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


////////////////////////////////////////
// cancel and terminate projects


function initToolBoxLinks() {

	// this init is called again any time the list of participants changes => first unbound the previous listener before binding the new one
	$("#cancelProjectLink").off("click");
	if (isCancelProjectLinkEffective) {
		askAndAct_On("#cancelProjectLink", "", confirmCancelProjectText, whenUserConfirmsCancelCancelProject);
	} else {
		initClickAndDisplayMessage("#cancelProjectLink", "", cannotCancelProjectExplanationText);
	}
	
	askAndAct_On("#terminateProjectLink", "", confirmTerminateProjectText, whenUserConfirmsTerminateProject);
	askAndAct_On("#undoTerminateProjectLink", "", confirmRestartProjectText, whenUserConfirmsRestartProject);
}

function whenUserConfirmsCancelCancelProject() {
	setConfirmationFunction(onConfirmCancelCancelProject);
}

function onConfirmCancelCancelProject() {
	$("#hiddenCancelProjectform form input").val(projectId);
	$("#hiddenCancelProjectform form").submit();
}


function whenUserConfirmsTerminateProject() {
	setConfirmationFunction(onConfirmTerminateProject);
}

function onConfirmTerminateProject() {
	$("#hiddenTerminateProjectform form input").val(projectId);
	$("#hiddenTerminateProjectform form").submit();
}

function whenUserConfirmsRestartProject() {
	setConfirmationFunction(onConfirmRestartProject);
}

function onConfirmRestartProject() {
	$("#hiddenRestartProjectform form input").val(projectId);
	$("#hiddenRestartProjectform form").submit();
}

//////////////////////////////////
// project participantions

function initApplicationMecanics() {

	// init cancel application
	askAndAct_On("#cancelApplicationToProjectLink", "", confirmCancelApplicationText, whenUserConfirmsCancelApplication);

	initViewParticipationMotivationText();
	initAcceptRejectAppliction();
	
	// init apply to project
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
}

function whenUserConfirmsCancelApplication() {
	setConfirmationFunction(onConfirmCancelApplication);
}

function onConfirmCancelApplication() {
	$.post(cancelApplicationToProject(
			{projectId: projectId}
			), 
			function(data) {
				setTimeout(function() {
					isApplyLinkVisible = true;
					isCancelApplicationLinkVisible = false;
					refreshApplyLinkVisibility();
					closeConfirmationDialog();
				}, 350);
			}
	);
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
	
var applicationDiv;

function initAcceptRejectAppliction() {
	// this is defined in simpleActions.js
	askAndAct_On("#projectApplications", "span.acceptApplication", confirmAcceptApplicationText, whenUserConfirmsAcceptApplication);
	askAndAct_On("#projectApplications", "span.rejectApplication", confirmRejectApplicationText, whenUserConfirmsRejectApplication);
}

function whenUserConfirmsAcceptApplication(event) {
	recordActionedParticipantId(event);
	setConfirmationFunction(onConfirmAcceptApplication);
}
	
function onConfirmAcceptApplication() {
	$.post(acceptApplicationToProject(
			{projectId: projectId, applicant: participantId}
		), 
		function(data) {
			setTimeout(function() {
				refreshApplicationsAndPartipants();
				closeConfirmationDialog();
			},400);
		}
	);
}
	
function whenUserConfirmsRejectApplication(event) {
	recordActionedParticipantId(event);
	setConfirmationFunction(onConfirmRejectApplication);
}	
				
function onConfirmRejectApplication() {
	$.post(rejectApplicationToProject(
		{projectId: projectId, applicant: participantId}
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
	recordActionedParticipantId(event);
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
function whenUserConfirmsLeavesProject(event) {
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
function whenUserConfirmsMakeAdmin(event) {
	recordActionedParticipantId(event);
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
function whenUserConfirmsMakeMember(event) {
	recordActionedParticipantId(event);
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
function whenUserConfirmsGiveOwnership(event) {
	recordActionedParticipantId(event);
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
function whenUserConfirmsCancelGiveOwnership(event) {
	recordActionedParticipantId(event);
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

function whenUserAcceptsOwnership(event) {
	recordActionedParticipantId(event);
	setConfirmationFunction(onAcceptOwnership);
}

function onAcceptOwnership() {
	// this must be a regular non-AJAX form POST: we must refresh the whole page because his rights have changed a lot now (and I am lazy...)
	$("#hiddenAcceptOwnershipform form input").val(projectId);
	$("#hiddenAcceptOwnershipform form").submit();
}


function whenUserRefusesOwnership(event) {
	recordActionedParticipantId(event);
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

// this is also used to record applicant id (ugly, I know...)
function recordActionedParticipantId(event) {
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
			isCancelProjectLinkEffective = data.isCancelProjectLinkEffective;
			initToolBoxLinks();
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
	$.post(retrieveUpdatedParticipantContentData(
		{projectId: projectId, participant:participantId}
		), 
		function(htmlData) {
			$("#participant"+participantId).find(".oneLinerContentContainer").replaceWith($(htmlData).find(".oneLinerContentContainer"));
		}
	);
}