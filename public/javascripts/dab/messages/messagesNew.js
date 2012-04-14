$(document).ready(function() {
	new dabMessagesNewLib.MessagesNewController();
});

var dabMessagesNewLib = {
	
	MessagesNewController: function () {
		
		this.messagesNewModel = new dabMessagesNewLib.MessagesNewModel();
		
		this.init = function() {
			var self = this;
			
			// knockout bindings
			ko.applyBindings(this.messagesNewModel, $("#messagesContainer")[0]);

			// click on "add user from my contacts"
			var userListPopup = new dabUserPopupLib.UserListPopup(this, $("#messagesContainer .usersPopupList"), profileMessagesChooseFromMyContactsPopupTitle, this.whenTheRecipientIsChosen);
			this.messagesNewModel.updatenumberOfActiveContacts(userListPopup.countTotalNumberOfPopupUser());
			
			$("#choooseFromMyContactsLink").click(function() {
				if (self.messagesNewModel.isChooseLinkActive()) {
					userListPopup.open();
				}
			});
			
			$("#messagesTo").focus();

		};
		
		this.whenTheRecipientIsChosen = function(self, username) {
			self.messagesNewModel.updateRecipient(username)
		};
		
		this.init();
	},
	
	
	MessagesNewModel : function() {
		var self = this;
		
		this.messageRecipient = ko.observable($("#messagesTo").val());
		this.numberOfActiveContacts = ko.observable();
		
		this.isChooseLinkActive = ko.computed(function() {
			return self.numberOfActiveContacts() > 0 && (self.messageRecipient() == "" || self.messageRecipient() == undefined);
		});
		
		this.updateRecipient = function(username) {
			this.messageRecipient(username);
		};

		this.updatenumberOfActiveContacts = function(number) {
			this.numberOfActiveContacts(number);
		};
		
	},
};