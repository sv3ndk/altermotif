function init(hasPendingInvitation) {
	registerClickOnUnreadInbox();

	// initialization of the notification box
	$("#pendingInvitationNotificationContainer").notify();
	
	delayedDisplayPendingInvitationNotification(hasPendingInvitation);
}

function registerClickOnUnreadInbox() {

	$('.inboxRowUnread').click(function(event) {
		// id of the hidden field containing the message content

		// if the user clicked on the link, we do not want to view the message
		// nor to mark it as read
		var clickedId5 = event.target.id.substring(0, 4);
		if (clickedId5 != "link") {
			var messageIdId = "i_" + event.target.id.substring(2);
			var messageIdEl = $("body").find('#' + messageIdId);

			$("#hiddenNavigateToMessageForm\\:hiddenMessageId").val(messageIdEl.text());
			$("#hiddenNavigateToMessageForm\\:hiddenLink").click();

		}
	});
}

function delayedDisplayPendingInvitationNotification(hasPendingInvitation) {

	if (hasPendingInvitation == "true") {
		setTimeout(function() {
			displayPendingInvitationNotification();
		}, 500);
	}

}

function displayPendingInvitationNotification() {

	$("#pendingInvitationNotificationContainer").notify("create", {}, {
		expires : false,
		speed : 750
	});
}
