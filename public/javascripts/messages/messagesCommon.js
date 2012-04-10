var dabMessagesLib = {
		
	/////////////////////
	// controller user for inbox, outbox and deleted messages
	// the logic is extactly the same is all pages: next/previous/delete/read/refresh...
	MessageController : function() {
		
		this.messageDataModel = new dabMessagesLib.MessageDataModel();
		
		// this is initialized from html (and from Java before that)
		this.currentPage = currentPage;
		var self = this;
		
		this.init = function() {
			
			ko.applyBindings(this.messageDataModel, $(".messagesTable")[0]);
			
			// clicks on a message
			$(".messagesTable").on("click", '.inboxRow, .inboxRowUnread', function(event) {
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
			
			$(".messagesTable").on("change", "tr.inboxRowTr input.msgCb", function(event) {
				self.whenUserClickOnRowCheckBox(event);
			});
			
			$("#masterCheckbox").click(function(event) {
				if ($("#masterCheckbox").attr("checked") == undefined) {
					$(".messagesTable tr.inboxRowTr input.msgCb").removeAttr("checked");
					self.messageDataModel.toggleAllCheckBoxes(false);
				} else {
					$(".messagesTable tr.inboxRowTr input.msgCb").attr("checked", "checked");
					self.messageDataModel.toggleAllCheckBoxes(true);
				}	
			});
			
			$("#deleteSelectedMessagesLink").click(function() {
				if (self.messageDataModel.isAtLeastOneMessageSelected()) {
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
			this.messageDataModel.clear();
			_.each(messagePage.messages, function(message) {
				self.messageDataModel.addOneMessage(message);
			}); 
			this.messageDataModel.previousPageExists(messagePage.previousPageExists);
			this.messageDataModel.nextPageExists(messagePage.nextPageExists);
		};
		
		this.whenUserClicksPrevious = function () {
			if (this.currentPage > 0 && this.messageDataModel.previousPageExists()) {
				this.currentPage--;
				this.loadCurrentMessagePage();
			}
		};
		
		this.whenUserClicksNext = function () {
			if (this.messageDataModel.nextPageExists()) {
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
			
			var messageIdToBeMarkedAsRead = this.messageDataModel.setDisplayedMessage(this.determineClickedRowIndex(event));
			if (messageIdToBeMarkedAsRead != null && markAsReadAction) {
				// markAsReadAction is null for outbout and read message pages: we do not "mark" messages there
				$.post(markAsReadAction({
					messageId : messageIdToBeMarkedAsRead
				}));
			}
		};
		
		this.whenUserClickOnRowCheckBox = function (event) {
			this.messageDataModel.toggleCheckBox(this.determineClickedRowIndex(event));
		};
		
		this.replyTo = function() {
			if (this.messageDataModel.messageReactionEnabled() && this.messageDataModel.displayedMessage.id() != undefined) {
				$("#hiddenReplyToForm input.hiddenSubmit").val(this.messageDataModel.displayedMessage.id());
				$("#hiddenReplyToForm form").submit();
			}
		};

		this.forwardMessage = function () {
			if (this.messageDataModel.messageReactionEnabled() && this.messageDataModel.displayedMessage.id() != undefined) {
				$("#hiddenForwardForm input.hiddenSubmit").val(this.messageDataModel.displayedMessage.id());
				$("#hiddenForwardForm form").submit();
			}
		};
		
		
		this.deleteSelectedMessages = function() {
			if (self.messageDataModel.isAtLeastOneMessageSelected()) {
				$.post(
						deleteMessageAction({messageIds: JSON.stringify(self.messageDataModel.getAllSelectedMessageIds())}), 
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
	
	
	
	
	
	
	
	////////////////////////////////////////////
	////////////////////////////////////////////
		
	MessageDataModel : function() {
		
		this.messages = ko.observableArray();
		this.previousPageExists = ko.observable();
		this.nextPageExists = ko.observable();
		
		this.messageReactionEnabled = ko.observable(); 
		this.displayedMessage = new dabMessagesLib.Message(null);
		
		var self = this;
		
		this.clear = function() {
			this.messages.splice(0, this.messages().length);
			this.displayedMessage.clear();
			this.messageReactionEnabled(false);
		};
		
		this.areAllMessagesSelected = ko.computed(function() {
			return _.all(self.messages(), function(message) {
				return message.isChecked();
			});
		});
		
		this.isAtLeastOneMessageSelected  = ko.computed(function() {
			return _.any(self.messages(), function(message) {
				return message.isChecked();
			});
		});
		
		this.getAllSelectedMessageIds = function() {
			return _(self.messages())
						.chain()
						.filter(function(message) {return message.isChecked();})
						.map(function(message) {return message.id(); })
						.value();
		};
		
		this.toggleAllCheckBoxes = function(value) {
			_.each(self.messages(), function(message) {
				message.isChecked(value);
			});
		};
		
		this.toggleCheckBox = function (checkboxIndex) {
			var previousValue = this.messages()[checkboxIndex].isChecked();
			this.messages()[checkboxIndex].isChecked(!previousValue);
		};
		
		this.addOneMessage = function(message) {
			this.messages.push(new dabMessagesLib.Message(message));
		};
		
		this.setDisplayedMessage = function (messageIndex) {
			var clickedMessage = this.messages()[messageIndex];
			
			if (clickedMessage != null) {
				this.messageReactionEnabled(true);
				this.displayedMessage.apply(clickedMessage);
				
				if (!clickedMessage.isRead()) {
					clickedMessage.isRead(true);
					return clickedMessage.id();
				} else {
					return null;
				}
			} else {
				return null;
			}
		};
	},
	
	
	////////////////////////////////////////
	///////////////////////////////////////

	
	Message: function(message) {
		
		this.id = ko.observable(message != null ? message.id : "");
		this.fromUser = ko.observable(message != null ? message.fromUser : { isProfileActive : false});
		this.toUser = ko.observable(message != null ? message.toUser : { isProfileActive : false});;
		this.subject = ko.observable(message != null ? message.subject : ""); 
		this.creationDate = ko.observable(message != null ? message.creationDate : "");    
		this.isRead = ko.observable(message != null ? message.read : true);
		this.content = ko.observable(message != null ? message.content : "");
		
		this.isChecked = ko.observable(false);
		
		this.apply = function(copied){
			this.id(copied.id());
			this.fromUser(copied.fromUser());
			this.toUser(copied.fromUser());
			this.subject(copied.subject());
			this.creationDate(copied.creationDate());
			this.isRead(copied.isRead());
			this.content(copied.content());
		};
		
		this.clear = function() {
			this.id("");
			this.fromUser("");
			this.toUser("");
			this.subject("");
			this.creationDate("");
			this.isRead(true);
			this.content("");
		};
	},
};