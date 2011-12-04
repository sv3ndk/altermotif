function init() {

	// this is present in languages.js
	initAllPossibleLanguagesMap();
	insertLangugeName();
	initPhotoGallery();
	initApplicationMecanics();
	
	
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



//////////////////////////////////
// project participantions

function initApplicationMecanics() {
	
	initViewParticipationMotivationText();
	initRejectApplication();
	
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

	
var applicantId;
var applicationDiv;
function initRejectApplication() {

	$("span.rejectApplication").click(function(event) {
		applicantId = $(event.target).next().text();
		applicationDiv = $(event.target).parent();
		$("#confirmRejectApplicationtDialog").dialog("open");		
	});
	
	$("#confirmRejectApplicationtDialog").dialog({
		autoOpen : false,
		width: 400,
		"buttons" : [ {
			text : okLabelValue,
			click : function(event) {
				
				$.post(rejectApplicationToProject(
						{projectId: projectId, applicant: applicantId}
						), 
						function(data) {
							applicationDiv.slideUp();
							$("#confirmRejectApplicationtDialog").dialog("close");
						}
				);
				
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
