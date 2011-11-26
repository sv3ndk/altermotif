var okLabelValue;
var cancelLabelValue;
var numberOfActiveContactsValue;

function init() {
	updateChooseContactLinkState();
	initchooseMessageRecipientPopup();

}

function initchooseMessageRecipientPopup() {

	$("#messagesTo").bind("change", function() {
		updateChooseContactLinkState();
	});

	$("#choooseFromMyContactsLink").click(function() {
		if ($("#choooseFromMyContactsLink").hasClass("dabLinkLSpaced")) {
			$("#chooseFromMyContactsPopup").dialog("open");
		}
	});

	if (numberOfActiveContactsValue == 0) {
		popupHeight = 150;
	} else if (numberOfActiveContactsValue < 4) {
		popupHeight = 50 + numberOfActiveContactsValue * 100;
	} else {
		popupHeight = 435;
	}

	$("#chooseFromMyContactsPopup").dialog({
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
		$("#messagesTo").val(username);
		updateChooseContactLinkState();
		$("#chooseFromMyContactsPopup").dialog("close");
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