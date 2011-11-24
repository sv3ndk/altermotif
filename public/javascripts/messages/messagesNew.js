var okLabelValue;
var cancelLabelValue;
var numberOfActiveContactsValue;

function init(okLabel, cancelLabel, numberOfActiveContacts) {
	okLabelValue = okLabel;
	cancelLabelValue = cancelLabel;
	numberOfActiveContactsValue = numberOfActiveContacts;

	updateChooseContactLinkState();
	initchooseMessageRecipientPopup();

}

function initchooseMessageRecipientPopup() {

	$("#messagesNewForm\\:messagesTo").bind("change", function() {
		updateChooseContactLinkState();
	});

	$("#messagesNewForm\\:chooseFromMyContacts").click(function() {
		if ($("#messagesNewForm\\:chooseFromMyContacts").hasClass("dabLinkLSpaced")) {
			$("#chooseFromMyContacts").dialog("open");
		}
	});

	if (numberOfActiveContactsValue == 0) {
		popupHeight = 150;
	} else if (numberOfActiveContactsValue < 4) {
		popupHeight = 50 + numberOfActiveContactsValue * 100;
	} else {
		popupHeight = 435;
	}

	$("#chooseFromMyContacts").dialog({
		autoOpen : false,
		width : 550,
		height : popupHeight,
		modal : true
	});

	$(".oneContactPopupLine").click(function(event) {
		var username = $(event.target).find(".contactUserName").text();
		if (username == null || username == "") {
			username = $(event.target).parent().find(".contactUserName").text();
		}
		$("#messagesNewForm\\:messagesTo").val(username);
		updateChooseContactLinkState();
		$("#chooseFromMyContacts").dialog("close");
	});

}

function updateChooseContactLinkState() {
	if (numberOfActiveContactsValue > 0 && ($("#messagesNewForm\\:messagesTo").val() == "" || $("#messagesNewForm\\:messagesTo").val() == null)) {
		$("#messagesNewForm\\:chooseFromMyContacts").removeClass("dabLinkLSpacedDisabled");
		$("#messagesNewForm\\:chooseFromMyContacts").addClass("dabLinkLSpaced");
	} else {
		$("#messagesNewForm\\:chooseFromMyContacts").removeClass("dabLinkLSpaced");
		$("#messagesNewForm\\:chooseFromMyContacts").addClass("dabLinkLSpacedDisabled");
	}
}