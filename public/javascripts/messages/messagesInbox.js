$(document).ready(function() {
	new dabMessageInboxLib.MessageInboxController();
});

var dabMessageInboxLib =  {
	
	MessageInboxController : function() {
		
		this.messageInboxDataModel = new dabMessagesLib.MessageDataModel();
		
		// this is initialized from html (and from Java before that)
		this.currentPage = currentPage;
		var self = this;
		
		this.init = function() {
			
			ko.applyBindings(this.messageInboxDataModel, $("#messagesInboxTable")[0]);
			
			// clicks on a message
			$("#messagesInboxTable").on("click", '.inboxRow, .inboxRowUnread', function(event) {
				self.whenUserClicksOnMessage(event);
			});
			
			$("#replyToLink").click( function() {
				self.replyTo();
			});
					
			$("#forwardLink").click(function() {
				self.forwardMessage();
			});
			
			$("#messagesPreviousLink").click(function() {
				self.whenUserClicksPrevious();
			});

			$("#messagesNextLink").click(function() {
				self.whenUserClicksNext();
			});
			
			$("#messagesInboxTable").on("change", "tr.inboxRowTr input.msgCb", function(event) {
				self.whenUserClickOnRowCheckBox(event);
			});
			
			$("#masterCheckbox").click(function(event) {
				if ($("#masterCheckbox").attr("checked") == undefined) {
					$("#messagesInboxTable tr.inboxRowTr input.msgCb").removeAttr("checked");
					self.messageInboxDataModel.toggleAllCheckBoxes(false);
				} else {
					$("#messagesInboxTable tr.inboxRowTr input.msgCb").attr("checked", "checked");
					self.messageInboxDataModel.toggleAllCheckBoxes(true);
				}	
			});
			
			$("#messageDeleteSelected").click(function() {
				if (self.messageInboxDataModel.isAtLeastOneMessageSelected()) {
					$("#confirmDeleteMessages").dialog("open");
				}
			});

			this.initConfirmDeleteDialog();
			this.loadCurrentMessagePage();
		};
		
		// calls the back-end for new message and updates the model accordingly 
		this.loadCurrentMessagePage = function () {
			$.post(loadOnePageAction({
				pageNumber : self.currentPage
			}),
			function(messagePage) {
				self.parseMessagesPage(messagePage);
			});
		};
		
		this.parseMessagesPage = function (messagePage){
			this.messageInboxDataModel.clear();
			_.each(messagePage.messages, function(message) {
				self.messageInboxDataModel.addOneMessage(message);
			}); 
			this.messageInboxDataModel.previousPageExists(messagePage.previousPageExists);
			this.messageInboxDataModel.nextPageExists(messagePage.nextPageExists);
		};
		
		this.whenUserClicksPrevious = function () {
			if (this.currentPage > 0 && this.messageInboxDataModel.previousPageExists()) {
				this.currentPage--;
				this.loadCurrentMessagePage();
			}
		};
		
		this.whenUserClicksNext = function () {
			if (this.messageInboxDataModel.nextPageExists()) {
				this.currentPage++;
				this.loadCurrentMessagePage();
			}
		};
		
		this.whenUserClicksOnMessage = function(event) {
			var eventTarget = $(event.target); 
			
			// if we clicked on the user name link or on the checkbox: no need to refresh the message content
			if (eventTarget.hasClass("dabLink") || eventTarget.attr("type") == "checkbox") {
				return;
			}
			
			var messageIdToBeMarkedAsRead = this.messageInboxDataModel.setDisplayedMessage(this.determineClickedRowIndex(event));
			if (messageIdToBeMarkedAsRead != null) {
				$.post(markAsReadAction({
					messageId : messageIdToBeMarkedAsRead
				}));
			}
		};
		
		this.whenUserClickOnRowCheckBox = function (event) {
			this.messageInboxDataModel.toggleCheckBox(this.determineClickedRowIndex(event));
		};
		
		this.replyTo = function() {
			if (this.messageInboxDataModel.messageReactionEnabled() && this.messageInboxDataModel.displayedMessage.id() != undefined) {
				$("#hiddenReplyToForm input.hiddenSubmit").val(this.messageInboxDataModel.displayedMessage.id());
				$("#hiddenReplyToForm form").submit();
			}
		};

		this.forwardMessage = function () {
			if (this.messageInboxDataModel.messageReactionEnabled() && this.messageInboxDataModel.displayedMessage.id() != undefined) {
				$("#hiddenForwardForm input.hiddenSubmit").val(this.messageInboxDataModel.displayedMessage.id());
				$("#hiddenForwardForm form").submit();
			}
		};
		
		
		this.deleteSelectedMessages = function() {
			if (self.messageInboxDataModel.isAtLeastOneMessageSelected()) {
				$.post(
						deleteMessageAction({messageIds: JSON.stringify(self.messageInboxDataModel.getAllSelectedMessageIds())}), 
						function(response) {
							self.loadCurrentMessagePage();
							$("#confirmDeleteMessages").dialog("close");
						}
					);
			} else {
				$("#confirmDeleteMessages").dialog("close");
			}
		}
		
		
		this.initConfirmDeleteDialog = function(){
			
			$("#confirmDeleteMessages").dialog({
				autoOpen : false,
				modal : true,
				"buttons" : [ {
					text : okLabelValue,
					click : self.deleteSelectedMessages
				}, {
					text : cancelLabelValue,
					click : function() {
						$(this).dialog("close");
					}
				}]
			});
		};

		/////////////////////
		
		this.determineClickedRowIndex = function(event) {
			var clickedRow = $(event.target);
			while (!clickedRow.is("tr")) {
				clickedRow = clickedRow.parent();
			}
			return clickedRow.index();
		}

		this.init();
	},
};