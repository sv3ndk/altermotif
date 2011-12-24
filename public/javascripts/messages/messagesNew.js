var okLabelValue;
var cancelLabelValue;
var numberOfActiveContactsValue;

function init() {
	updateChooseContactLinkState();
	initchooseMessageRecipientPopup();
	
	// this is in usersPopupList.js
	initUsersPopupList(whenTheRecipientIsChosen);
}

function initchooseMessageRecipientPopup() {

	$("#messagesTo").bind("change", function() {
		updateChooseContactLinkState();
	});

	$("#choooseFromMyContactsLink").click(function() {
		if ($("#choooseFromMyContactsLink").hasClass("dabLinkLSpaced")) {
			openUsersPopupList();
		}
	});
}

function updateChooseContactLinkState() {
	if (numberOfActiveContactsValue > 0 && ($("#messagesTo").val() == "" || $("#messagesTo").val() == null)) {
		$("#choooseFromMyContactsLink").removeClass("dabLinkLSpacedDisabled");
		$("#choooseFromMyContactsLink").addClass("dabLinkLSpaced");
	} else {
		$("#choooseFromMyContactsLink").removeClass("dabLinkLSpaced");
		$("#choooseFromMyContactsLink").addClass("dabLinkLSpacedDisabled");
	}
}


// this is called back from the user popup list handler, after the user has chosen a contact (see usersPopupList.js)
function whenTheRecipientIsChosen(username) {
	$("#messagesTo").val(username);
	updateChooseContactLinkState();
	closeUsersPopupList();
}