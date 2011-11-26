function init(hasPendingInvitation) {
	registerClickOnUnreadInbox();

	// initialization of the notification box
	$("#pendingInvitationNotificationContainer").notify();
	
	delayedDisplayPendingInvitationNotification(hasPendingInvitation);
}

function registerClickOnUnreadInbox() {

	
	$('.inboxRowUnread').click(function(event) {

		var clickedRow = $(event.target);
		
		while (!clickedRow.hasClass("inboxRowTr")) {
			clickedRow = clickedRow.parent();
		}
		
		$("#hiddenRedirectToInboxForm #messageId").val(clickedRow.find("span.hidden").text())
		$("#hiddenRedirectToInboxForm form").submit();
		
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
